package fr.an.fxtree.model.stream;

import fr.an.fxtree.model.FxNode;

/**
 * API for pushing FxNode notifications in a stream with open/close.
 * = output stream of FxNodeStreamEvent
 * <BR/>
 * see corresponding pulling class FxNodeInStream.
 * see FXNodeBlockingQueueStream
 *
 * <PRE>
 *    FxNodeOutStream  +----------
 *             o-------| QueueStream
 *                     +----------
 *     -open->
 *
 *     -data->
 *     -data->
 *     -data->
 *      ...
 *
 *     -close->
 * </PRE>
 *
 * <p>
 * Lifecycle:
 * <PRE>
 *   onOpen(..)
 *   ..
 *   onItem(item0)
 *   onItem(item1)
 *   ..
 *   onItem(itemN)
 *
 *   onClose(..)  OR  onCloseError(.., ex)
 * <PRE>
 *
 *
 */
public abstract class FxNodeOutStream {

    /**
     *
     */
    public abstract void onOpen(FxNode openMetadata);

    /**
     *
     */
    public abstract void onItem(FxNode node);

    /**
     *
     */
    public abstract void onClose(FxNode closeMetadata);

    /**
     *
     */
    public abstract void onCloseError(FxNode closeErrorMetadata, Throwable ex);

}
