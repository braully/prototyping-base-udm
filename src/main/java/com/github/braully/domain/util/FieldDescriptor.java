package com.github.braully.domain.util;

public interface FieldDescriptor {

    public int getPosition();

    public int getLength();

    public <T> Class<T> getClasse();
}
