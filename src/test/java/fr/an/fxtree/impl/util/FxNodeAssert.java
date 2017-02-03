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

    public static void assertBoolEquals(boolean expected, FxNode actual) {
        Assert.assertTrue(actual.isBoolean());
        Assert.assertEquals(expected, actual.booleanValue());
    }

    public static void assertIntEquals(int expected, FxNode actual) {
        Assert.assertTrue(actual.isInt());
        Assert.assertEquals(expected, actual.intValue());
    }

    public static void assertLongEquals(long expected, FxNode actual) {
        Assert.assertTrue(actual.isLong());
        Assert.assertEquals(expected, actual.longValue());
    }

    public static void assertFloatEquals(float expected, FxNode actual, float eps) {
        Assert.assertTrue(actual.isFloat());
        Assert.assertEquals(expected, actual.floatValue(), eps);
    }

    public static void assertDoubleEquals(double expected, FxNode actual, double eps) {
        Assert.assertTrue(actual.isDouble());
        Assert.assertEquals(expected, actual.doubleValue(), eps);
    }

    public static void assertTextEquals(String expected, FxNode actual) {
        Assert.assertTrue(actual.isTextual());
        Assert.assertEquals(expected, actual.textValue());
    }

}
