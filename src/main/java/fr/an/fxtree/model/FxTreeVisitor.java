package fr.an.fxtree.model;

public abstract class FxTreeVisitor {

    public abstract void visitRoot(FxRootDocument node);

    
    public abstract void visitObj(FxObjNode node);
    
    public abstract void visitArray(FxArrayNode node);
    
    public abstract void visitTextValue(FxTextNode node);
    public abstract void visitDoubleValue(FxDoubleNode node);
    public abstract void visitIntValue(FxIntNode node);
    public abstract void visitLongValue(FxLongNode node);
    public abstract void visitBoolValue(FxBoolNode node);
    public abstract void visitBinaryValue(FxBinaryNode node);
    public abstract void visitPOJOValue(FxPOJONode node);

    public abstract void visitLink(FxLinkProxyNode node);

    public abstract void visitNullValue(FxNullNode node);

}
