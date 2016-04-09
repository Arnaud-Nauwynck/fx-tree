package fr.an.fxtree.model.stream;

import fr.an.fxtree.model.FxNode;

/**
 * API for pulling FxNode notifications in a stream with open/close
 * = input stream of FxNodeStreamEvent
 * <BR/>
 * see corresponding pushing class FxNodeInStream.
 * see FXNodeBlockingQueueStream
 * 
 * <PRE>
 *        --------+  FxNodeInStream
 *    QueueStream |------o
 *          ------+
 *                         -read-  
 *                         \-----> openData
 *                              
 *                         -read-
 *                         \-----> data
 *                         -read-
 *                         \-----> data
 *                         -read-
 *                         \-----> data
 *                         ...
 *                         -read-
 *                         \-----> closeData
 * </PRE>
 *  
 * <p>
 * Lifecycle:
 * <PRE>
 *   FxNodeInStream stream = ..;
 *   FxNode openData = stream.readOpen();
 *
 *   FxNodeStreamToken token; 
 *   while((token = stream.readNext()) == onItem) {
 *      FxNode item = stream.getCurrentItem();
 *      ..
 *   }
 *   
 *   if (token == onClose) {
 *     FxNode closeData = stream.getCloseData();
 *   } else {
 *     FxNode closeErrorData = stream.getCloseErrorData();
 *     Throwable getCloseErrorException();
 *   }
 *   // check.. token == onItem
 *   FxNode item0 = stream.getCurrentItem();
 *   
 *   onItem(item0)
 *   onItem(item1)
 *   ..
 *   onItem(itemN)
 *   
 *   onClose(..)  OR  onCloseError(.., ex)
 * <PRE> * 
 */
public abstract class FxNodeInStream {

    public static enum FxNodeStreamToken {
        onOpen,
        onItem,
        onClose,
        onCloseError
    }

    public abstract FxNodeStreamToken read(long timeoutMillis);

    public abstract FxNodeStreamToken read();

    /** helper method for <code>readNext() + return new Open/Item/Close/CloseError StreamEvent</code> */
    public FxNodeStreamEvent readNextEvent() {
        FxNodeStreamToken eventType = read();
        switch(eventType) {
        case onOpen: return new FxNodeStreamEvent.FxOpenStreamEvent(getOpenData()); 
        case onItem: return new FxNodeStreamEvent.FxItemStreamEvent(getCurrentItem());
        case onClose: return new FxNodeStreamEvent.FxCloseStreamEvent(getCloseData()); 
        case onCloseError: return new FxNodeStreamEvent.FxCloseErrorStreamEvent(getCloseErrorData(), getCloseErrorException());
        default: throw new IllegalStateException();
        }
    }
    
    /**
     * helper for <code>readNext(); return getOpenData();</code>
     */
    public FxNode readOpen() {
        FxNodeStreamToken token = read();
        if (token != FxNodeStreamToken.onOpen) {
            throw new IllegalStateException();
        }
        return getOpenData();
    }

    /**
     * @return open Data / valid only when readNext()==FxNodeStreamToken.onOpen
     */
    public abstract FxNode getOpenData();

    /**
     * @return current item / valid only when readNext()==FxNodeStreamToken.onItem
     */
    public abstract FxNode getCurrentItem();

    /**
     * @return close Data / valid only when readNext()==FxNodeStreamToken.onClose
     */
    public abstract FxNode getCloseData();

    /**
     * @return close error Data / valid only when readNext()==FxNodeStreamToken.onCloseError
     */
    public abstract FxNode getCloseErrorData();
    /**
     * @return close error Exception / valid only when readNext()==FxNodeStreamToken.onCloseError
     */
    public abstract Throwable getCloseErrorException();

}
