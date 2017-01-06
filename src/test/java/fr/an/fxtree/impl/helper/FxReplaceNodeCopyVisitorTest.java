package fr.an.fxtree.impl.helper;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxChildWriter;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;
import fr.an.fxtree.model.FxTextNode;

public class FxReplaceNodeCopyVisitorTest {

    FxReplaceNodeCopyVisitor sut;

    @Before
    public void setup() {
        Map<String, FxNode> repls = new HashMap<>();
        FxMemRootDocument doc = new FxMemRootDocument();
        FxObjNode content = doc.contentWriter().addObj();
        repls.put("var1", content.put("1", "value1"));
        repls.put("var2", content.put("2", "value2"));
        
        FxObjNode objRepl = content.putObj("3");
        objRepl.put("subField1", "valueSubField1");
        objRepl.put("subField2", "valueSubField2");
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
    	FxTextNode textNode = new FxMemRootDocument().setContentText(text);
        FxChildWriter outWriter = new FxMemRootDocument().contentWriter();
		FxNode resNode = sut.visitTextValue(textNode, outWriter);
        Assert.assertTrue(resNode.isTextual());
        Assert.assertEquals(expected, resNode.textValue());
    }

}
