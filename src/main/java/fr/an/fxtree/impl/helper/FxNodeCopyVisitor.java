package fr.an.fxtree.impl.helper;

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
        FxMemRootDocument tmpDoc = new FxMemRootDocument();
        copyTo(tmpDoc.contentWriter(), src);
        return tmpDoc.getContent();
    }
    
    // ------------------------------------------------------------------------

    
    @Override
    public FxNode visitRoot(FxRootDocument src, FxChildWriter out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FxNode visitObj(FxObjNode src, FxChildWriter out) {
        FxObjNode res = out.addObj(); 
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
        FxArrayNode res = out.addArray();
        FxChildWriter outChildAdder = res.insertBuilder();
        int index = 0;
        for(FxNode srcChild : src.children()) {
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
        return out.add(src.getValue());
    }

    @Override
    public FxNode visitDoubleValue(FxDoubleNode src, FxChildWriter out) {
        return out.add(src.getValue());
    }

    @Override
    public FxNode visitIntValue(FxIntNode src, FxChildWriter out) {
        return out.add(src.getValue());
    }

    @Override
    public FxNode visitLongValue(FxLongNode src, FxChildWriter out) {
        return out.add(src.getValue());
    }

    @Override
    public FxNode visitBoolValue(FxBoolNode src, FxChildWriter out) {
        return out.add(src.getValue());
    }

    @Override
    public FxNode visitBinaryValue(FxBinaryNode src, FxChildWriter out) {
        byte[] tmp = src.getValue();
        if (tmp != null) {
            tmp = tmp.clone();
        }
        return out.add(tmp);
    }

    @Override
    public FxNode visitPOJOValue(FxPOJONode src, FxChildWriter out) {
        Object pojo = src.getValue();
        // TODO clone pojo??
        return out.addPOJO(pojo);
    }

    @Override
    public FxNode visitNullValue(FxNullNode src, FxChildWriter out) {
        return out.addNull();
    }
    
}
