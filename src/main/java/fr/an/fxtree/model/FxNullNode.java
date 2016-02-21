package fr.an.fxtree.model;

public class FxNullNode extends FxValueNode {

    // ------------------------------------------------------------------------

    protected FxNullNode(FxContainerNode parent, FxChildId childId) {
        super(parent, childId);
    }

    // ------------------------------------------------------------------------

    @Override
    public final FxNodeType getNodeType() {
        return FxNodeType.NULL;
    }

    @Override
    public void accept(FxTreeVisitor visitor) {
        visitor.visitNullValue(this);
    }

    @Override
    public <P, R> R accept(FxTreeVisitor2<P, R> visitor, P param) {
        return visitor.visitNullValue(this, param);
    }

    @Override
    public String asText(String defaultValue) {
        return defaultValue;
    }

    @Override
    public String asText() {
        return "null";
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        return (o instanceof FxNullNode);
    }

    @Override
    public int hashCode() {
        return FxNodeType.NULL.ordinal();
    }

    @Override
    public String toString() {
        return "null";
    }
}
