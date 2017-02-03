package fr.an.fxtree.impl.helper;

import java.util.ArrayList;
import java.util.List;

import fr.an.fxtree.model.FxArrayNode;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public class FxDefaultStackTreeVisitor extends FxDefaultTreeVisitor {

    protected List<FxNode> currentNodeStack = new ArrayList<>();

    // ------------------------------------------------------------------------

    public FxDefaultStackTreeVisitor() {
    }

    // ------------------------------------------------------------------------

    /* override to keep track of current NodePath */
    @Override
    protected void recurseObjFieldNode(FxObjNode parent, String fieldname, FxNode value) {
        currentNodeStack.add(value);
        try {
            super.recurseObjFieldNode(parent, fieldname, value);
        } finally {
            currentNodeStack.remove(currentNodeStack.size()-1);
        }
    }

    /* override to keep track of current NodePath */
    @Override
    protected void recurseArrayEltNode(FxArrayNode parent, int index, FxNode elt) {
        currentNodeStack.add(elt);
        try {
            super.recurseArrayEltNode(parent, index, elt);
        } finally {
            currentNodeStack.remove(currentNodeStack.size()-1);
        }
    }

}
