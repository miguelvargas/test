package com.mvwsolutions.common.test;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author smineyev
 *
 */
public class TestUtils {

    private static AtomicLong baseCounter = new AtomicLong(0);
    
    public static long getUniqueLong() {
        return System.currentTimeMillis() + baseCounter.getAndIncrement();
    }
 
}
