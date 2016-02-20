package fr.an.fxtree.model;

public abstract class FxNodeFactory {

    public abstract FxArrayNode newArray();
    public abstract FxObjNode newObj();
    
    public abstract FxTextNode newText();
    public abstract FxDoubleNode newDouble();
    public abstract FxIntNode newInt();
    public abstract FxBoolNode newBool();
    public abstract FxPOJONode newPOJO();

    public abstract FxNullNode newNull();
}
