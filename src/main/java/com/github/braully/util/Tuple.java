/*
 Copyright 2109 Braully Rocha

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 
 */
package com.github.braully.util;

/**
 *
 * @author braully
 */
public class Tuple<K, V> {

    K k;
    V v;

    public Tuple(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getK() {
        return k;
    }

    public void setK(K k) {
        this.k = k;
    }

    public V getV() {
        return v;
    }

    public void setV(V v) {
        this.v = v;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.k != null ? this.k.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Tuple<?, ?> other = (Tuple<?, ?>) obj;
        if (this.k != other.k && (this.k == null || !this.k.equals(other.k))) {
            return false;
        }
        if (this.v != other.v && (this.v == null || !this.v.equals(other.v))) {
            return false;
        }
        return true;
    }

}
