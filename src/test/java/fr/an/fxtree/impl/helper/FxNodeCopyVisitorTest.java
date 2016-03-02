package fr.an.fxtree.impl.helper;

import org.junit.Assert;
import org.junit.Test;

import fr.an.fxtree.format.json.FxJsonUtilsTest;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxObjNode;

public class FxNodeCopyVisitorTest {

    protected FxNodeCopyVisitor sut = new FxNodeCopyVisitor();

    @Test
    public void testAccept() {
        // Prepare
        FxMemRootDocument src = FxJsonUtilsTest.getJsonTstFile("file1.json");
        FxObjNode srcContent = src.getContentObj();
        FxMemRootDocument dest = new FxMemRootDocument();
        FxChildWriter destOut = dest.contentWriter();
        // Perform
        srcContent.accept(sut, destOut);
        FxObjNode destContent = dest.getContentObj();
        // Post-check
        Assert.assertEquals(srcContent.toString(), destContent.toString());
    }
}
