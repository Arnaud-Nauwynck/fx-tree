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

    public static void assertIntEquals(int expected, FxNode actual) {
        Assert.assertTrue(actual.isInt());
        Assert.assertEquals(expected, actual.intValue());
    }

    public static void assertTextEquals(String expected, FxNode actual) {
        Assert.assertTrue(actual.isTextual());
        Assert.assertEquals(expected, actual.textValue());
    }

}
