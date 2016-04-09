package fr.an.fxtree.impl.stream;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.util.FxNodeAssert;
import fr.an.fxtree.model.FxIntNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxTextNode;
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
        queueIn.onOpen(createTextNode("open.."));
        for (int i = 0; i < TEST_QUEUE_SIZE-2; i++) {
            queueIn.onItem(createIntNode(i));
        }
        queueIn.onClose(createTextNode("close.."));
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
            queueIn.onOpen(createTextNode("open.."));
            for (int i = 0; i < itemCount; i++) {
                queueIn.onItem(createIntNode(i));
            }
            queueIn.onClose(createTextNode("close.."));
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
    
    protected FxIntNode createIntNode(int value) {
        FxMemRootDocument doc = new FxMemRootDocument();
        doc.contentWriter().add(value);
        return (FxIntNode) doc.getContent();
    }
    
    protected FxTextNode createTextNode(String value) {
        FxMemRootDocument doc = new FxMemRootDocument();
        doc.contentWriter().add(value);
        return (FxTextNode) doc.getContent();
    }
}
