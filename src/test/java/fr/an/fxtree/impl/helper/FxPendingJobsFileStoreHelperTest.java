package fr.an.fxtree.impl.helper;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import fr.an.fxtree.impl.helper.FxPendingJobsFileStoreHelper.PendingEntry;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.impl.util.FxNodeAssert;
import fr.an.fxtree.model.FxObjNode;

public class FxPendingJobsFileStoreHelperTest {

    protected FxPendingJobsFileStoreHelper sut;
    protected FxKeyNodeFileStore pendingStore;
    private static final FxSourceLoc TST_loc = FxSourceLoc.inMem();

    @Before
    public void setup() {
        File dir = new File("target/test");
        if (! dir.exists()) {
            dir.mkdirs();
        }
        File storeFile = new File(dir, "test-PendingJobsFileStore.yaml");
        pendingStore = new FxKeyNodeFileStore(storeFile);
        sut = new FxPendingJobsFileStoreHelper(pendingStore);
    }

    @Test
    public void testAddPending_remove_wait() throws InterruptedException {
        FxObjNode objNode = new FxMemRootDocument(TST_loc).setContentObj(TST_loc);

        FxObjNode job1 = objNode.putObj("job1", TST_loc);
        job1.put("id", 123, TST_loc);
        sut.addPending("job1", job1);

        Assert.assertTrue(sut.isPending("job1"));
        Assert.assertFalse(sut.isPending("unknown-job"));

        List<String> pendings = sut.listPendings();
        Assert.assertEquals(ImmutableList.of("job1"), pendings);

        PendingEntry job1Entry = sut.getPendingValueCopyOrNull("job1");
        Assert.assertEquals("job1", job1Entry.id);
        Assert.assertNotNull(job1Entry.startTime);
        FxNodeAssert.assertEquals(job1Entry.pendingData, job1);

        FxPendingJobsFileStoreHelper sutReload = new FxPendingJobsFileStoreHelper(new FxKeyNodeFileStore(pendingStore.getStoreFile()));
        Assert.assertEquals(ImmutableList.of("job1"), sutReload.listPendings());

        AtomicBoolean finishWaitJob1 = new AtomicBoolean(false);
        new Thread(() -> {
            sut.waitPending("job1");
            finishWaitJob1.set(true);
        }).start();

        sut.removePending("job1");
        Assert.assertFalse(sut.isPending("job1"));

        Thread.sleep(10);
        if (!finishWaitJob1.get()) {
            Thread.sleep(20); // should not occur.. still wait few millis
            Assert.assertTrue(finishWaitJob1.get());
        }
    }
}
