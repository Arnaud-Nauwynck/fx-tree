package fr.an.fxtree.model.path;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public class FxNodeOuterPathTest {

	private static final FxSourceLoc TST_loc = FxSourceLoc.inMem();

	@Test
	public void testSelectFromStack() {
		// Prepare
		FxMemRootDocument doc = FxMemRootDocument.newInMem();
		FxObjNode root = doc.contentWriter().addObj(TST_loc);
		FxObjNode aObj = root.putObj("a", TST_loc);
		FxObjNode b1Obj = aObj.putObj("b1", TST_loc);
		FxObjNode c1Obj = b1Obj.putObj("c1", TST_loc);
		FxObjNode b2Obj = aObj.putObj("b2", TST_loc);
		FxObjNode c2Obj = b2Obj.putObj("c2", TST_loc);
		List<FxNode> stackC1 = ImmutableList.of(aObj, b1Obj, c1Obj);
		// Perform
		FxNodeOuterPath outerPath = FxNodeOuterPath.parse("^2.b2.c2");
		FxNode res = outerPath.selectFromStack(stackC1);
		// Post-check
		Assert.assertSame(c2Obj, res);
	}

}
