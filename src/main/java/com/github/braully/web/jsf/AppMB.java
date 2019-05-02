package com.github.braully.web.jsf;

import com.github.braully.util.MemoryAppender;
import com.github.braully.util.UtilProperty;
import java.util.Enumeration;
import java.util.List;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "appMB")
@Scope("singleton")
public class AppMB {

    //TODO: Refactor and Translate
    private static final String CODIGO_DEFAULT_GOOGLE_ANALYTICS = "000000";
    private static final String NOME_EMPRESA_DEFAULT = "Empresa";
    private static final String DESCRICAO_EMPRESA_DEFAULT = "SA/LTDA";
    /* */
    private static final Logger log = Logger.getLogger(AppMB.class);
    public static final String codigoGoogleAnalytics;
    private static String nomeEmpresa;
    private static String descricaoEmpresa;

    static {
        String codigoGa = CODIGO_DEFAULT_GOOGLE_ANALYTICS;
        try {
            String propriedade = UtilProperty.getProperty("application.properties", "codigoGoogleAnalytics");
            codigoGa = propriedade;
        } catch (Exception e) {
            log.debug("NÃO FOI POSSIVEL RESGATAR O CODIGO DO GOOGLE ANALYTICS, USANDO DEFAULT");
        }
        codigoGoogleAnalytics = codigoGa;
    }

    private MemoryAppender appender;
    private LoggingEvent logAtual;

    public AppMB() {
    }

    public String getCodigoGoogleAnalytics() {
        return codigoGoogleAnalytics;
    }

    public static String getCodigoGoogleAnalyticsSt() {
        return codigoGoogleAnalytics;
    }

    public String getNomeEmpresa() {
        if (nomeEmpresa == null) {
            try {
                String propriedade = UtilProperty.getProperty("application.properties", "nomeEmpresa");
                nomeEmpresa = propriedade;
            } catch (Exception e) {
                log.debug("NÃO FOI POSSIVEL RESGATAR O NOME DA EMPRESA, USANDO DEFAULT");
            }
            if (nomeEmpresa == null) {
                nomeEmpresa = NOME_EMPRESA_DEFAULT;
            }
        }
        return nomeEmpresa;
    }

    public String getDescricaoEmpresa() {
        if (descricaoEmpresa == null) {
            try {
                String propriedade = UtilProperty.getProperty("application.properties", "descricaoEmpresa");
                descricaoEmpresa = propriedade;
            } catch (Exception e) {
                log.debug("NÃO FOI POSSIVEL RESGATAR A DESCRICAO DA EMPRESA, USANDO DEFAULT");
            }
            if (descricaoEmpresa == null) {
                descricaoEmpresa = DESCRICAO_EMPRESA_DEFAULT;
            }
        }
        return descricaoEmpresa;
    }

    public MemoryAppender getAppender() {
        try {
            if (appender == null) {
                Logger logger = Logger.getRootLogger();
                this.appender = (MemoryAppender) logger.getAppender("memory");
                if (this.appender == null) {
                    for (Enumeration loggers = LogManager.getCurrentLoggers(); loggers.hasMoreElements();) {
                        Logger logger1 = (Logger) loggers.nextElement();
                        for (Enumeration appenders = logger1.getAllAppenders(); appenders.hasMoreElements();) {
                            Appender appender = (Appender) appenders.nextElement();
                            if (appender != null && "memory".equalsIgnoreCase(appender.getName())) {
                                this.appender = (MemoryAppender) appender;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Falha ao recuperar appender", e);
        }
        return appender;
    }

    public List<LoggingEvent> getLoggingEvents() {
        MemoryAppender appender1 = getAppender();
        if (appender1 != null) {
            return appender1.getLoggingEvents();
        }
        return null;
    }

    public LoggingEvent getLogAtual() {
        return logAtual;
    }

    public void setLogAtual(LoggingEvent logAtual) {
        this.logAtual = logAtual;
    }

    public void poupLogAtual(LoggingEvent logAtual) {
        this.logAtual = logAtual;
    }

    public String getStackTraceLogAtual() {
        StringBuilder sb = new StringBuilder();
        if (logAtual != null) {
            String[] throwableStrRep = this.logAtual.getThrowableStrRep();
            if (throwableStrRep != null) {
                for (String str : throwableStrRep) {
                    sb.append(str);
                    sb.append("\n");
                }
                sb.append("\n");
            }

            ThrowableInformation throwableInformation = this.logAtual.getThrowableInformation();
            if (throwableInformation != null) {
                String[] throwableStrRep1 = throwableInformation.getThrowableStrRep();
                if (throwableStrRep1 != null) {
                    for (String str : throwableStrRep1) {
                        sb.append(str);
                        sb.append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }
}
