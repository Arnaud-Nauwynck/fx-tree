package fr.an.fxtree.impl.helper;

import java.util.Iterator;
import java.util.Map.Entry;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxBoolNode;
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

/**
 * default implementation to recurse on FxNode child elements
 * 
 * @param <P>
 * @param <R>
 */
public class FxDefaultTreeVisitor2<P,R> extends FxTreeVisitor2<P,R> {

    protected R recurseNode(FxNode node, P param) {
        if (node != null) {
            return node.accept(this, param);
        } else {
            return null;
        }
    }
    
    @Override
    public R visitRoot(FxRootDocument node, P param) {
        return recurseNode(node.getContent(), param);
    }

    @Override
    public R visitObj(FxObjNode node, P param) {
        for (Iterator<Entry<String, FxNode>> iter = node.fields(); iter.hasNext(); ) {
            Entry<String, FxNode> e = iter.next();
            recurseObjFieldNode(node, e.getKey(), e.getValue(), param);
        }
        return null;
    }

    protected void recurseObjFieldNode(FxObjNode parent, String fieldname, FxNode value, P param) {
        recurseNode(value, param);
    }

    @Override
    public R visitArray(FxArrayNode node, P param) {
        int size = node.size();
        for(int i = 0; i < size; i++) {
            FxNode elt = node.get(i);
            recurseArrayEltNode(node, i, elt, param);
        }
        return null;
    }

    protected void recurseArrayEltNode(FxArrayNode node, int index, FxNode elt, P param) {
        recurseNode(elt, param);
    }

    @Override
    public R visitTextValue(FxTextNode node, P param) {
        return null;
    }

    @Override
    public R visitDoubleValue(FxDoubleNode node, P param) {
        return null;
    }

    @Override
    public R visitIntValue(FxIntNode node, P param) {
        return null;
    }

    @Override
    public R visitLongValue(FxLongNode node, P param) {
        return null;
    }

    @Override
    public R visitBoolValue(FxBoolNode node, P param) {
        return null;
    }

    @Override
    public R visitBinaryValue(FxBinaryNode node, P param) {
        return null;
    }

    @Override
    public R visitPOJOValue(FxPOJONode node, P param) {
        return null;
    }

    @Override
    public R visitNullValue(FxNullNode node, P param) {
        return null;
    }

}
