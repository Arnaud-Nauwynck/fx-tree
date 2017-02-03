package fr.an.fxtree.impl.util;

import fr.an.fxtree.model.FxNode;

/**
 * similar to FxNodecheck without runtime dependency to junit.jar
 * <p>
 * check failed throw IllegalArgumentException(), instead of junit check failed throwing junit AssertionError
 */
public final class FxNodeCheckUtils {

    /** private to force all static */
    private FxNodeCheckUtils() {
    }

    public static void checkEquals(FxNode expected, FxNode actual) {
//        JsonNode expectedJson = Fx2JacksonUtils.fxTreeToJsonNode(expected);
//        JsonNode actualJson = Fx2JacksonUtils.fxTreeToJsonNode(actual);
        if (! expected.equals(actual)) {
            System.out.println("expecting:" + expected);
            System.out.println("actual   :" + actual);

            throw new IllegalArgumentException("expecting\n" + expected + "\nactual\n" + actual);
        }
    }

    public static void checkBoolEquals(boolean expected, FxNode actual) {
        if (!actual.isBoolean()) {
            checkFail("expecting type bool, but was " + actual.getClass());
        }
        checkEquals(expected, actual.booleanValue());
    }

    public static void checkIntEquals(int expected, FxNode actual) {
        if (!actual.isInt()) {
            checkFail("expecting type int, but was " + actual.getClass());
        }
        checkEquals(expected, actual.intValue());
    }

    public static void checkLongEquals(long expected, FxNode actual) {
        if (!actual.isLong()) {
            checkFail("expecting type long, but was " + actual.getClass());
        }
        checkEquals(expected, actual.longValue());
    }

    public static void checkFloatEquals(float expected, FxNode actual, float eps) {
        if (!actual.isFloat()) {
            checkFail("expecting type float, but was " + actual.getClass());
        }
        checkEquals(expected, actual.floatValue(), eps);
    }

    public static void checkDoubleEquals(double expected, FxNode actual, double eps) {
        if (!actual.isDouble()) {
            checkFail("expecting type double, but was " + actual.getClass());
        }
        checkEquals(expected, actual.doubleValue(), eps);
    }

    public static void checkTextEquals(String expected, FxNode actual) {
        if (!actual.isTextual()) {
            checkFail("expecting type text, but was " + actual.getClass());
        }
        checkEquals(expected, actual.textValue());
    }

    // ------------------------------------------------------------------------

    public static void checkFail(String msg) {
        throw new IllegalArgumentException(msg);
    }

    public static void checkTrue(boolean val) {
        if (! val) {
            checkFail("expecting true, but was " + val);
        }
    }

    public static void checkEquals(String expected, String actual) {
        if (! (expected == actual || (expected!=null && expected.equals(actual)))) {
            checkFail("expecting '" + expected + "', but was '" + actual + "'");
        }
    }

    public static void checkEquals(boolean expected, boolean actual) {
        if (expected != actual) {
            checkFail("expecting '" + expected + "', but was '" + actual + "'");
        }
    }

    public static void checkEquals(int expected, int actual) {
        if (expected != actual) {
            checkFail("expecting '" + expected + "', but was '" + actual + "'");
        }
    }

    public static void checkEquals(long expected, long actual) {
        if (expected != actual) {
            checkFail("expecting '" + expected + "', but was '" + actual + "'");
        }
    }

    public static void checkEquals(double expected, double actual, double eps) {
        if (Math.abs(actual-expected) > eps) {
            checkFail("expecting '" + expected + "', but was '" + actual + "'");
        }
    }

}
