package fr.an.fxtree.impl.helper;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.an.fxtree.impl.helper.FxAsyncTaskFileStoreHelper.AsyncTaskData;
import fr.an.fxtree.impl.helper.FxAsyncTaskFileStoreHelper.FxAsyncTaskCallback;
import fr.an.fxtree.impl.helper.FxAsyncTaskFileStoreHelper.FxAsyncTaskLauncher;
import fr.an.fxtree.impl.helper.FxAsyncTaskFileStoreHelper.TaskStatus;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxObjNode;

public class FxAsyncTaskFileStoreHelperTest {

    protected FxAsyncTaskFileStoreHelper sut;
    
    @Before
    public void setup() {
        File dir = new File("target/test");
        if (! dir.exists()) {
            dir.mkdirs();
        }
        File taskResultFile = new File(dir, "test-asyncTask-result.yaml");
        if (taskResultFile.exists()) {
            taskResultFile.delete();
        }
        File pendingStoreFile = new File(dir, "test-asyncTask-pending.yaml");
        if (pendingStoreFile.exists()) {
            pendingStoreFile.delete();
        }
        FxKeyNodeFileStore taskResultStore = new FxKeyNodeFileStore(taskResultFile);
        FxKeyNodeFileStore pendingJobsStore = new FxKeyNodeFileStore(pendingStoreFile);
        FxPendingJobsFileStoreHelper pendingTaskStore = new FxPendingJobsFileStoreHelper(pendingJobsStore);
        sut = new FxAsyncTaskFileStoreHelper(taskResultStore, pendingTaskStore);
    }
    
    @Test
    public void testReloadResultOrLaunchTask() {
        // Prepare
        Executor threadExecutor = Executors.newSingleThreadExecutor();
        int taskData = 123;
        AtomicBoolean finishFlag = new AtomicBoolean(false);
        AtomicInteger launchCount = new AtomicInteger(0);
        FxAsyncTaskLauncher taskLauncher = (callback) -> {
            FxObjNode taskRes = new FxMemRootDocument().setContentObj();
            taskRes.put("taskTmpData", taskData);
            callback.onTaskUpdate(taskRes);
            
            threadExecutor.execute(() -> runSlowTask(callback, taskData, finishFlag, launchCount));
        };
        // Perform
        AsyncTaskData res0 = sut.reloadResultOrLaunchTask("task1", taskLauncher);
        if (0 == launchCount.get()) {
            // thread executor launched... must wait few millis
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            Assert.assertEquals(1, launchCount.get());
        }
        AsyncTaskData res1 = sut.reloadResultOrLaunchTask("task1", taskLauncher);
        Assert.assertEquals(1, launchCount.get());
        while(! finishFlag.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        AsyncTaskData res2 = sut.reloadResultOrLaunchTask("task1", taskLauncher);
        Assert.assertEquals(1, launchCount.get());
        // Post-check
        System.out.println("res0:" + res0);
        System.out.println("res1:" + res1);
        System.out.println("res2:" + res2);
        // => 
        //res0:{"startTime":Tue Apr 05 01:02:52 CEST 2016,"status":"running","result":{"taskTmpData":123}}
        //res1:{"startTime":Tue Apr 05 01:02:52 CEST 2016,"status":"running","result":{"taskTmpData":123}}
        //res2:{"startTime":Tue Apr 05 01:02:52 CEST 2016,"endTime":Tue Apr 05 01:02:53 CEST 2016,"status":"finished","result":{"taskFinishedData":"some-data"}}

        Assert.assertEquals(TaskStatus.RUNNING, res0.getStatus());
        Date startTime = res0.getStartTime();
        Assert.assertNotNull(startTime);
        FxObjNode res0Result = (FxObjNode) res0.getResult();
        Assert.assertEquals(123, res0Result.get("taskTmpData").asInt());
        
        // Assert.assertEquals(res0, res1); // same pending ... but task not relaunched twice!

        Assert.assertEquals(TaskStatus.FINISHED, res2.getStatus());
        Assert.assertEquals(startTime, res2.getStartTime());
        Assert.assertNotNull(res2.getEndTime());
        FxObjNode res2Result = (FxObjNode) res2.getResult();
        Assert.assertEquals("some-data", res2Result.get("taskFinishedData").asText());
    }
    
    protected void runSlowTask(FxAsyncTaskCallback callback, int taskData, AtomicBoolean finishFlag, AtomicInteger launchCount) {
        launchCount.incrementAndGet();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
        FxObjNode taskRes = new FxMemRootDocument().setContentObj();
        taskRes.put("taskFinishedData", "some-data");
        callback.onTaskFinishedOK(taskRes);
        
        finishFlag.set(true);
    }
}
