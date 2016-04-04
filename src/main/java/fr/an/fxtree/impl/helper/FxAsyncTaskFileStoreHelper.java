package fr.an.fxtree.impl.helper;

import java.util.Date;

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
    
    protected static class TaskNodeFormat { 
        protected String fieldStatus = "status";
        protected String fieldStartTime = "startTime";
        protected String fieldEndTime = "endTime";
        protected String fieldResult = "result";
        protected String fieldErrorDetails = "errorDetails";
        protected String fieldUpdateDetails = "updateDetails";
        
        protected String statusRunning = "running";
        protected String statusFinished = "finished";
        protected String statusFailed = "failed";
    }
    protected TaskNodeFormat fmt = new TaskNodeFormat();
    
    private Object lock = new Object();
    
    private FxKeyNodeFileStore taskResultStore;
    
    private FxPendingJobsFileStoreHelper pendingTaskStore;
    
    // ------------------------------------------------------------------------

    public FxAsyncTaskFileStoreHelper(FxKeyNodeFileStore taskResultStore, FxPendingJobsFileStoreHelper pendingTaskStore) {
        this.taskResultStore = taskResultStore;
        this.pendingTaskStore = pendingTaskStore;
    }

    // ------------------------------------------------------------------------

    public static interface FxAsyncTaskCallback {
//        public void onTaskUpdateDetails(FxNode resultNode, FxNode updateDetails);
        public void onTaskFinishedOK(FxNode resultNode);
        public void onTaskFinishedError(FxNode resultNode, FxNode errorDetails);
    }
    
    @FunctionalInterface
    public static interface FxAsyncTaskLauncher {
        public FxNode launchTask(FxAsyncTaskCallback callback);
    }
    
    public FxObjNode reloadResultOrLaunchTask(String taskId, FxAsyncTaskLauncher taskLauncher) {
        FxObjNode resultNode;
        synchronized (lock) {
            resultNode = (FxObjNode) taskResultStore.getCopy(taskId);
            if (resultNode == null) {
                PendingEntry pendingTask = pendingTaskStore.getPendingValueCopyOrNull(taskId);
                if (pendingTask == null) {
                    InnerAsyncTaskCallback callback = new InnerAsyncTaskCallback(taskId);
                    // *** launch new async task ***
                    FxNode pendingResultData = taskLauncher.launchTask(callback);
                    
                    pendingTask = pendingTaskStore.addPending(taskId, pendingResultData);
                    
                    // return "running" task data
                    resultNode = new FxMemRootDocument().setContentObj();
                    resultNode.putPOJO(fmt.fieldStartTime, pendingTask.startTime);
                    resultNode.put(fmt.fieldStatus, fmt.statusRunning);
                    FxNodeCopyVisitor.copyTo(resultNode.putBuilder(fmt.fieldResult), pendingResultData);
                } else {
                    // can take very long time ... do not wait pending ... immediate return { taskId:.., status: "running", ..startTime:.. }
                    resultNode = new FxMemRootDocument().setContentObj();
                    resultNode.putPOJO(fmt.fieldStartTime, pendingTask.startTime);
                    resultNode.put(fmt.fieldStatus, fmt.statusRunning);
                    FxNodeCopyVisitor.copyTo(resultNode.putBuilder(fmt.fieldResult), pendingTask.pendingData);
                }
            } // else already executed and finished/failed => use result
        }
        return resultNode;
    }

    /**
     * inner callback for updating pendingTask / task result
     */
    protected class InnerAsyncTaskCallback implements FxAsyncTaskCallback {
        private final String taskId;

        protected InnerAsyncTaskCallback(String taskId) {
            this.taskId = taskId;
        }

//        public void onTaskUpdateDetails(FxNode resultNode, FxNode updateDetails) {
//            synchronized (lock) {
//                PendingEntry pendingTask = pendingTaskStore.getPendingValueCopyOrNull(taskId);
//                if (pendingTask == null) {
//                    throw new IllegalStateException();
//                }
//                // pendingTaskStore.updatePendingDetails(taskId, resultNode, updateDetails);
//            }            
//        }
        
        public void onTaskFinishedOK(FxNode result) {
            synchronized (lock) {
                PendingEntry pendingTask = pendingTaskStore.getPendingValueCopyOrNull(taskId);
                if (pendingTask == null) {
                    throw new IllegalStateException();
                }
                
                FxObjNode taskNode = new FxMemRootDocument().setContentObj();
                taskNode.putPOJO(fmt.fieldStartTime, pendingTask.startTime);
                taskNode.putPOJO(fmt.fieldEndTime, new Date());
                taskNode.put(fmt.fieldStatus, fmt.statusFinished);
                FxNodeCopyVisitor.copyTo(taskNode.putBuilder(fmt.fieldResult), result);
                
                taskResultStore.put(taskId, taskNode);
                pendingTaskStore.removePending(taskId);
            }
        }
        
        public void onTaskFinishedError(FxNode result, FxNode errorDetails) {
            synchronized (lock) {
                PendingEntry pendingTask = pendingTaskStore.getPendingValueCopyOrNull(taskId);
                if (pendingTask == null) {
                    throw new IllegalStateException();
                }
                
                FxObjNode taskNode = new FxMemRootDocument().setContentObj();
                taskNode.putPOJO(fmt.fieldStartTime, pendingTask.startTime);
                taskNode.putPOJO(fmt.fieldEndTime, new Date());
                taskNode.put(fmt.fieldStatus, fmt.statusFailed);
                FxNodeCopyVisitor.copyTo(taskNode.putBuilder(fmt.fieldResult), result);
                FxNodeCopyVisitor.copyTo(taskNode.putBuilder(fmt.fieldErrorDetails), errorDetails);
                
                taskResultStore.put(taskId, taskNode);
                pendingTaskStore.removePending(taskId);
            }
        }
    }
    
}
