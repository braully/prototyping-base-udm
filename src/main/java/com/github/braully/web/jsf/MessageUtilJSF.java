package com.github.braully.web.jsf;

import com.github.braully.interfaces.ErrorMessage;
import com.github.braully.interfaces.InfoMessage;
import com.github.braully.util.UtilValidation;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessageUtilJSF {

    public static String tratarExecao(Exception e) {
        String message = "";
        if (e != null) {
            message = e.getMessage();
        }
        return message;
    }

    public void add(InfoMessage infoMsg) {
        FacesContext facesContext = getFacesContext();
        if (facesContext != null) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, infoMsg.getText(), ""));
        }
    }

    public void add(ErrorMessage erroMsg, Exception e) {
        StringBuilder mensagem = new StringBuilder();
        if (erroMsg != null) {
            mensagem.append(erroMsg.getText());
            mensagem.append(": ");
        }
        if (e != null) {
            mensagem.append(e.getMessage());
        }
        FacesContext facesContext = getFacesContext();
        if (facesContext != null) {
            facesContext.addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem.toString(), ""));
        }
    }

    public static void addMensagem(String title, String detail, String type) {
        if (UtilValidation.isStringEmpty(title)) {
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (facesContext != null) {
            FacesMessage.Severity stype = FacesMessage.SEVERITY_INFO;
            if (type != null) {
                if ("error".equalsIgnoreCase(type)) {
                    stype = FacesMessage.SEVERITY_ERROR;
                } else if ("warn".equalsIgnoreCase(type)) {
                    stype = FacesMessage.SEVERITY_WARN;
                }
            }
            facesContext.addMessage(null,
                    new FacesMessage(stype, title, detail));
        }
    }

    public void add(ErrorMessage erroRelatorioResp) {
        this.add(erroRelatorioResp, null);
    }

    public void addErro(String string, Exception e) {
        String detalhe = null;
        if (e != null) {
            detalhe = e.getMessage();
        }
        addErro(string, detalhe);
    }

    public static void addErroMensagem(String string, Exception e) {
        if (UtilValidation.isStringEmpty(string)) {
            return;
        }
        String detalhe = null;
        if (e != null) {
            detalhe = tratarExecao(e);
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, string, detalhe));
        }
    }

    public static void addErroMensagem(String string) {
        if (UtilValidation.isStringEmpty(string)) {
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, string, ""));
        }
    }

    public void addErro(String string, String detalhe) {
        if (UtilValidation.isStringEmpty(string)) {
            return;
        }
        FacesContext facesContext = getFacesContext();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, string, detalhe));
        }
    }

    public void addErro(String string) {
        if (UtilValidation.isStringEmpty(string)) {
            return;
        }
        FacesContext facesContext = getFacesContext();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, string, ""));
        }
    }

    public void add(String string) {
        FacesContext facesContext = getFacesContext();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, string, ""));
        }
    }

    public static void addMensagem(String string) {
        if (UtilValidation.isStringEmpty(string)) {
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, string, ""));
        }
    }

    public static void addAlertaMensagem(String string) {
        if (UtilValidation.isStringEmpty(string)) {
            return;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, string, ""));
        }
    }

    public void addAlerta(String string) {
        if (UtilValidation.isStringEmpty(string)) {
            return;
        }
        FacesContext facesContext = getFacesContext();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, string, ""));
        }
    }

    public void addAlerta(String string, String detalhes) {
        if (UtilValidation.isStringEmpty(string)) {
            return;
        }
        FacesContext facesContext = getFacesContext();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, string, detalhes));
        }
    }

    private FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}
