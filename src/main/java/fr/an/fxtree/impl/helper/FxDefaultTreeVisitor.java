package fr.an.fxtree.impl.helper;

import java.util.Iterator;
import java.util.Map.Entry;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxBinaryNode;
import fr.an.fxtree.model.FxBoolNode;
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
import fr.an.fxtree.model.FxTreeVisitor;

/**
 * default implementation to recurse on FxNode child elements
 *
 */
public class FxDefaultTreeVisitor extends FxTreeVisitor {

    // ------------------------------------------------------------------------

    public FxDefaultTreeVisitor() {
    }

    // ------------------------------------------------------------------------

    protected void recurseNode(FxNode node) {
        if (node != null) {
            node.accept(this);
        } else {

        }
    }

    @Override
    public void visitRoot(FxRootDocument node) {
        recurseNode(node.getContent());
    }

    @Override
    public void visitObj(FxObjNode node) {
        for (Iterator<Entry<String, FxNode>> iter = node.fields(); iter.hasNext();) {
            Entry<String, FxNode> e = iter.next();
            String fieldname = e.getKey();
            FxNode childNode = e.getValue();
            recurseObjFieldNode(node, fieldname, childNode);
        }
    }

    protected void recurseObjFieldNode(FxObjNode parent, String fieldname, FxNode value) {
        recurseNode(value);
    }

    @Override
    public void visitArray(FxArrayNode node) {
        int size = node.size();
        for (int i = 0; i < size; i++) {
            FxNode elt = node.get(i);
            recurseArrayEltNode(node, i, elt);
        }

    }

    protected void recurseArrayEltNode(FxArrayNode node, int index, FxNode elt) {
        recurseNode(elt);
    }

    @Override
    public void visitTextValue(FxTextNode node) {

    }

    @Override
    public void visitDoubleValue(FxDoubleNode node) {

    }

    @Override
    public void visitIntValue(FxIntNode node) {

    }

    @Override
    public void visitLongValue(FxLongNode node) {

    }

    @Override
    public void visitBoolValue(FxBoolNode node) {

    }

    @Override
    public void visitBinaryValue(FxBinaryNode node) {

    }

    @Override
    public void visitPOJOValue(FxPOJONode node) {

    }

    @Override
    public void visitLink(FxLinkProxyNode node) {
        FxNode targetNode = node.getTargetNode();
        if (targetNode != null) {
            targetNode.accept(this);
        }
    }

    @Override
    public void visitNullValue(FxNullNode node) {

    }

}
