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
import com.github.braully.util.UtilFaces;
import java.io.IOException;
import java.io.InputStream;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
        Object att = this.getAtributeFromRequest(chave);
        return this.parseLong(att);
    }

    @Override
    protected HttpSession getCurrentSession() {
        HttpSession currentSession = super.getCurrentSession();
        try {
            Object session1 = null;
            if (currentSession == null) {
                session1 = FacesContext.getCurrentInstance()
                        .getExternalContext().getSession(true);
                currentSession = (HttpSession) session1;
            }
        } catch (Exception e) {
            log.debug("Fail on load curresnt session", e);
        }
        return currentSession;
    }

    @Override
    protected HttpServletRequest getCurrentRequest() {
        HttpServletRequest currentRequest = super.getCurrentRequest();
        try {
            Object request = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequest();
            currentRequest = (HttpServletRequest) request;
        } catch (Exception e) {
            log.debug("Fail on load curresnt request", e);
        }
        return currentRequest;
    }

    @Override
    protected Object getAtributeFromRequest(String nomeAtt) {
        Object ret = super.getAtributeFromRequest(nomeAtt);
        if (ret == null) {
            try {
                ret = getCurrentRequest().getParameter(nomeAtt);
                //(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            } catch (Exception e) {

            }
        }
        return ret;
    }

    protected void sendFile(InputStream stream, String nomeArquivo) {
        try {
            UtilFaces.mostrarArquivo(stream, nomeArquivo);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Falha ao enviar arquivo", ex);
        }
    }

    //TODO: Refactor
    protected void mostrarArquivo(byte[] recibo, String nomeArquivo) {
        try {
            UtilFaces.mostrarArquivo(recibo, nomeArquivo);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Falha ao enviar arquivo", ex);
        }
    }

    protected Object popSessionJSF(String nome) {
        return this.popSession(nome);
    }
}
