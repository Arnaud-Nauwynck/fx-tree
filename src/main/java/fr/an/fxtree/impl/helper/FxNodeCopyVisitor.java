package fr.an.fxtree.impl.helper;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
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

public class FxNodeCopyVisitor extends FxTreeVisitor2<FxChildWriter,FxNode> {

    public static final FxNodeCopyVisitor INSTANCE = new FxNodeCopyVisitor();
    
    // ------------------------------------------------------------------------

    public FxNodeCopyVisitor() {
    }

    // ------------------------------------------------------------------------

    public static FxNode copyTo(FxChildWriter out, FxNode src) {
        if (src == null) {
            return null;
        }
        return src.accept(INSTANCE, out);
    }
    
    public static FxNode cloneMemNode(FxNode src) {
        if (src == null) {
            return null;
        }
        FxMemRootDocument tmpDoc = new FxMemRootDocument(src.getSourceLoc());
        copyTo(tmpDoc.contentWriter(), src);
        return tmpDoc.getContent();
    }
    
    public static FxNode copyChildTo(FxObjNode dest, String name, FxNode src) {
        if (src != null) {
            return FxNodeCopyVisitor.copyTo(dest.putBuilder(name), src);
        }
        return null;
    }

    public static void copyChildMapTo(FxObjNode dest, Map<String,FxNode> src) {
        if (src != null && !src.isEmpty()) {
            src.forEach((name,value)-> {
                copyTo(dest.putBuilder(name), value);
            });
        }
    }

    public static void removeAndCopyChildTo(FxChildWriter out, FxObjNode parentSrc, String fieldName) {
//        if (out.canAddMoveFrom(parentSrc)) {
//            // OPTIMISATION ... equivalent to "remove() + copyTo()"
//            // when compatible nodeFactory (example both in-memory ..)
//            // out.addMoveFrom(parentSrc, fieldName);
//        } else {
            FxNode node = parentSrc.remove(fieldName);
            FxNodeCopyVisitor.copyTo(out, node);
//        }
    }
    
    public static void copyAllChildTo(FxChildWriter out, FxArrayNode src) {
        for(Iterator<FxNode> iter = src.childIterator(); iter.hasNext(); ) {
            FxNode srcChild = iter.next();
            copyTo(out, srcChild);
        }
    }
    
    public static void copyLsAllChildTo(FxChildWriter out, Collection<FxArrayNode> src) {
        for(FxArrayNode e : src) {
            copyAllChildTo(out, e);
        }
    }
    
    // ------------------------------------------------------------------------

    
    @Override
    public FxNode visitRoot(FxRootDocument src, FxChildWriter out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FxNode visitObj(FxObjNode src, FxChildWriter out) {
        FxObjNode res = out.addObj(src.getSourceLoc()); 
        for(Iterator<Map.Entry<String, FxNode>> iter = src.fields(); iter.hasNext(); ) {
            Entry<String, FxNode> srcFieldEntry = iter.next();
            String name = srcFieldEntry.getKey();
            FxNode srcValue = srcFieldEntry.getValue();
            FxChildWriter outChildAdder = res.putBuilder(name);
            // recurse copy object field value
            visitObjField(name, srcValue, outChildAdder);
        }
        return res;
    }

    @Override
    public FxNode visitArray(FxArrayNode src, FxChildWriter out) {
        FxArrayNode res = out.addArray(src.getSourceLoc());
        FxChildWriter outChildAdder = res.insertBuilder();
        int index = 0;
        for(Iterator<FxNode> iter = src.childIterator(); iter.hasNext(); ) {
            FxNode srcChild = iter.next();
            // recurse copy array element
            visitArrayElt(index, srcChild, outChildAdder);
            index++;
        }
        return res;
    }
    

    protected void visitObjField(String name, FxNode srcValue, FxChildWriter out) {
        srcValue.accept(this, out);
    }
    
    protected void visitArrayElt(int index, FxNode srcValue, FxChildWriter out) {
        srcValue.accept(this, out);
    }
    
    @Override
    public FxNode visitTextValue(FxTextNode src, FxChildWriter out) {
        return out.add(src.getValue(), src.getSourceLoc());
    }

    @Override
    public FxNode visitDoubleValue(FxDoubleNode src, FxChildWriter out) {
        return out.add(src.getValue(), src.getSourceLoc());
    }

    @Override
    public FxNode visitIntValue(FxIntNode src, FxChildWriter out) {
        return out.add(src.getValue(), src.getSourceLoc());
    }

    @Override
    public FxNode visitLongValue(FxLongNode src, FxChildWriter out) {
        return out.add(src.getValue(), src.getSourceLoc());
    }

    @Override
    public FxNode visitBoolValue(FxBoolNode src, FxChildWriter out) {
        return out.add(src.getValue(), src.getSourceLoc());
    }

    @Override
    public FxNode visitBinaryValue(FxBinaryNode src, FxChildWriter out) {
        byte[] tmp = src.getValue();
        if (tmp != null) {
            tmp = tmp.clone();
        }
        return out.add(tmp, src.getSourceLoc());
    }

    @Override
    public FxNode visitPOJOValue(FxPOJONode src, FxChildWriter out) {
        Object pojo = src.getValue();
        // TODO clone pojo??
        return out.addPOJO(pojo, src.getSourceLoc());
    }
    
    @Override
    public FxNode visitLink(FxLinkProxyNode src, FxChildWriter out) {
        return out.addLink(src.getTargetRelativePath(), src.getSourceLoc());
    }

    @Override
    public FxNode visitNullValue(FxNullNode src, FxChildWriter out) {
        return out.addNull(src.getSourceLoc());
    }
    
}
