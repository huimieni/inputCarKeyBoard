package com.widget.carkeyboard;

/**
 *
 */
public class Objects {

    private Objects() {
    }

    public static <T> T notNull(T val) {
        if (null == val) {
            throw new NullPointerException("Null pointer");
        }
        return val;
    }
}
