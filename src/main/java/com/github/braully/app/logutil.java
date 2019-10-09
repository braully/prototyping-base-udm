package com.github.braully.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author strike
 */
public class logutil {
    
    public static Logger log = LogManager.getLogger("com.github.braully");
    
    public static void info(String strmsg) {
        log.info(strmsg);
    }
    
    public static void info(String strmsg, Throwable e) {
        log.info(strmsg, e);
    }
    
    public static void error(String strmsg, Throwable ex) {
        log.error(strmsg, ex);
    }
    
    public static void error(Throwable ex) {
        log.error(ex);
    }
    
    public static void debug(String fail, Exception e) {
        log.debug(fail, e);
    }
    
    public static void debug(String msg) {
        log.debug(msg);
    }
    
    public static void warn(String msg) {
        log.warn(msg);
    }
    
    public static void warn(String msg, Exception e) {
        log.warn(msg, e);
    }
}
