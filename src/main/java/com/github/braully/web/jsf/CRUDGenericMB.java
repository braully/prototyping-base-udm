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
package com.github.braully.web.jsf;

import com.github.braully.app.CRUDGenericController;
import com.github.braully.persistence.IEntity;

/**
 *
 * @author braully
 */
public abstract class CRUDGenericMB<T extends IEntity> extends CRUDGenericController<T> {

    public void addErro(String msg, Exception e) {
        MessageUtilJSF.addErroMensagem(msg, e);
    }

    //TODO: Rever isso
    @Override
    public void add(String msg) {
        MessageUtilJSF.addMensagem(msg);
    }

    @Override
    public void addErro(String msg) {
        MessageUtilJSF.addErroMensagem(msg);
    }

    @Override
    public void addAlerta(String msg) {
        MessageUtilJSF.addAlertaMensagem(msg);
    }

    protected void pushSessionJSF(String chave, Object val) {
        this.setAtributeInSession(chave, val);
    }

    protected Long getRequestParamJSFLong(String chave) {
        Object att = this.getAtributeFromSession(chave);
        return this.parseLong(att);
    }

    protected Long parseLong(Object att) {
        Long ret = null;
        if (att instanceof Number) {
            if (att instanceof Long) {
                ret = (Long) att;
            } else {
                ret = ((Number) att).longValue();
            }
        } else if (att != null) {
            String parse = att.toString();
            ret = Long.parseLong(parse);
        }
        return ret;
    }

    protected void mostrarArquivo(byte[] recibo, String reciboMatpdf) {
        throw new UnsupportedOperationException("Recuperar esse metodo perdido");
    }

    protected Object popSessionJSF(String nome) {
        return this.popSession(nome);
    }
}
