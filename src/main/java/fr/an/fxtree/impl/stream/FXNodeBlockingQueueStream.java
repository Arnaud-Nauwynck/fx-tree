package fr.an.fxtree.impl.stream;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.stream.FxNodeInStream;
import fr.an.fxtree.model.stream.FxNodeOutStream;
import fr.an.fxtree.model.stream.FxNodeStreamEvent;

/**
 * Blocking queue with fixed size capacity for FxNodeStreamEvent
 *
 * <PRE>
 *    FxNodeOutStream  +---------------------------+  FxNodeInStream
 *             o-------| FXNodeBlockingQueueStream |------o
 *                     +---------------------------+
 *     -open->                                         -read-
 *                                                     \-----> openData
 *     -data->
 *     -data->                                         -read-
 *     -data->                                         \-----> data
 *      ...                                            -read-
 *                                                     \-----> data
 *     -close->                                        -read-
 *                                                     \-----> data
 *                                                     ...
 *                                                     -read-
 *                                                     \-----> closeData
 * </PRE>
 */
public class FXNodeBlockingQueueStream {

    private ArrayBlockingQueue<FxNodeStreamEvent> eventQueue;

    private FxNodeInStream output = new InnerOutput();
    private FxNodeOutStream input = new InnerInput();

    // ------------------------------------------------------------------------

    public FXNodeBlockingQueueStream(int capacity) {
        this.eventQueue = new ArrayBlockingQueue<>(capacity);
    }

    // ------------------------------------------------------------------------

    public FxNodeInStream getOutput() {
        return output;
    }

    public FxNodeOutStream getInput() {
        return input;
    }

    // ------------------------------------------------------------------------

    protected class InnerInput extends FxNodeOutStream {

        @Override
        public void onOpen(FxNode openData) {
            put(new FxNodeStreamEvent.FxOpenStreamEvent(openData));
        }

        @Override
        public void onItem(FxNode item) {
            put(new FxNodeStreamEvent.FxItemStreamEvent(item));
        }

        @Override
        public void onClose(FxNode closeData) {
            put(new FxNodeStreamEvent.FxCloseStreamEvent(closeData));
        }

        @Override
        public void onCloseError(FxNode closeErrorData, Throwable ex) {
            put(new FxNodeStreamEvent.FxCloseErrorStreamEvent(closeErrorData, ex));
        }

        protected void put(FxNodeStreamEvent event) {
            try {
                eventQueue.put(event);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ------------------------------------------------------------------------

    protected class InnerOutput extends FxNodeInStream {

        FxNodeStreamEvent curr;

        @Override
        public FxNodeStreamToken read() {
            this.curr = null;
            for(;;) {
                try {
                    this.curr = eventQueue.poll(60, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (curr != null) {
                    break;
                }
            }
            return curr.getEventType();
        }

        @Override
        public FxNodeStreamToken read(long timeoutMillis) {
            try {
                this.curr = eventQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // ignore, no rethrow
            }
            return (curr != null)? curr.getEventType() : null;
        }

        @Override
        public FxNode getOpenData() {
            return ((FxNodeStreamEvent.FxOpenStreamEvent) curr).getOpenData();
        }

        @Override
        public FxNode getCurrentItem() {
            return ((FxNodeStreamEvent.FxItemStreamEvent) curr).getItem();
        }

        @Override
        public FxNode getCloseData() {
            return ((FxNodeStreamEvent.FxCloseStreamEvent) curr).getCloseData();
        }

        @Override
        public FxNode getCloseErrorData() {
            return ((FxNodeStreamEvent.FxCloseErrorStreamEvent) curr).getCloseErrorData();
        }

        @Override
        public Throwable getCloseErrorException() {
            return ((FxNodeStreamEvent.FxCloseErrorStreamEvent) curr).getException();
        }

    }

}
