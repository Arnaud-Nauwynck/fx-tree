package fr.an.fxtree.impl.model.mem;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import fr.an.fxtree.model.FxChildId;

public abstract class FxMemChildId extends FxChildId {
    
    private static class FxMemObjNameChildIdInternCache {

        private final ReentrantLock lock = new ReentrantLock();
        
        private Map<String, FxMemObjNameChildId> cache = new WeakHashMap<>();
        
        public FxMemObjNameChildId intern(String value) {
            if (value == null) {
                return null;
            }
            lock.lock();
            try {
                FxMemObjNameChildId res = cache.get(value);
                if (res == null) {
                    res = new FxMemObjNameChildId(value);
                    cache.put(value, res);
                }
                return res;
            } finally {
                lock.unlock();
            }
        }
    }
    
    private static final FxMemObjNameChildIdInternCache OBJNAME_CHILD_ID_CACHE = new FxMemObjNameChildIdInternCache();
    
    // ------------------------------------------------------------------------

    public static class FxMemObjNameChildId extends FxMemChildId {
        
        public final String name;

        private FxMemObjNameChildId(String name) {
            this.name = name;
        }
        
        public static FxMemObjNameChildId of(String name) {
            return OBJNAME_CHILD_ID_CACHE.intern(name);
        }
        
        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FxMemObjNameChildId other = (FxMemObjNameChildId) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return name;
        }
        
    }
    
    // ------------------------------------------------------------------------
    
    public static class FxMemArrayInsertChildId extends FxMemChildId {

        public final int childId;
        
        private int currIndex;
        
        public FxMemArrayInsertChildId(int childId, int currIndex) {
            this.childId = childId;
            this.currIndex = currIndex;
        }
        
        public int getChildId() {
            return childId;
        }

        public int getCurrIndex() {
            return currIndex;
        }

        /*pp*/ void _setCurrIndex(int index) {
            this.currIndex = index;
        }

        @Override
        public int hashCode() {
            return childId;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FxMemArrayInsertChildId other = (FxMemArrayInsertChildId) obj;
            if (childId != other.childId)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return Integer.toString(currIndex) + "#" + childId;
        }
        
    }
}
