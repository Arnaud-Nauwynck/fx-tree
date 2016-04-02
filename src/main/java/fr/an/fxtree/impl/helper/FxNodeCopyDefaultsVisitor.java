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
import fr.an.fxtree.model.FxLinkProxyNode;
import fr.an.fxtree.model.FxLongNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxNullNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxPOJONode;
import fr.an.fxtree.model.FxRootDocument;
import fr.an.fxtree.model.FxTextNode;
import fr.an.fxtree.model.FxTreeVisitor2;

public class FxNodeCopyDefaultsVisitor extends FxTreeVisitor2<FxNode,FxNode> {

    public static final FxNodeCopyDefaultsVisitor INSTANCE = new FxNodeCopyDefaultsVisitor();
    
    // ------------------------------------------------------------------------
    
    public FxNodeCopyDefaultsVisitor() {
    }

    // ------------------------------------------------------------------------

    public static void copyDefaultsInto(FxNode dest, FxNode src) {
        if (src == null) {
            return;
        }
        src.accept(INSTANCE, dest);
    }
    
    // ------------------------------------------------------------------------
    
    protected void skipMergeTypeMismatch(FxNode src, FxNode dest) {
//        if (! allowSkipTypeMismatch) {
//            throw new IllegalArgumentException("can not merge defaults " + dest.getNodeType() + " <- " + src.getNodeType());
//        }
    }
    
    @Override
    public FxNode visitRoot(FxRootDocument src, FxNode dest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FxNode visitObj(FxObjNode src, FxNode dest) {
        if (dest.isObject()) {
            FxObjNode destObj = (FxObjNode) dest;
            for(Iterator<Map.Entry<String, FxNode>> iter = src.fields(); iter.hasNext(); ) {
                Entry<String, FxNode> srcFieldEntry = iter.next();
                String fieldname = srcFieldEntry.getKey();
                FxNode srcValue = srcFieldEntry.getValue();
                
                FxNode destValueNode = destObj.get(fieldname);
                if (destValueNode == null) {
                    FxChildWriter destChildWriter = destObj.putBuilder(fieldname);
                    // optim using moveCopyTo when allowed
                    FxNodeCopyVisitor.copyTo(destChildWriter, srcValue);
                } else { 
                    // recurse merge default object field value
                    srcValue.accept(this, destValueNode);
                }
            }
        } else {
            // can not merge defaults object<-array, object<-value ...ignore or rethrow
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
                    // recursively merge defaults obj
                    srcElt.accept(this, foundDestElt);
                } else {
                    // no merge match detected =>  use append ?
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
        // do nothing
        return dest;
    }

    @Override
    public FxNode visitDoubleValue(FxDoubleNode src, FxNode dest) {
        // do nothing
        return dest;
    }

    @Override
    public FxNode visitIntValue(FxIntNode src, FxNode dest) {
        // do nothing
        return dest;
    }

    @Override
    public FxNode visitLongValue(FxLongNode src, FxNode dest) {
        // do nothing
        return dest;
    }

    @Override
    public FxNode visitBoolValue(FxBoolNode src, FxNode dest) {
        // do nothing
        return dest;
    }

    @Override
    public FxNode visitBinaryValue(FxBinaryNode src, FxNode dest) {
        // do nothing
        return dest;
    }

    @Override
    public FxNode visitPOJOValue(FxPOJONode src, FxNode dest) {
        // do nothing
        return dest;
    }

    @Override
    public FxNode visitLink(FxLinkProxyNode src, FxNode dest) {
        // do nothing
        return dest;
    }
    
    @Override
    public FxNode visitNullValue(FxNullNode src, FxNode dest) {
        // do nothing
        return dest;
    }
    
}
