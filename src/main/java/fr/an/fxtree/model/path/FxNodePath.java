package fr.an.fxtree.model.path;

import java.util.Arrays;
import java.util.List;

import fr.an.fxtree.impl.util.FxUtils;
import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.path.FxChildPathElement.FxArrayIndexPathElt;
import fr.an.fxtree.model.path.FxChildPathElement.FxObjFieldPathElt;
import fr.an.fxtree.model.path.impl.FxNodePathParserUtils;

/**
 * immutable path into a json tree, using strict descending operator only... no "parent", or "root" allowed
 */
public class FxNodePath {

    private final FxChildPathElement[] elements;

    // ------------------------------------------------------------------------
    
    public FxNodePath(FxChildPathElement[] elements) {
        this.elements = elements;
    }
    
    public static FxNodePath of(FxChildPathElement... elements) {
        FxChildPathElement[] copy = elements.clone();
        return new FxNodePath(copy);
    }

    public static FxNodePath of(List<FxChildPathElement> elements) {
        FxChildPathElement[] copy = elements.toArray(new FxChildPathElement[elements.size()]);
        return new FxNodePath(copy);
    }

    public static FxNodePath of(Object... fieldOrIndexElements) {
        FxChildPathElement[] tmp = new FxChildPathElement[fieldOrIndexElements.length];
        int i = 0;
        for(Object fieldOrIndex : fieldOrIndexElements) {
            tmp[i] = FxChildPathElement.of(fieldOrIndex);
            i++;
        }
        return new FxNodePath(tmp);
    }
    
    public static FxNodePath parse(String text) {
        return FxNodePathParserUtils.parse(text);
    }
    
    // ------------------------------------------------------------------------

    public int size() {
        return elements.length;
    }

    public FxChildPathElement get(int index) {
        return elements[index];
    }

    public FxNode select(FxNode src) {
        return select(src, src);
    }
    
    public FxNode select(FxNode baseSrc, FxNode src) {
        if (src == null) {
            return null;
        }
        FxNode tmp = src;
        for(int i = 0; i < elements.length; i++) {
            tmp = elements[i].select(baseSrc, tmp);
            if (tmp == null) {
                return null;
            }
        }
        return tmp;
    }
    
    
    public FxChildWriter selectInsertBuilder(FxNode src) {
        return selectInsertBuilder(src, src);
    }
    
    public FxChildWriter selectInsertBuilder(FxNode baseSrc, FxNode src) {
        FxNodePath parentPath = parent();
        FxNode destParent = parentPath.select(baseSrc, src);
        
        FxChildPathElement insertPathElt = get(size()-1);
        FxChildWriter destWriter;
        if (destParent == null) {
            throw FxUtils.notImplYet(); // create missing path?...
        }
        
        if (insertPathElt instanceof FxArrayIndexPathElt) {
            int arrayIndex = ((FxArrayIndexPathElt) insertPathElt).getIndex();
            if (!destParent.isArray()) {
                throw new IllegalArgumentException("invalid copy toPath:'" + this + "' expecting array position, got " + destParent.getNodeType());
            }
            FxArrayNode destParentArray = (FxArrayNode) destParent;
            int insertPos = (arrayIndex >= 0)? arrayIndex : (destParentArray.size() - arrayIndex);
            destWriter = destParentArray.insertBuilder(insertPos);
        } else if (insertPathElt instanceof FxObjFieldPathElt) {
            String fieldname = ((FxObjFieldPathElt) insertPathElt).getFieldname();
            if (!destParent.isObject()) {
                throw new IllegalArgumentException("invalid copy toPath:'" + this + "' expecting array position, got " + destParent.getNodeType());
            }
            destWriter = ((FxObjNode) destParent).putBuilder(fieldname);
        } else {
            throw new IllegalArgumentException("invalid copy toPath:'" + this + "' expecting writable array/obj node content, got " + insertPathElt);
        }
        
        return destWriter;
    }
    
    
    // ------------------------------------------------------------------------
    
    public FxNodePath parent() {
        if (elements.length == 0) throw new IllegalArgumentException(); 
        FxChildPathElement[] parentElts = Arrays.copyOf(elements, elements.length - 1);
        return new FxNodePath(parentElts);
    }
    
    public FxNodePath child(FxNodePath append) {
        FxChildPathElement[] resElts = Arrays.copyOf(elements, elements.length + append.elements.length);
        System.arraycopy(append.elements, 0, resElts, elements.length, append.elements.length);
        return new FxNodePath(resElts);
    }

    public FxNodePath child(String childFieldname) {
        FxChildPathElement[] resElts = Arrays.copyOf(elements, elements.length + 1);
        resElts[elements.length] = FxChildPathElement.of(childFieldname);
        return new FxNodePath(resElts);
    }
    
    public FxNodePath child(int index) {
        FxChildPathElement[] resElts = Arrays.copyOf(elements, elements.length + 1);
        resElts[elements.length] = FxChildPathElement.of(index);
        return new FxNodePath(resElts);
    }

    public FxNodePath child(Object... fieldOrIndexElements) {
        FxChildPathElement[] resElts = Arrays.copyOf(elements, elements.length + fieldOrIndexElements.length);
        int i = elements.length;
        for(Object fieldOrIndex : fieldOrIndexElements) {
            resElts[i] = FxChildPathElement.of(fieldOrIndex); // should not use "$." inside path?
            i++;
        }
        return new FxNodePath(resElts);
    }
    
    public FxNodePath childSubPath() {
        if (elements.length == 0) return null;
        FxChildPathElement[] resElts = new FxChildPathElement[elements.length - 1];
        System.arraycopy(elements, 1, resElts, 0, elements.length-1);
        return new FxNodePath(resElts);
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(elements);
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
        FxNodePath other = (FxNodePath) obj;
        if (!Arrays.equals(elements, other.elements))
            return false;
        return true;
    }

    @Override
    public String toString() {
        if (elements.length == 1) {
            return elements[0].toString();
        }
        StringBuilder sb = new StringBuilder(elements.length * 8);
        for(int i = 0; i < elements.length; i++) {
            sb.append(elements[i].toString());
        }
        return sb.toString();
    }
    
}
