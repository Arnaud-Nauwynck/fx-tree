package fr.an.fxtree.impl.helper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.util.StdDateFormat;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

/**
 * Store helper for pending jobs, using File (Yaml/Json) storage
 * 
 */
public class FxPendingJobsFileStoreHelper {
    
    public static final String FIELD_startTime = "startTime";
    public static final String FIELD_pendingData = "pendingData";
    
    private static final StdDateFormat DATE_FMT = new StdDateFormat();
    
    private final Object lock = new Object();
    
    private Set<String> pendings = new HashSet<>();

    private IFxKeyNodeStore pendingJobsStore;
    
    // ------------------------------------------------------------------------

    public FxPendingJobsFileStoreHelper(IFxKeyNodeStore pendingJobsStore) {
        this.pendingJobsStore = pendingJobsStore;
        pendings.addAll(pendingJobsStore.keySet());
    }

    // ------------------------------------------------------------------------

    public PendingEntry addPending(String jobId, FxNode src) {
        synchronized(lock) {
            boolean added = pendings.add(jobId);
            if (! added) {
                return null; // should not occur?
            }
            PendingEntry res = doWriteAddPendingJobNode(jobId, src);
            return res;
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

    public static class PendingEntry {
        public final String id;
        public final Date startTime;
        public final FxNode pendingData;
        
        public PendingEntry(String id, Date startTime, FxNode pendingData) {
            this.id = id;
            this.startTime = startTime;
            this.pendingData = pendingData;
        }
        
    }
    
    private static Date parseDateSafe(String text) {
        if (text == null) {
            return null;
        }
        try {
            return DATE_FMT.parse(text);
        } catch (ParseException e) {
            return null;
        } 
    }
    private static String fmtDateToText(Date date) {
        return (date != null)? DATE_FMT.format(date) : null;
    }
    
    public PendingEntry getPendingValueCopyOrNull(String jobId) {
        synchronized(lock) {
            FxObjNode tmpres = (FxObjNode) pendingJobsStore.getCopy(jobId);
            if (tmpres == null) {
                return null;
            }
            Date startTime = parseDateSafe(tmpres.get(FIELD_startTime).asText());
            FxNode pendingData = tmpres.get(FIELD_pendingData);
            return new PendingEntry(jobId, startTime, pendingData);
        }
    }
    
    public List<String> listPendings() {
        synchronized(lock) {
            return new ArrayList<>(pendings);
        }
    }

    public void updatePending(String jobId, FxNode newValue) {
        synchronized(lock) {
            PendingEntry pending = getPendingValueCopyOrNull(jobId);
            if (pending != null) {
                FxSourceLoc loc = newValue.getSourceLoc();
                FxObjNode pendingNode = new FxMemRootDocument(loc).setContentObj(loc);
                pendingNode.put(FIELD_startTime, fmtDateToText(pending.startTime), loc);
                FxNodeCopyVisitor.copyTo(pendingNode.putBuilder(FIELD_pendingData), newValue);
                
                pendingJobsStore.updatePutIfPresent(jobId, pendingNode);
            } // else.. should not occur
        }
    }

    // ------------------------------------------------------------------------
    
    protected PendingEntry doWriteAddPendingJobNode(String jobId, FxNode src) {
        FxSourceLoc loc = src.getSourceLoc();
        FxObjNode pendingNode = new FxMemRootDocument(loc).setContentObj(loc);
        Date startTime = new Date();
        pendingNode.putPOJO(FIELD_startTime, fmtDateToText(startTime), loc);
        FxNodeCopyVisitor.copyTo(pendingNode.putBuilder(FIELD_pendingData), src);
        
        pendingJobsStore.put(jobId, pendingNode);
        return new PendingEntry(jobId, startTime, src);
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
