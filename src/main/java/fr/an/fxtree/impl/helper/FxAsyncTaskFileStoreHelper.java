package fr.an.fxtree.impl.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.impl.helper.FxPendingJobsFileStoreHelper.PendingEntry;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

/**
 * helper class to store asynchronous tasks results by ids in key-value store,<br/> 
 * and reload previous result instead of re-evaluating/relaunching task.
 * 
 * <p>
 * typical scenario:
 * <PRE>
 *                        +----------------------------+
 *    --- eval task1  --> + FxAsyncTaskFileStoreHelper |   ---> Launch Task1 thread/polling thread          
 *                        +----------------------------+
 *       <-- immediate return --
 *          { taskId:.., status: "running", 
 *            startTime:.. 
 *          }
 *           
 *    ... long after ...
 *                                                     <--- ... finished task1 
 *                                                          store  task1 result = {..}
 *    
 *    --- (re)eval task1 -->          
 *      <-- { taskId:.., status:"finished"/"failed",
 *            startTime:.., endTime:..
 *            result: { .. }
 *          }
 *              
 * </PRE>
 * 
 *  thread-safety: thread-safe, protected by <code>lock</code> + <code>pendings</code>
 */
public class FxAsyncTaskFileStoreHelper {

    private static final Logger LOG = LoggerFactory.getLogger(FxAsyncTaskFileStoreHelper.class);
    
    public static enum TaskStatus {
        RUNNING, FINISHED, FAILED
    }
    
    public static class AsyncTaskData {        
        
        protected String taskId;
        protected TaskStatus status;
        protected Date startTime;
        protected Date endTime;
        protected FxNode result;
        protected FxNode errorDetails;
        protected FxNode updateDetails;
        
        public String getTaskId() {
            return taskId;
        }
        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }
        public TaskStatus getStatus() {
            return status;
        }
        public void setStatus(TaskStatus status) {
            this.status = status;
        }
        public Date getStartTime() {
            return startTime;
        }
        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }
        public Date getEndTime() {
            return endTime;
        }
        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }
        public FxNode getResult() {
            return result;
        }
        public void setResult(FxNode result) {
            this.result = result;
        }
        public FxNode getErrorDetails() {
            return errorDetails;
        }
        public void setErrorDetails(FxNode errorDetails) {
            this.errorDetails = errorDetails;
        }
        public FxNode getUpdateDetails() {
            return updateDetails;
        }
        public void setUpdateDetails(FxNode updateDetails) {
            this.updateDetails = updateDetails;
        }
        @Override
        public String toString() {
            return "AsyncTaskData [taskId=" + taskId + ", status=" + status + ", startTime=" + startTime + ", endTime=" + endTime + ", result="
                + result + ", errorDetails=" + errorDetails + ", updateDetails=" + updateDetails + "]";
        }
        
        
    }
    
    private Object lock = new Object();
    
    private FxKeyNodeFileStore taskResultStore;
    
    private FxPendingJobsFileStoreHelper pendingTaskStore;
    
    // ------------------------------------------------------------------------

    public FxAsyncTaskFileStoreHelper(FxKeyNodeFileStore taskResultStore, FxPendingJobsFileStoreHelper pendingTaskStore) {
        this.taskResultStore = taskResultStore;
        this.pendingTaskStore = pendingTaskStore;
    }

    // ------------------------------------------------------------------------


    public List<String> getPendingTaskIds() {
        return pendingTaskStore.listPendings();
    }
    
    public List<AsyncTaskData> getPendingTasks() {
        List<AsyncTaskData> res = new ArrayList<>(); 
        List<String> ids = pendingTaskStore.listPendings();
        for(String id : ids) {
            PendingEntry pendingTask = pendingTaskStore.getPendingValueCopyOrNull(id);
            if (pendingTask != null) {
                AsyncTaskData taskData = treeToTaskData(pendingTask.pendingData);
                res.add(taskData);
            }
        }
        return res;
    }

    public Set<String> listTaskResultIds() {
        return taskResultStore.keySet();
    }

    public List<AsyncTaskData> listTaskResults() {
        List<AsyncTaskData> res = new ArrayList<>();
        Map<String, FxNode> tmpres = taskResultStore.listResultCopies();
        for (FxNode e : tmpres.values()) {
            AsyncTaskData resElt = treeToTaskData(e);
            res.add(resElt);
        }
        return res;
    }
    
    public static interface FxAsyncTaskCallback {
        public void onTaskUpdate(FxNode resultNode);
        public void onTaskFinishedOK(FxNode resultNode);
        public void onTaskFinishedError(FxNode resultNode, FxNode errorDetails);
   }
    
    @FunctionalInterface
    public static interface FxAsyncTaskLauncher {
        public void launchTask(FxAsyncTaskCallback callback);
    }
    
    public AsyncTaskData reloadResultOrLaunchTask(String taskId, FxAsyncTaskLauncher taskLauncher) {
        AsyncTaskData res;
        synchronized (lock) {
            FxNode taskResultNode = taskResultStore.getCopy(taskId);
            if (taskResultNode == null) {
                PendingEntry pendingTask = pendingTaskStore.getPendingValueCopyOrNull(taskId);
                if (pendingTask == null) {
                    AsyncTaskData taskData = new AsyncTaskData();
                    taskData.setTaskId(taskId);
                    taskData.setStartTime(new Date());
                    taskData.setStatus(TaskStatus.RUNNING);
                    FxNode taskDataNode = taskDataToTree(taskData);
                    
                    // insert immediate.. in case launched task return immediatly
                    pendingTask = pendingTaskStore.addPending(taskId, taskDataNode);
                    
                    InnerAsyncTaskCallback callback = new InnerAsyncTaskCallback(taskId);
                    
                    // *** launch new async task ***
                    taskLauncher.launchTask(callback);
                }
                
                PendingEntry pendingTask2 = pendingTaskStore.getPendingValueCopyOrNull(taskId);
                FxNode taskResultNode2;
                if (pendingTask2 != null) {
                    taskResultNode2 = pendingTask2.pendingData;
                } else { 
                    // immediatly finished
                    taskResultNode2 = taskResultStore.getCopy(taskId);
                }
                res = treeToTaskData(taskResultNode2);
            } else {
                // else already executed and finished/failed => use result
                res = treeToTaskData(taskResultNode);
            }
        }
        return res;
    }

    public FxAsyncTaskCallback onRestartServerCreateFxStoreCallackFor(String taskId) {
        return new InnerAsyncTaskCallback(taskId);
    }
    
    
    /**
     * inner callback for updating pendingTask / task result
     */
    protected class InnerAsyncTaskCallback implements FxAsyncTaskCallback {
        private final String taskId;

        protected InnerAsyncTaskCallback(String taskId) {
            this.taskId = taskId;
        }

        public void onTaskUpdate(FxNode resultNode) {
            synchronized (lock) {
                PendingEntry pendingTask = pendingTaskStore.getPendingValueCopyOrNull(taskId);
                if (pendingTask == null) {
                    return; // should not occur?
                }
                
                AsyncTaskData taskData = treeToTaskData(pendingTask.pendingData);
                taskData.result = resultNode;
                FxNode taskDataNode = taskDataToTree(taskData);
                
                pendingTaskStore.updatePending(taskId, taskDataNode);
            }
        }
        
        public void onTaskFinishedOK(FxNode result) {
            synchronized (lock) {
                PendingEntry pendingTask = pendingTaskStore.getPendingValueCopyOrNull(taskId);
                if (pendingTask == null) {
                    // should not occur?!
                    pendingTask = new PendingEntry(taskId, new Date(), null);
                }
                AsyncTaskData taskData = pendingToTaskData(pendingTask);
                taskData.setEndTime(new Date());
                taskData.setStatus(TaskStatus.FINISHED);
                taskData.setResult(result);
                FxNode taskDataNode = taskDataToTree(taskData);
                
                taskResultStore.put(taskId, taskDataNode);
                if (pendingTask != null) {
                    pendingTaskStore.removePending(taskId);
                }
            }
        }
        
        public void onTaskFinishedError(FxNode result, FxNode errorDetails) {
            synchronized (lock) {
                PendingEntry pendingTask = pendingTaskStore.getPendingValueCopyOrNull(taskId);
                if (pendingTask == null) {
                    // shoul not occur?!
                    pendingTask = new PendingEntry(taskId, new Date(), null);
               }
                AsyncTaskData taskData = pendingToTaskData(pendingTask);
                taskData.setEndTime(new Date());
                taskData.setStatus(TaskStatus.FAILED);
                taskData.setResult(result);
                taskData.setErrorDetails(errorDetails);
                FxNode taskDataNode = taskDataToTree(taskData);

                taskResultStore.put(taskId, taskDataNode);
                if (pendingTask != null) {
                    pendingTaskStore.removePending(taskId);
                }
            }
        }
    }
    
    protected AsyncTaskData treeToTaskData(FxNode src) {
        // return FxJsonUtils.treeToValue(AsyncTaskData.class, src); .. error No serializer found for class fr.an.fxtree.impl.model.mem.FxMemNodeFactory..
        AsyncTaskData res = new AsyncTaskData();
        FxObjNode src2 = (FxObjNode) src;
        FxObjValueHelper srcH = new FxObjValueHelper (src2); 
        res.setTaskId(srcH.getString("taskId"));
        res.setStatus(TaskStatus.valueOf(srcH.getString("status")));
        res.setStartTime(srcH.getDateAsLongOrNull("startTime"));
        res.setEndTime(srcH.getDateAsLongOrNull("endTime"));
        res.setResult(FxNodeCopyVisitor.cloneMemNode(src2.get("result")));
        res.setErrorDetails(FxNodeCopyVisitor.cloneMemNode(src2.get("errorDetails")));
        res.setUpdateDetails(FxNodeCopyVisitor.cloneMemNode(src2.get("updateDetails")));
        return res;
    }
    
    protected FxNode taskDataToTree(AsyncTaskData src) {
        // return FxJsonUtils.valueToTree(src);
        FxObjNode res = new FxMemRootDocument().setContentObj();
        res.put("taskId", src.getTaskId());
        res.put("status", src.getStatus().name());
        if (src.getStartTime() != null) {
            res.put("startTime", src.getStartTime().getTime());
        }
        if (src.getEndTime() != null) {
            res.put("endTime", src.getEndTime().getTime());
        }
        if (src.getResult() != null) {
            FxNodeCopyVisitor.copyTo(res.putBuilder("result"), src.getResult()); 
        }
        if (src.getErrorDetails() != null) {
            FxNodeCopyVisitor.copyTo(res.putBuilder("errorDetails"), src.getErrorDetails()); 
        }
        if (src.getUpdateDetails() != null) {
            FxNodeCopyVisitor.copyTo(res.putBuilder("updateDetails"), src.getUpdateDetails()); 
        }
        return res;
    }
    
    protected AsyncTaskData pendingToTaskData(PendingEntry src) {
        if (src == null) {
            return null;
        }
        AsyncTaskData res = treeToTaskData(src.pendingData);
        assert src.id.equals(res.getTaskId());
        if (res.getStatus() != TaskStatus.RUNNING) {
            LOG.warn("Should not occur ... expecting pending task status=RUNNING");
        }
        return res;
    }
}
