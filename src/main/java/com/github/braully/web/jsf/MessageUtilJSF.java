package com.github.braully.web.jsf;

import com.github.braully.interfaces.ErrorMessage;
import com.github.braully.interfaces.InfoMessage;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessageUtilJSF {

    public void add(InfoMessage infoMsg) {
        FacesContext facesContext = getFacesContext();
        if (facesContext != null) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, infoMsg.getText(), null));
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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem.toString(), null));
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
        String detalhe = null;
        if (e != null) {
            detalhe = e.getMessage();
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, string, detalhe));
        }
    }

    public static void addErroMensagem(String string) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, string, null));
        }
    }

    public void addErro(String string, String detalhe) {
        FacesContext facesContext = getFacesContext();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, string, detalhe));
        }
    }

    public void addErro(String string) {
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
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, string, ""));
        }
    }

    public static void addAlertaMensagem(String string) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, string, ""));
        }
    }

    public void addAlerta(String string) {
        FacesContext facesContext = getFacesContext();
        if (facesContext != null) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, string, ""));
        }
    }

    public void addAlerta(String string, String detalhes) {
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
