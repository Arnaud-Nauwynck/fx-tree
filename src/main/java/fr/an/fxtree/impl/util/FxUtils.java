package fr.an.fxtree.impl.util;

public class FxUtils {

    public static RuntimeException fail() {
        return new RuntimeException();
    }

    public static RuntimeException switchDefault() {
        return new RuntimeException("should not occur: missing switch-case .. using default!");
    }

    public static RuntimeException notImplYet() {
        throw new UnsupportedOperationException("NOT IMPLEMENTED YET");
    }

}