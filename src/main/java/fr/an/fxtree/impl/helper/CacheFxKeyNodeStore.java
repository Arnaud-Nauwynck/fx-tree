package fr.an.fxtree.impl.helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

public class CacheFxKeyNodeStore implements IFxKeyNodeStore {

    private final IFxKeyNodeStore target;

    private long expiresDelayMillis = 15*1000; // 15 seconds 
    
    // local cache... to expires after
    private static class CacheEntry {
        private long expiryTime;
        private FxNode cachedValue;
        void checkExpire() {
            if (cachedValue != null && expiryTime > System.currentTimeMillis()) {
                this.cachedValue = null;
            }
        }
        FxNode get() {
            checkExpire();
            return cachedValue;
        }
        void set(FxNode value, long expiryTime) {
            this.expiryTime = expiryTime;
            this.cachedValue = value;
        }
    }

    private Object lock = new Object();
    private Map<String,CacheEntry> cache = Collections.synchronizedMap(new HashMap<>());
    
    // ------------------------------------------------------------------------

    public CacheFxKeyNodeStore(IFxKeyNodeStore target, long expiresDelayMillis) {
        this.target = target;
        this.expiresDelayMillis = expiresDelayMillis;
    }
    
    // ------------------------------------------------------------------------

    public void purgeCache() {
        cache.clear();
    }
    
    private long nextExpiryTime() {
        return System.currentTimeMillis() + expiresDelayMillis;
    }

    private CacheEntry doGetOrCreate(String key) {
        CacheEntry res = cache.get(key);
        if (res == null) {
            res = new CacheEntry();
            cache.put(key, res);
        }
        return res;
    }
    
    @Override
    public Set<String> keySet() {
        Set<String> res = target.keySet();
        synchronized (lock) {
            for(String key : res) {
                doGetOrCreate(key);
            }
        }
        return res;
    }

    @Override
    public boolean containsKey(String key) {
        synchronized (lock) {
            if (cache.containsKey(key)) {
                return true;
            }
        }
        boolean res = target.containsKey(key);
        if (res) {
            synchronized (lock) {
                doGetOrCreate(key);
            }
        }
        return res;
    }

    @Override
    public FxNode getCopy(String key) {
        FxNode res = null;
        CacheEntry e;
        synchronized (lock) {
            e = cache.get(key);
            if (e != null) {
                res = e.get();
            }
        }
        if (res != null) {
            return FxNodeCopyVisitor.cloneMemNode(res);
        }
        res = target.getCopy(key);
        if (res != null) {
            FxNode resCopy = FxNodeCopyVisitor.cloneMemNode(res);
            synchronized (lock) {
                if (e == null) {
                    e = doGetOrCreate(key);
                }
                e.set(resCopy, nextExpiryTime());
            }
        }
        return res;
    }

    @Override
    public void getCopyTo(FxChildWriter out, String key) {
        FxNode res = null;
        CacheEntry e;
        synchronized (lock) {
            e = cache.get(key);
            if (e != null) {
                res = e.get();
            }
        }
        if (res == null) {
            res = target.getCopy(key);
            synchronized (lock) {
                if (e == null) {
                    e = doGetOrCreate(key);
                }
                e.set(res, nextExpiryTime());
            }
        }
        if (res == null) {
            // add null or empty/nothing?
        } else {
            FxNodeCopyVisitor.copyTo(out, res);
        }
    }

    @Override
    public Map<String, FxNode> listResultCopies() {
        return target.listResultCopies(); // TOADD use cache?
    }

    @Override
    public void put(String key, FxNode value) {
        synchronized (lock) {
            doGetOrCreate(key).set(value, nextExpiryTime());
        }
        target.put(key, value);
    }

    @Override
    public void updatePutIfPresent(String key, FxNode value) {
        synchronized (lock) {
            cache.remove(key); // TOADD use cache?
        }
        target.updatePutIfPresent(key, value);
    }

    @Override
    public void remove(String key) {
        synchronized (lock) {
            cache.remove(key);
        }
        target.remove(key);
    }

    @Override
    public void clear() {
        synchronized (lock) {
            cache.clear();
        }
        target.clear();
    }

}
