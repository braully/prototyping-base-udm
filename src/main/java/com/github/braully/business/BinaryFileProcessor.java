package com.github.braully.business;

import com.github.braully.domain.BinaryFile;

/**
 *
 * @author braully
 */
public abstract class BinaryFileProcessor {

    public abstract String getType();

    public abstract void processFile(BinaryFile arquivo);
}
