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
package com.github.braully.constant;

/**
 *
 * @author braully
 */
public enum StrategyCalcDV {

    MODULO_11("Módulo 11"), MODULO_10("Módulo 10");

    private StrategyCalcDV(String descrciao) {
        this.descrciao = descrciao;
    }

    private final String descrciao;

    public String getDescrciao() {
        return descrciao;
    }

    @Override
    public String toString() {
        return descrciao;
    }
}
