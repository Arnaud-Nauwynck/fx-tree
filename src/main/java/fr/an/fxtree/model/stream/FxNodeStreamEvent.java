package fr.an.fxtree.model.stream;

import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.stream.FxNodeInStream.FxNodeStreamToken;

/**
 * abstract AST base class hierarchy for FxNodeOutStream events.
 * <PRE>
 *                     +-------------------+
 *                     | FxNodeStreamEvent |
 *                     +-------------------+
 *                              /\
 *                              |
 *   +--------------------------+-----------------+------------------+
 *   |                          |                 |                  |
 *  FxOpenStreamEvent   FxItemStreamEvent  FxCloseStreamEvent  FxCloseErrorStreamEvent
 * </PRE>
 */
public abstract class FxNodeStreamEvent {

    public abstract FxNodeStreamToken getEventType();
    
    /** visitor design pattern */
    public abstract void accept(FxNodeOutStream out);
    
    // ------------------------------------------------------------------------

    public static class FxOpenStreamEvent extends FxNodeStreamEvent {
        
        private final FxNode openData;

        public FxOpenStreamEvent(FxNode openData) {
            this.openData = openData;
        }

        @Override
        public FxNodeStreamToken getEventType() {
            return FxNodeStreamToken.onOpen;
        }

        @Override
        public void accept(FxNodeOutStream out) {
            out.onOpen(openData);
        }

        public FxNode getOpenData() {
            return openData;
        }
        
    }

    public static class FxItemStreamEvent extends FxNodeStreamEvent {
        private final FxNode item;

        public FxItemStreamEvent(FxNode item) {
            this.item = item;
        }

        @Override
        public FxNodeStreamToken getEventType() {
            return FxNodeStreamToken.onItem;
        }

        @Override
        public void accept(FxNodeOutStream out) {
            out.onItem(item);
        }

        public FxNode getItem() {
            return item;
        }
        
    }

    public static class FxCloseStreamEvent extends FxNodeStreamEvent {
        
        private final FxNode closeData;

        public FxCloseStreamEvent(FxNode closeData) {
            this.closeData = closeData;
        }

        @Override
        public FxNodeStreamToken getEventType() {
            return FxNodeStreamToken.onClose;
        }

        @Override
        public void accept(FxNodeOutStream out) {
            out.onClose(closeData);
        }

        public FxNode getCloseData() {
            return closeData;
        }
        
    }

    public static class FxCloseErrorStreamEvent extends FxNodeStreamEvent {

        private final FxNode closeErrorData;
        private final Throwable exception;

        public FxCloseErrorStreamEvent(FxNode closeErrorData, Throwable exception) {
            this.closeErrorData = closeErrorData;
            this.exception = exception;
        }

        @Override
        public FxNodeStreamToken getEventType() {
            return FxNodeStreamToken.onCloseError;
        }

        @Override
        public void accept(FxNodeOutStream out) {
            out.onCloseError(closeErrorData, exception);
        }

        public FxNode getCloseErrorData() {
            return closeErrorData;
        }

        public Throwable getException() {
            return exception;
        }
        
    }

}
