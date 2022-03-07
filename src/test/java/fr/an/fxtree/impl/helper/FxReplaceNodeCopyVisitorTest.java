package fr.an.fxtree.impl.helper;


import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.impl.model.mem.FxSourceLoc;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxTextNode;

public class FxReplaceNodeCopyVisitorTest {

    FxReplaceNodeCopyVisitor sut;
    private static final FxSourceLoc TST_loc = FxSourceLoc.inMem();

    @Before
    public void setup() {
        Map<String, FxNode> repls = new HashMap<>();
        FxMemRootDocument doc = new FxMemRootDocument(TST_loc);
        FxObjNode content = doc.contentWriter().addObj(TST_loc);
        repls.put("var1", content.put("1", "value1", TST_loc));
        repls.put("var2", content.put("2", "value2", TST_loc));

        FxObjNode objRepl = content.putObj("3", TST_loc);
        objRepl.put("subField1", "valueSubField1", TST_loc);
        objRepl.put("subField2", "valueSubField2", TST_loc);
        repls.put("var3", objRepl);

        sut = new FxReplaceNodeCopyVisitor(repls);
    }

    @Test
    public void testReplaceMatchingText() {
        assertReplaceText("value1", "#{var1}");
        assertReplaceText("value1...", "#{var1}...");
        assertReplaceText("...value1", "...#{var1}");
        assertReplaceText("...value1...", "...#{var1}...");
        assertReplaceText("...value1..value1...", "...#{var1}..#{var1}...");
        assertReplaceText("value1value2", "#{var1}#{var2}");
        assertReplaceText("...value1..value2...", "...#{var1}..#{var2}...");
        assertReplaceText("...value1..value2", "...#{var1}..#{var2}");
    }

    @Test
    public void testReplaceMatchingText_leave_unmatching_vars() {
        assertReplaceText("#{varUnmodified}", "#{varUnmodified}");
        assertReplaceText("..#{varUnmodified}...", "..#{varUnmodified}...");
    }

    @Test
    public void testReplaceJqExpr() {
    	assertReplaceText("valueSubField1", "#{var3:.subField1}");
    	assertReplaceText("valueSubField1...", "#{var3:.subField1}...");
    	assertReplaceText("...valueSubField1", "...#{var3:.subField1}");
    	assertReplaceText("...valueSubField1...", "...#{var3:.subField1}...");
    	assertReplaceText("...valueSubField1..valueSubField2..", "...#{var3:.subField1}..#{var3:.subField2}..");
    }

    @Test @Ignore // TODO
    public void testReplacePathExpr() {
        assertReplaceText("valueSubField1", "#{var3.subField1}");
        assertReplaceText("valueSubField1...", "#{var3.subField1}...");
        assertReplaceText("...valueSubField1", "...#{var3.subField1}");
        assertReplaceText("...valueSubField1...", "...#{var3.subField1}...");
        assertReplaceText("...valueSubField1..valueSubField2..", "...#{var3.subField1}..#{var3.subField2}..");
    }

    private void assertReplaceText(String expected, String text) {
    	FxTextNode textNode = new FxMemRootDocument(TST_loc).setContentText(text, TST_loc);
        FxChildWriter outWriter = new FxMemRootDocument(TST_loc).contentWriter();
		FxNode resNode = sut.visitTextValue(textNode, outWriter);
        Assert.assertTrue(resNode.isTextual());
        Assert.assertEquals(expected, resNode.textValue());
    }

}
