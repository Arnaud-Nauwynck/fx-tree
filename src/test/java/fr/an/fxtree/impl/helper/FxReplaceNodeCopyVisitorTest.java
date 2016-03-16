package fr.an.fxtree.impl.helper;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.an.fxtree.impl.helper.FxReplaceNodeCopyVisitor.TmpVarReplMatch;
import fr.an.fxtree.impl.model.mem.FxMemRootDocument;
import fr.an.fxtree.model.FxNode;
import fr.an.fxtree.model.FxObjNode;

public class FxReplaceNodeCopyVisitorTest {

    FxReplaceNodeCopyVisitor sut;

    @Before
    public void setup() {
        Map<String, FxNode> repls = new HashMap<>();
        FxMemRootDocument doc = new FxMemRootDocument();
        FxObjNode content = doc.contentWriter().addObj();
        repls.put("key1", content.put("1", "value1"));
        repls.put("key2", content.put("2", "value2"));
        sut = new FxReplaceNodeCopyVisitor(repls);
    }

    @Test
    public void testReplaceMatchingText() {
        assertReplaceText("value1", "#{key1}");
        assertReplaceText("value1...", "#{key1}...");
        assertReplaceText("...value1", "...#{key1}");
        assertReplaceText("...value1...", "...#{key1}...");
        assertReplaceText("...value1..value1...", "...#{key1}..#{key1}...");
        assertReplaceText("...value1..value2...", "...#{key1}..#{key2}...");
        assertReplaceText("...value1..value2", "...#{key1}..#{key2}");
    }

    private void assertReplaceText(String expected, String text) {
        TmpVarReplMatch tmpVarReplMatch = sut.matcher(text);
        sut.findNextVarRepl(tmpVarReplMatch, 0);
        String res = sut.replaceMatchingText(text, tmpVarReplMatch);
        Assert.assertEquals(expected, res);
    }

}
