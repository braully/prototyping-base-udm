package com.github.braully.app;

import java.util.Locale;
import org.junit.Test;

/**
 *
 * @author strike
 */
public class ConfigTest {

    @Test
    public void testLocaleString() {
        Locale loc = Locale.getDefault();

        System.out.println(loc);
        System.out.println(loc.getLanguage());
        System.out.println(loc.getCountry());
    }
}
