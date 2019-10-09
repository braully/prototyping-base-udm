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
public enum ExpressionType {
//TODO: Translate and unify 
    SIMPLES("Substituição simples"), EXCEL("Expressão excel"), JS("Expressão js");

    private final String label;

    private ExpressionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getDescricao() {
        return label;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
