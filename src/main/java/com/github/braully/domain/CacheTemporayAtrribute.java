/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* MIT License
* 
* Copyright (c) 2021 Braully Rocha
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package com.github.braully.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author strike
 */
public class CacheTemporayAtrribute {

    Map<String, Object> map = new HashMap<>();

    public Map<String, Object> getMap() {
        return map;
    }

    public CacheTemporayAtrribute merge(CacheTemporayAtrribute cache) {
        if (cache != null) {
            merge(cache.map);
        }
        return this;
    }

    public CacheTemporayAtrribute merge(Map<String, Object> map) {
        if (map != null) {
            for (Entry<String, Object> e : map.entrySet()) {
                if (e.getValue() != null) {
                    this.map.put(e.getKey(), e.getValue());
                }
            }
        }
        return this;
    }

    public CacheTemporayAtrribute setValue(String attr, Object value) {
        this.map.put(attr, value);
        return this;
    }

    public CacheTemporayAtrribute condicao(String condicao) {
        return this.setValue("expressaoCondicao", condicao);
    }

    public CacheTemporayAtrribute valor(String valor) {
        return this.setValue("expressaoValor", valor);
    }

    public CacheTemporayAtrribute referente(String ref) {
        return this.setValue("expressaoReferente", ref);
    }

    public CacheTemporayAtrribute validade(String validade) {
        return this.setValue("expressaoValidade", validade);
    }

    public void setExpressaoCondicao(String condicao) {
        this.setValue("expressaoCondicao", condicao);
    }

    public String getExpressaoCondicao() {
        return (String) this.map.get("expressaoCondicao");
    }

    public void setExpressaoValor(String condicao) {
        this.setValue("expressaoValor", condicao);
    }

    public String getExpressaoValor() {
        return (String) this.map.get("expressaoValor");
    }

    public void setExpressaoReferente(String referente) {
        this.setValue("expressaoReferente", referente);
    }

    public String getExpressaoReferente() {
        return (String) this.map.get("expressaoReferente");
    }

    public void setExpressaoValidade(String referente) {
        this.setValue("expressaoValidade", referente);
    }

    public String getExpressaoValidade() {
        return (String) this.map.get("expressaoValidade");
    }

}
