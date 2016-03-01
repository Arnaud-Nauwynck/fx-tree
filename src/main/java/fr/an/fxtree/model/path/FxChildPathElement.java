package fr.an.fxtree.model.path;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public abstract class FxChildPathElement {

    public static FxChildPathElement of(Object obj) {
        if (obj instanceof String) {
            String fieldname = (String) obj;
            return of(fieldname);
        } else if (obj instanceof Integer) {
            int index = ((Integer) obj).intValue();
            return of(index);
        } else if (obj instanceof FxThisPathElement) {
            return (FxThisPathElement) obj;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static FxThisPathElement ofThis() {
        return FxThisPathElement.INSTANCE;
    }
    
    public static FxObjFieldPathElt of(String field) {
        return FxObjFieldPathElt.of(field);
    }

    public static FxArrayIndexPathElt of(int index) {
        return FxArrayIndexPathElt.of(index);
    }

    
    
    
    public abstract FxNode select(FxNode src);
    
    
    // ------------------------------------------------------------------------
    
    /**
     * <PRE>$.</PRE>  json child path element = current json node 
     */
    public static final class FxThisPathElement extends FxChildPathElement {
        
        public static final FxThisPathElement INSTANCE = new FxThisPathElement();
        
        private FxThisPathElement() {
        }
        
        @Override
        public FxNode select(FxNode src) {
            return src;
        }
        
        @Override
        public int hashCode() {
            return 123;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            return true;
        }
 
        @Override
        public String toString() {
            return "$.";
        }
                
    }
    
    // ------------------------------------------------------------------------

    /**
     * <PRE>.fieldname</PRE>  json child path element = field element by name of json object 
     */
    public static final class FxObjFieldPathElt extends FxChildPathElement {
        
        private final String fieldname;

        private FxObjFieldPathElt(String fieldname) {
            if (fieldname == null) throw new IllegalArgumentException();
            this.fieldname = fieldname;
        }
        
        public static FxObjFieldPathElt of(String fieldname) {
            return new FxObjFieldPathElt(fieldname);
        }
        
        public String getFieldname() {
            return fieldname;
        }

        @Override
        public FxNode select(FxNode src) {
            if (!(src instanceof FxObjNode)) {
                return null;
            }
            return ((FxObjNode) src).get(fieldname);
        }
        
        @Override
        public int hashCode() {
            return 123;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FxObjFieldPathElt o = (FxObjFieldPathElt) obj;
            if (!fieldname.equals(o.fieldname)) {
                return false;
            }
            return true;
        }
 
        @Override
        public String toString() {
            return "." + fieldname;
        }
    }
    

    // ------------------------------------------------------------------------

    /**
     * <PRE>[index]</PRE>  json array index path element = element by index of json array
     * Notice: negative number are supported, to start from end of array.  [-1] is the last element. 
     */
    public static final class FxArrayIndexPathElt extends FxChildPathElement {
        private static final int CACHE_MAX = 20;
        private static final FxArrayIndexPathElt[] CACHE;
        private static final FxArrayIndexPathElt CACHE_MINUS1;
        static {
            FxArrayIndexPathElt[] tmpcache = new FxArrayIndexPathElt[CACHE_MAX]; 
            for (int i = 0; i < CACHE_MAX; i++) {
                tmpcache[i] = new FxArrayIndexPathElt(i);
            }
            CACHE = tmpcache;
            CACHE_MINUS1 = new FxArrayIndexPathElt(-1);
        }
                
        private final int index;

        private FxArrayIndexPathElt(int index) {
            this.index = index;
        }
        
        public static FxArrayIndexPathElt of(int index) {
            if (index >= 0) {
                return (index < CACHE_MAX)? CACHE[index] : new FxArrayIndexPathElt(index);
            } else {
                return (index == -1)? CACHE_MINUS1 : new FxArrayIndexPathElt(index);
            }
        }
        
        public int getIndex() {
            return index;
        }

        @Override
        public FxNode select(FxNode src) {
            if (!(src instanceof FxArrayNode)) {
                return null;
            }
            FxArrayNode array = (FxArrayNode) src;
            if (index >= 0) {
                return array.get(index);
            } else {
                int i = array.size() + index;
                if (i == -1 && index == -1) return null; // conventionally: last element of empty array is null?
                return array.get(i);
            }
        }
        
        @Override
        public int hashCode() {
            return 123;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FxArrayIndexPathElt o = (FxArrayIndexPathElt) obj;
            if (index != o.index) {
                return false;
            }
            return true;
        }
 
        @Override
        public String toString() {
            return "[" + index + "]";
        }
    }
}
