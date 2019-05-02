package com.github.braully.util;

/**
 *
 * @author braullyrocha
 */
public class BussinessException extends RuntimeException {

    public BussinessException(String message) {
        super(message);
    }

    public BussinessException(Throwable cause) {
        super(cause);
    }

    public BussinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
