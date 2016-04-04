package fr.an.fxtree.impl.helper;

import fr.an.fxtree.impl.helper.FxPendingJobsFileStoreHelper.PendingEntry;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.func.FxEvalContext;
import fr.an.fxtree.model.func.FxNodeFunc;

/**
 * helper class to store function results by ids in file, and reload previous result instead of re-evaluating function
 * 
 *  thread-safety: thread-safe, protected by <code>lock</code> + <code>pendings</code>
 */
public class FxMemoizedFileStoreFuncHelper {
    
    private Object lock = new Object();
    
    private FxKeyNodeFileStore keyStore;
    
    private FxPendingJobsFileStoreHelper pendingJobsFileStoreHelper;
    
    // ------------------------------------------------------------------------

    public FxMemoizedFileStoreFuncHelper(FxKeyNodeFileStore keyStore, FxPendingJobsFileStoreHelper pendingJobsFileStoreHelper) {
        this.keyStore = keyStore;
        this.pendingJobsFileStoreHelper = pendingJobsFileStoreHelper;
    }

    // ------------------------------------------------------------------------

    public void evalSaveOrReloadResult(String resultId, FxNodeFunc func, FxChildWriter dest, FxEvalContext ctx, FxNode src) {
        FxNode resultNode;
        synchronized (lock) {
            resultNode = keyStore.getCopy(resultId);
            
            if (resultNode == null) {
                PendingEntry pending = pendingJobsFileStoreHelper.addPending(resultId, src);
                if (pending == null) {
                    pendingJobsFileStoreHelper.waitPending(resultId);
                    resultNode = keyStore.getCopy(resultId);
                }
            }
        }
        if (resultNode != null) {
            // reload => simply copy
            FxNodeCopyVisitor.copyTo(dest, resultNode);
        } else {
            // do eval func + copy result and save to storage for later re-eval
            // ** do eval func **
            func.eval(dest, ctx, src);
            
            resultNode = dest.getResultChild();
            
            synchronized (lock) {
                pendingJobsFileStoreHelper.removePending(resultId);
                keyStore.put(resultId, resultNode);
            }
        }
    }

}
