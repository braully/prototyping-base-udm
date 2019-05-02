package com.github.braully.interfaces;

/**
 *
 * @author braully
 */
public interface IConvertibleEntity {

    String format();

    void parse(String str);
}
