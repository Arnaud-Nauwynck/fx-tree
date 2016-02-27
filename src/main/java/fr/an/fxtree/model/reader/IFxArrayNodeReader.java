package fr.an.fxtree.model.reader;

import fr.an.fxtree.model.FxNode;

public interface IFxArrayNodeReader extends IFxContainerNodeReader {

    public FxNode get(int index);
    
}
