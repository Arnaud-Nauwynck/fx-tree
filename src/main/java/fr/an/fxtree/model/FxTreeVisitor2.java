package fr.an.fxtree.model;

public abstract class FxTreeVisitor2<P,R> {

    public abstract R visitRoot(FxRootDocument node, P param);

    public abstract R visitObj(FxObjNode node, P param);

    public abstract R visitArray(FxArrayNode node, P param);

    public abstract R visitTextValue(FxTextNode node, P param);
    public abstract R visitDoubleValue(FxDoubleNode node, P param);
    public abstract R visitIntValue(FxIntNode node, P param);
    public abstract R visitLongValue(FxLongNode node, P param);
    public abstract R visitBoolValue(FxBoolNode node, P param);
    public abstract R visitBinaryValue(FxBinaryNode node, P param);
    public abstract R visitPOJOValue(FxPOJONode node, P param);

    public abstract R visitLink(FxLinkProxyNode node, P param);

    public abstract R visitNullValue(FxNullNode node, P param);

}
