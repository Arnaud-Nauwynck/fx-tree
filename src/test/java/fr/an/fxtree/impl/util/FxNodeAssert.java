package fr.an.fxtree.impl.util;

import org.junit.Assert;

import fr.an.fxtree.model.FxNode;

public class FxNodeAssert {

    public static void assertEquals(FxNode expected, FxNode actual) {
        String expectedText = expected.toString();
        String resString = actual.toString();
        if (! expectedText.equals(resString)) {
            System.out.println("expecting:" + expectedText);
            System.out.println("actual   :" + resString);
            // TODO ... pretty print as json
            
            Assert.assertEquals(expectedText, resString);
        }
    }
}
