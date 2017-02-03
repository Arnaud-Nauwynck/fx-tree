package fr.an.fxtree.impl.stream;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import fr.an.fxtree.format.memmaplist.Fx2MemMapListUtils;
import fr.an.fxtree.impl.util.FxNodeAssert;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.stream.FxNodeInStream;
import fr.an.fxtree.model.stream.FxNodeInStream.FxNodeStreamToken;
import fr.an.fxtree.model.stream.FxNodeOutStream;

public class FXNodeBlockingQueueStreamTest {

    protected static int TEST_QUEUE_SIZE = 10;
    protected FXNodeBlockingQueueStream sut = new FXNodeBlockingQueueStream(TEST_QUEUE_SIZE);

    @Test
    public void testGetInput_GetOutput_nonBlocking() {
        // Prepare
        FxNodeOutStream queueIn = sut.getInput();
        FxNodeInStream queueOut = sut.getOutput();
        // Perform queueOut
        queueIn.onOpen(Fx2MemMapListUtils.valueToTree("open.."));
        for (int i = 0; i < TEST_QUEUE_SIZE-2; i++) {
            queueIn.onItem(Fx2MemMapListUtils.valueToTree(i));
        }
        queueIn.onClose(Fx2MemMapListUtils.valueToTree("close.."));
        // Perform queueIn + Post-check
        FxNode openData = queueOut.readOpen();
        FxNodeAssert.assertTextEquals("open..", openData);
        for (int i = 0; i < TEST_QUEUE_SIZE-2; i++) {
            Assert.assertEquals(FxNodeStreamToken.onItem, queueOut.read());
            FxNodeAssert.assertIntEquals(i, queueOut.getCurrentItem());
        }
        Assert.assertEquals(FxNodeStreamToken.onClose, queueOut.read());
        FxNodeAssert.assertTextEquals("close..", queueOut.getCloseData());
    }

    @Test
    public void testGetInput_GetOutput_multithread() throws Exception {
        // Prepare
        FxNodeOutStream queueIn = sut.getInput();
        FxNodeInStream queueOut = sut.getOutput();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        int itemCount = 100;
        // Perform queueOut
        Future<?> writerFuture = executor.submit(() -> {
            queueIn.onOpen(Fx2MemMapListUtils.valueToTree("open.."));
            for (int i = 0; i < itemCount; i++) {
                queueIn.onItem(Fx2MemMapListUtils.valueToTree(i));
            }
            queueIn.onClose(Fx2MemMapListUtils.valueToTree("close.."));
        });
        // Perform queueIn + Post-check
        Future<?> readerFuture = executor.submit(() -> {
            FxNode openData = queueOut.readOpen();
            FxNodeAssert.assertTextEquals("open..", openData);
            for (int i = 0; i < itemCount; i++) {
                Assert.assertEquals(FxNodeStreamToken.onItem, queueOut.read());
                FxNodeAssert.assertIntEquals(i, queueOut.getCurrentItem());
            }
            Assert.assertEquals(FxNodeStreamToken.onClose, queueOut.read());
            FxNodeAssert.assertTextEquals("close..", queueOut.getCloseData());
        });
        // Post-check
        writerFuture.get();
        readerFuture.get();
        executor.awaitTermination(0, TimeUnit.SECONDS);
        executor.shutdown();
    }

}
