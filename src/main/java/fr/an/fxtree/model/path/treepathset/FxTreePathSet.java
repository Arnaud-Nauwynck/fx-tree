package fr.an.fxtree.model.path.treepathset;

import java.util.HashMap;
import java.util.Map;

import fr.an.fxtree.model.path.FxChildPathElement;
import fr.an.fxtree.model.path.FxChildPathElement.FxArrayIndexPathElt;
import fr.an.fxtree.model.path.FxChildPathElement.FxObjFieldPathElt;
import fr.an.fxtree.model.path.FxNodePath;

public abstract class FxTreePathSet {

    
    public boolean match(FxNodePath path) {
        return match(path, 0);
    }
    
    public abstract boolean match(FxNodePath path, int fromIndex);
    
//    public abstract List<FxTreePathSet> subselect(FxNodePath path);

    // ------------------------------------------------------------------------

    /**
     * FxTreePathSet for matching FxRootNode element, then delegate <code>content</code> match to FxTreePathSet 
     */
    public static class FxRootTreePathSet extends FxTreePathSet {
        
        private FxTreePathSet contentMatch;

        public FxRootTreePathSet() {
        }
        
        public FxRootTreePathSet(FxTreePathSet contentMatch) {
            this.contentMatch = contentMatch;
        }

        @Override
        public boolean match(FxNodePath path, int fromIndex) {
            if (fromIndex != 0) return false; // ??
            return path.size() == 0 || (path.size() == 1 && path.get(0) == FxChildPathElement.thisRoot());
        }
        
        public FxTreePathSet getContentMatch() {
            return contentMatch;
        }

        public void setContentMatch(FxTreePathSet contentMatch) {
            this.contentMatch = contentMatch;
        }
        
    }
    
    // ------------------------------------------------------------------------

    /**
     * FxTreePathSet for matching FxObjNode element, then delegate <code>field</code> match to FxTreePathSet 
     */
    public static class FxObjTreePathSet extends FxTreePathSet {
        
        private Map<String,FxTreePathSet> fieldMatches;
        private FxTreePathSet otherFieldMatch;

        public FxObjTreePathSet() {
            fieldMatches = new HashMap<>();
        }

        public FxObjTreePathSet(Map<String, FxTreePathSet> fieldMatch, FxTreePathSet otherFieldMatch) {
            this.fieldMatches = fieldMatch;
            this.otherFieldMatch = otherFieldMatch;
        }

        @Override
        public boolean match(FxNodePath path, int fromIndex) {
            if (path.size() == fromIndex) return true;
            FxChildPathElement fromPathElt = path.get(fromIndex);
            if (!(fromPathElt instanceof FxObjFieldPathElt)) return false;
            String fieldName = ((FxObjFieldPathElt) fromPathElt).getFieldname();
            FxTreePathSet fieldMatch = fieldMatches.get(fieldName);
            if (fieldMatch == null) {
                fieldMatch = otherFieldMatch;
            }
            if (fieldMatch == null) {
                return false;
            }
            // test match recurse remaining path
            // ?? FxNodePath remainChildSubPath = path.childSubPath();
            if (path.size() == fromIndex+1) {
                return true;
            }
            boolean res = fieldMatch.match(path, fromIndex+1);
            return res;
        }
        
        public Map<String, FxTreePathSet> getFieldMatches() {
            return fieldMatches;
        }

        public void setFieldMatches(Map<String, FxTreePathSet> fieldMatches) {
            this.fieldMatches = fieldMatches;
        }

        public void put(String field, FxTreePathSet pathSet) {
            this.fieldMatches.put(field, pathSet);
        }

        public void remove(String field) {
            this.fieldMatches.remove(field);
        }

        public FxTreePathSet getOtherFieldMatch() {
            return otherFieldMatch;
        }

        public void setOtherFieldMatch(FxTreePathSet otherFieldMatch) {
            this.otherFieldMatch = otherFieldMatch;
        }

        @Override
        public String toString() {
            return "FxObjTreePathSet [fields=" + fieldMatches.keySet() + "]";
        }
        
    }
    

    // ------------------------------------------------------------------------

    /**
     * FxTreePathSet for matching FxArrayNode element, then delegate <code>elements</code> match to FxTreePathSet 
     */
    public static class FxArrayTreePathSet extends FxTreePathSet {
        
        private Map<Integer,FxTreePathSet> elementMatch;
        private FxTreePathSet otherElementMatch;

        public FxArrayTreePathSet() {
            elementMatch = new HashMap<>();
        }

        public FxArrayTreePathSet(Map<Integer, FxTreePathSet> elementMatch, FxTreePathSet otherFieldMatch) {
            this.elementMatch = elementMatch;
            this.otherElementMatch = otherFieldMatch;
        }

        @Override
        public boolean match(FxNodePath path, int fromIndex) {
            if (path.size() == fromIndex) return true;
            FxChildPathElement fromPathElt = path.get(fromIndex);
            if (!(fromPathElt instanceof FxArrayIndexPathElt)) return false;
            int arrayIndex = ((FxArrayIndexPathElt) fromPathElt).getIndex();
            FxTreePathSet indexMatch = elementMatch.get(arrayIndex);
            if (indexMatch == null) {
                indexMatch = otherElementMatch;
            }
            if (indexMatch == null) {
                return false;
            }
            // test match recurse remaining path
            // ?? FxNodePath remainChildSubPath = path.childSubPath();
            if (path.size() == fromIndex+1) {
                return true;
            }
            boolean res = indexMatch.match(path, fromIndex+1);
            return res;
        }
        
        public Map<Integer, FxTreePathSet> getElementMatch() {
            return elementMatch;
        }

        public void setElementMatch(Map<Integer, FxTreePathSet> elementMatch) {
            this.elementMatch = elementMatch;
        }

        public void put(int index, FxTreePathSet pathSet) {
            this.elementMatch.put(index, pathSet);
        }

        public void remove(int index) {
            this.elementMatch.remove(index);
        }

        public FxTreePathSet getOtherElementMatch() {
            return otherElementMatch;
        }

        public void setOtherElementMatch(FxTreePathSet otherElementMatch) {
            this.otherElementMatch = otherElementMatch;
        }

        @Override
        public String toString() {
            return "FxArrayTreePathSet [elements=" + elementMatch.keySet() + "]";
        }
        
    }
    
}
