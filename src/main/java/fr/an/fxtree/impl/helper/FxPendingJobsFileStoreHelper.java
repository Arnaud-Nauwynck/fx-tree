package fr.an.fxtree.impl.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

/**
 * Store helper for pending jobs, using File (Yaml/Json) storage
 * 
 */
public class FxPendingJobsFileStoreHelper {
    
    private final Object lock = new Object();
    
    private Set<String> pendings = new HashSet<>();

    private FxKeyNodeFileStore pendingJobsStore;
    
    // ------------------------------------------------------------------------

    public FxPendingJobsFileStoreHelper(FxKeyNodeFileStore pendingJobsStore) {
        this.pendingJobsStore = pendingJobsStore;
        pendings.addAll(pendingJobsStore.keySet());
    }

    // ------------------------------------------------------------------------

    public boolean addPending(String jobId, FxNode src) {
        synchronized(lock) {
            doWriteAddPendingJobNode(jobId, src);
            return pendings.add(jobId);
        }
    }

    public void removePending(String jobId) {
        synchronized(lock) {
            pendings.remove(jobId);
            doWriteRemovePendingJobNode(jobId);
            lock.notifyAll();
        }
    }
    
    public void waitPending(String jobId) {
        synchronized(lock) {
            while(pendings.contains(jobId)) {
                try {
                    lock.wait(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public boolean isPending(String jobId) {
        synchronized(lock) {
            return pendings.contains(jobId);
        }
    }

    public List<String> listPendings() {
        synchronized(lock) {
            return new ArrayList<>(pendings);
        }
    }

    // ------------------------------------------------------------------------
    
    protected void doWriteAddPendingJobNode(String jobId, FxNode src) {
        FxObjNode jobNode = new FxMemRootDocument().setContentObj();
        FxNodeCopyVisitor.copyTo(jobNode.putBuilder("src"), src);
        jobNode.putPOJO("startDate", new Date());
        pendingJobsStore.put(jobId, jobNode);
    }
    
    protected void doWriteRemovePendingJobNode(String jobId) {
        pendingJobsStore.remove(jobId);
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FxPendingJobsFileStoreHelper [" + pendingJobsStore);
        synchronized (lock) {
            sb.append(", pendings=" + pendings);
        }
        sb.append("]");
        return sb.toString();
    }    
    
}
