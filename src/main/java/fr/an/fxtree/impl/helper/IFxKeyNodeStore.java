package fr.an.fxtree.impl.helper;

import java.util.Map;
import java.util.Set;

import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;

public interface IFxKeyNodeStore {

	void purgeCache();

	public Set<String> keySet();

	public boolean containsKey(String key);

	public FxNode getCopy(String key);

	public void getCopyTo(FxChildWriter out, String key);

	public Map<String, FxNode> listResultCopies();

	public void put(String key, FxNode nodeToCopy);

	public void updatePutIfPresent(String key, FxNode nodeToCopy);

	public void remove(String key);

	public void clear();
}
