package com.github.braully.interfaces;

public interface FieldDescriptor {

    public int getPosition();

    public int getLength();

    public <T> Class<T> getClasse();
}
