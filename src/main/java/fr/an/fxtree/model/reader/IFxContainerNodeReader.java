package fr.an.fxtree.model.reader;

import java.util.Collection;
import java.util.Iterator;

import fr.an.fxtree.model.FxNode;

public interface IFxContainerNodeReader extends IFxNodeReader {

    public int size();

    public boolean isEmpty();

    public Collection<FxNode> children();

    public Iterator<FxNode> childIterator();

}
