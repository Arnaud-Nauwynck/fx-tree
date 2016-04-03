package fr.an.fxtree.impl.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.format.FxFileUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

/**
 * Store helper for pending jobs, using File (Yaml/Json) storage
 * 
 */
public class FxPendingJobsFileStoreHelper {
    
    private static final Logger LOG = LoggerFactory.getLogger(FxPendingJobsFileStoreHelper.class);
    
    private final Object lock = new Object();
    
    private Set<String> pendings = new HashSet<>();

    private File pendingJobsStoreFile;
    private FxObjNode pendingJobsContent;
    
    // ------------------------------------------------------------------------

    public FxPendingJobsFileStoreHelper(File storeFile) {
        this.pendingJobsStoreFile = storeFile;
        if (storeFile.exists()) {
            this.pendingJobsContent = (FxObjNode) FxFileUtils.readTree(pendingJobsStoreFile);
        } else {
            FxMemRootDocument doc = new FxMemRootDocument();
            this.pendingJobsContent = doc.setContentObj();
            try {
                FxFileUtils.writeTree(pendingJobsStoreFile, pendingJobsContent);
            } catch(Exception ex) {
                throw new RuntimeException("can not write to file '" + pendingJobsStoreFile + "'", ex);
            }
        }
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
        FxObjNode jobNode = pendingJobsContent.putObj(jobId);
        FxNodeCopyVisitor.copyTo(jobNode.putBuilder("src"), src);
        jobNode.putPOJO("startDate", new Date());
        doWriteFileContentNoEx("add pending job '" + jobId + "'");
    }
    
    protected void doWriteRemovePendingJobNode(String jobId) {
        pendingJobsContent.remove(jobId);
        doWriteFileContentNoEx("remove pending job '" + jobId + "'");
    }

    protected void doWriteFileContentNoEx(String displayMsg) {
        LOG.info(displayMsg + " - write update file " + pendingJobsStoreFile);
        try {
            FxFileUtils.writeTree(pendingJobsStoreFile, pendingJobsContent);
        } catch(Exception ex) {
            throw new RuntimeException("can not write to file '" + pendingJobsStoreFile + "' .. ignore, no rethrow!" + ex.getMessage());
        }
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FxPendingJobsFileStoreHelper [file:" + pendingJobsStoreFile);
        synchronized (lock) {
            sb.append(", pendings=" + pendings);
        }
        sb.append("]");
        return sb.toString();
    }    
    
}
