package fr.an.fxtree.model;

public abstract class FxNodeFactory<T extends FxNode> {

    public abstract T newNode();

}
