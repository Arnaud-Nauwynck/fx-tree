package fr.an.fxtree.impl.helper;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.fxtree.format.FxFileUtils;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

/**
 * helper class to store node by (flat) keys in file
 * 
 *  thread-safety: thread-safe, protected by <code>lock</code>
 */
public class FxKeyNodeFileStore implements IFxKeyNodeStore {
    
    private static final Logger LOG = LoggerFactory.getLogger(FxKeyNodeFileStore.class);
    
    private Object lock = new Object();
    
    private File storeFile;
    private FxSourceLoc source;
    
    private FxObjNode contentObj;
    
    // ------------------------------------------------------------------------

    public FxKeyNodeFileStore(File storeFile) {
        this.storeFile = storeFile;
        this.source = new FxSourceLoc("keystore", storeFile.getAbsolutePath());
        if (storeFile.exists()) {
            this.contentObj = (FxObjNode) FxFileUtils.readTree(storeFile, source);
        } else {
            FxMemRootDocument doc = new FxMemRootDocument(source);
            this.contentObj = doc.setContentObj(source);
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

    @Override
    public void purgeCache() {
        this.contentObj = (FxObjNode) FxFileUtils.readTree(storeFile, source);
    }
    
    @Override
    public Set<String> keySet() {
        synchronized (lock) {
            Set<String> res = new LinkedHashSet<>();
            for (Iterator<Entry<String, FxNode>> iter = contentObj.fields(); iter.hasNext(); ) {
                res.add(iter.next().getKey());
            }
            return res;
        }
    }

    @Override
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
    
    @Override
    public FxNode getCopy(String key) {
        synchronized (lock) {
            FxNode tmpres = contentObj.get(key);
            return (tmpres != null)? FxNodeCopyVisitor.cloneMemNode(tmpres) : null;
        }
    }

    @Override
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

    @Override
    public Map<String,FxNode> listResultCopies() {
        synchronized (lock) {
            Map<String,FxNode> res = new LinkedHashMap<>();
            contentObj.forEachFields((n,v) -> {
                res.put(n, FxNodeCopyVisitor.cloneMemNode(v));
            });
            return res;
        }
    }

    @Override
    public void put(String key, FxNode nodeToCopy) {
        synchronized (lock) {
            // FxNode prev = contentObj.get(key);
            FxChildWriter writer = contentObj.putBuilder(key);
            FxNodeCopyVisitor.copyTo(writer, nodeToCopy);
            flushWrite(key);
        }
    }

    @Override
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

    @Override
    public void remove(String key) {
        synchronized (lock) {
            FxNode prev = contentObj.remove(key);
            if (prev != null) {
                flushWrite(key);
            }
        }
    }

    @Override
    public void clear() {
        synchronized (lock) {
            contentObj.removeAll();
            flushWrite(null);
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
