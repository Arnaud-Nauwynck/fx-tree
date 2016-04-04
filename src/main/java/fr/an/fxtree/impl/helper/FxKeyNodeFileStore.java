package fr.an.fxtree.impl.helper;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.format.FxFileUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

/**
 * helper class to store node by (flat) keys in file
 * 
 *  thread-safety: thread-safe, protected by <code>lock</code>
 */
public class FxKeyNodeFileStore {
    
    private static final Logger LOG = LoggerFactory.getLogger(FxKeyNodeFileStore.class);
    
    private Object lock = new Object();
    
    private File storeFile;
    private FxObjNode contentObj;
    
    // ------------------------------------------------------------------------

    public FxKeyNodeFileStore(File storeFile) {
        this.storeFile = storeFile;
        if (storeFile.exists()) {
            this.contentObj = (FxObjNode) FxFileUtils.readTree(storeFile);
        } else {
            FxMemRootDocument doc = new FxMemRootDocument();
            this.contentObj = doc.setContentObj();
            try {
                FxFileUtils.writeTree(storeFile, contentObj);
            } catch(Exception ex) {
                throw new RuntimeException("can not write to file '" + storeFile + "'", ex);
            }
        }
    }

    // ------------------------------------------------------------------------

    public File getStoreFile() {
        return storeFile;
    }

    public Set<String> keySet() {
        synchronized (lock) {
            Set<String> res = new LinkedHashSet<>();
            for (Iterator<Entry<String, FxNode>> iter = contentObj.fields(); iter.hasNext(); ) {
                res.add(iter.next().getKey());
            }
            return res;
        }
    }

    public boolean containsKey(String key) {
        synchronized (lock) {
            FxNode tmpres = contentObj.get(key);
            return (tmpres != null);
        }
    }

    /** unsafe .. should not modify node from outside !*/
    public FxNode _unsafeGet(String key) {
        synchronized (lock) {
            return contentObj.get(key);
        }
    }
    
    public FxNode getCopy(String key) {
        synchronized (lock) {
            FxNode tmpres = contentObj.get(key);
            return (tmpres != null)? FxNodeCopyVisitor.cloneMemNode(tmpres) : null;
        }
    }

    public void getCopyTo(FxChildWriter out, String key) {
        synchronized (lock) {
            FxNode tmpres = contentObj.get(key);
            if (tmpres == null) {
                // add null or empty/nothing?
                return;
            } else {
                FxNodeCopyVisitor.copyTo(out, tmpres);
            }
        }
    }

    /**
     * @param key
     * @param nodeToCopy node to copy for key
     */
    public void put(String key, FxNode nodeToCopy) {
        synchronized (lock) {
            // FxNode prev = contentObj.get(key);
            FxChildWriter writer = contentObj.putBuilder(key);
            FxNodeCopyVisitor.copyTo(writer, nodeToCopy);
            flushWrite(key);
        }
    }

    public void updatePutIfPresent(String key, FxNode nodeToCopy) {
        synchronized (lock) {
            FxNode prev = contentObj.remove(key);
            if (prev != null) {                
                FxChildWriter writer = contentObj.putBuilder(key);
                FxNodeCopyVisitor.copyTo(writer, nodeToCopy);
                flushWrite(key);
            }
        }
    }

    /**
     * @param key
     */
    public void remove(String key) {
        synchronized (lock) {
            FxNode prev = contentObj.remove(key);
            if (prev != null) {
                flushWrite(key);
            }
        }
    }

    protected void flushWrite(String key) {
        try {
            FxFileUtils.writeTree(storeFile, contentObj);
        } catch(Exception ex) {
            LOG.warn("Failed to write keyNodeStore to file '" + storeFile + "' (value for key '" + key + "' may be lost on stop/restart) ... ignore, no rethrow", ex);
        }        
    }

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "FxKeyNodeFileStore [storeFile=" + storeFile + "]";
    }

    
}
