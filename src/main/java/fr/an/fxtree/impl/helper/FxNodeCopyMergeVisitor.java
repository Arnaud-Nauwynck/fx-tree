package fr.an.fxtree.impl.helper;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxBoolNode;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxDoubleNode;
import fr.an.fxtree.model.FxIntNode;
import fr.an.fxtree.model.FxLongNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxNullNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.FxRootDocument;
import fr.an.fxtree.model.FxTextNode;
import fr.an.fxtree.model.FxTreeVisitor2;

public class FxNodeCopyMergeVisitor extends FxTreeVisitor2<FxNode,FxNode> {

    public static final FxNodeCopyMergeVisitor INSTANCE = new FxNodeCopyMergeVisitor(false, false);
    public static final FxNodeCopyMergeVisitor INSTANCE_ALLOWMOVECOPY = new FxNodeCopyMergeVisitor(true, false);
    public static final FxNodeCopyMergeVisitor INSTANCE_SKIPMISMATCH = new FxNodeCopyMergeVisitor(false, true);
    public static final FxNodeCopyMergeVisitor INSTANCE_ALLOWMOVECOPY_SKIPMISMATCH = new FxNodeCopyMergeVisitor(true, true);
    
    private final boolean allowMoveCopy;
    private final boolean allowSkipTypeMismatch;
    
    // ------------------------------------------------------------------------

    public FxNodeCopyMergeVisitor() {
        this(false, false);
    }
    
    public FxNodeCopyMergeVisitor(boolean allowMoveCopy, boolean allowSkipTypeMismatch) {
        this.allowMoveCopy = allowMoveCopy;
        this.allowSkipTypeMismatch = allowSkipTypeMismatch;
    }

    // ------------------------------------------------------------------------

    public static void copyMergeInto(FxNode dest, FxNode src, boolean allowMoveCopy, boolean allowSkipTypeMismatch) {
        if (src == null) {
            return;
        }
        FxNodeCopyMergeVisitor v = instance(allowMoveCopy, allowSkipTypeMismatch);
        src.accept(v, dest);
    }

    public static FxNodeCopyMergeVisitor instance(boolean allowMoveCopy, boolean allowSkipTypeMismatch) {
        FxNodeCopyMergeVisitor res = allowMoveCopy? 
            (allowSkipTypeMismatch? INSTANCE_ALLOWMOVECOPY : INSTANCE_ALLOWMOVECOPY_SKIPMISMATCH)
            :  (allowSkipTypeMismatch? INSTANCE_SKIPMISMATCH : INSTANCE);
        return res;
    }
    
    // ------------------------------------------------------------------------

    protected void skipMergeTypeMismatch(FxNode src, FxNode dest) {
        if (! allowSkipTypeMismatch) {
            throw new IllegalArgumentException("can not merge " + dest.getNodeType() + " <- " + src.getNodeType());
        }
    }
    

    
    @Override
    public FxNode visitRoot(FxRootDocument src, FxNode dest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FxNode visitObj(FxObjNode src, FxNode dest) {
        if (dest.isObject()) {
            FxObjNode destObj = (FxObjNode) dest;
            for(Iterator<Map.Entry<String, FxNode>> iter = src.fieldsIterCopy(allowMoveCopy); iter.hasNext(); ) {
                Entry<String, FxNode> srcFieldEntry = iter.next();
                String fieldname = srcFieldEntry.getKey();
                FxNode srcValue = srcFieldEntry.getValue();
                
                FxNode destValueNode = destObj.get(fieldname);
                if (destValueNode == null) {
                    FxChildWriter destChildWriter = destObj.putBuilder(fieldname);
                    // optim using moveCopyTo when allowed
                    if (allowMoveCopy) {
                        FxNodeCopyVisitor.removeAndCopyChildTo(destChildWriter, src, fieldname);
                    } else {
                        FxNodeCopyVisitor.copyTo(destChildWriter, srcValue);
                    }
                } else {
                    // recurse merge object field value
                    srcValue.accept(this, destValueNode);
                }
            }
        } else {
            // can not object<-array, object<-value ...ignore or rethrow
            skipMergeTypeMismatch(src, dest);
        }
        return dest;
    }


    @Override
    public FxNode visitArray(FxArrayNode src, FxNode dest) {
        if (dest.isArray()) {
            FxArrayNode destArray = (FxArrayNode) dest;
            Map<Object,FxNode> destEltByIds = null;
            final int srcLen = src.size();
            for(int i = 0; i < srcLen; i++) {
                FxNode srcElt = src.get(i);
                Object srcId = FxNodeValueUtils.tryExtractId(srcElt);
                FxNode foundDestElt = null;
                if (srcId != null) {
                    // try find corresponding node by id in dest array, then recursively merge
                    if (destEltByIds == null) {
                        destEltByIds = FxNodeValueUtils.tryIndexEltsByIds(destArray);
                    }
                    foundDestElt = destEltByIds.get(srcId);
                }
                if (foundDestElt != null) {
                    // recursively merge obj
                    srcElt.accept(this, foundDestElt);
                } else {
                    // no merge match detected => use append
                    FxChildWriter destChildWriter = destArray.insertBuilder();
                    FxNodeCopyVisitor.copyTo(destChildWriter, srcElt);
                }
            }
        } else {
            // can not object<-array, object<-value ...ignore or rethrow
            skipMergeTypeMismatch(src, dest);
        }            
        return dest;
    }

    @Override
    public FxNode visitTextValue(FxTextNode src, FxNode dest) {
        if (dest.isTextual()) {
            FxTextNode dest2 = (FxTextNode) dest;
            dest2.setValue(src.textValue());
        } else {
            skipMergeTypeMismatch(src, dest);
        }
        return dest;
    }

    @Override
    public FxNode visitDoubleValue(FxDoubleNode src, FxNode dest) {
        if (dest.isDouble()) {
            FxDoubleNode dest2 = (FxDoubleNode) dest;
            dest2.setValue(src.getValue());
        } else {
            skipMergeTypeMismatch(src, dest);
        }
        return dest;
    }

    @Override
    public FxNode visitIntValue(FxIntNode src, FxNode dest) {
        if (dest.isInt()) {
            FxIntNode dest2 = (FxIntNode) dest;
            dest2.setValue(src.getValue());
        } else {
            skipMergeTypeMismatch(src, dest);
        }
        return dest;
    }

    @Override
    public FxNode visitLongValue(FxLongNode src, FxNode dest) {
        if (dest.isLong()) {
            FxLongNode dest2 = (FxLongNode) dest;
            dest2.setValue(src.getValue());
        } else {
            skipMergeTypeMismatch(src, dest);
        }
        return dest;
    }

    @Override
    public FxNode visitBoolValue(FxBoolNode src, FxNode dest) {
        if (dest.isBoolean()) {
            FxBoolNode dest2 = (FxBoolNode) dest;
            dest2.setValue(src.getValue());
        } else {
            skipMergeTypeMismatch(src, dest);
        }
        return dest;
    }

    @Override
    public FxNode visitBinaryValue(FxBinaryNode src, FxNode dest) {
        if (dest.isBinary()) {
            FxBinaryNode dest2 = (FxBinaryNode) dest;
            dest2.setValue(src.getValueClone());
        } else {
            skipMergeTypeMismatch(src, dest);
        }
        return dest;
    }

    @Override
    public FxNode visitPOJOValue(FxPOJONode src, FxNode dest) {
        if (dest.isPojo()) {
            FxPOJONode dest2 = (FxPOJONode) dest;
            dest2.setValue(src.getValue());
        } else {
            skipMergeTypeMismatch(src, dest);
        }
        return dest;
    }

    @Override
    public FxNode visitNullValue(FxNullNode src, FxNode dest) {
        if (dest.isNull()) {
            // do nothing! ...TODO may allow merge from parent!
        } else {
            skipMergeTypeMismatch(src, dest);
        }
        return dest;
    }
    
}
