package com.github.braully.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author strike
 */
public class CollectionMapDelegate<E> implements Collection<E> {

    Map delegate = new HashMap();

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.values().contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return this.delegate.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.delegate.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return (T[]) this.delegate.values().toArray(ts);
    }

    @Override
    public boolean add(E e) {
        return null != this.delegate.put(e, e);
    }

    @Override
    public boolean remove(Object o) {
        return null != this.delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> clctn) {
        return this.delegate.containsKey(clctn);
    }

    @Override
    public boolean addAll(Collection<? extends E> clctn) {
        this.delegate.putAll(null);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        this.delegate.replaceAll(null);
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> clctn) {
        this.delegate.replaceAll(null);
        return true;
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    public boolean containsKey(Object o) {
        return this.delegate.containsKey(o);
    }

    public boolean containsValue(Object o) {
        return this.delegate.containsValue(o);
    }

    public E get(Object o) {
        return (E) this.delegate.get(o);
    }

    public E put(Object k, E v) {
        return (E) this.delegate.put(k, v);
    }

    public void putAll(Map<Object, ? extends E> map) {
        this.delegate.putAll(map);
    }

    public Set<Object> keySet() {
        return this.delegate.keySet();
    }

    public Collection<E> values() {
        return this.delegate.values();
    }

    public Set<Entry<Object, E>> entrySet() {
        return this.delegate.entrySet();
    }
}
