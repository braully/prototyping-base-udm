package com.github.braully.web.jsf;

import com.github.braully.app.SpringMainConfig;
import com.github.braully.app.logutil;
import com.github.braully.util.MemoryAppender;
import com.github.braully.util.UtilDate;
import com.github.braully.util.UtilProperty;
import com.github.braully.util.WebConsoleAppender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.util.Throwables;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Qualifier("appMB")
@Component("appMB")
@RestController
//@Controller
//@RequestMapping(path = "/system/app")
public class AppMB {

    //TODO: Refactor and Translate
    static final String CODIGO_DEFAULT_GOOGLE_ANALYTICS = "000000";
    static final String NOME_EMPRESA_DEFAULT = "Empresa";
    static final String DESCRICAO_EMPRESA_DEFAULT = "SA/LTDA";
    /* */
    static final Logger log = LogManager.getLogger(AppMB.class);
    static final Logger logWebConsole = LogManager.getLogger("WEBCONSOLE");
    public static final String codigoGoogleAnalytics;
    static String nomeEmpresa;
    static String descricaoEmpresa;

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

    MemoryAppender appender;
    LogEvent logAtual;
    protected Process process;

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
                LoggerContext lc = (LoggerContext) LogManager.getContext(false);
                appender = lc.getConfiguration().getAppender("Memory");
            }
        } catch (Exception e) {
            log.error("Falha ao recuperar appender", e);
        }
        return appender;
    }

    @RequestMapping(path = "/system/app/console")
    @ResponseBody
    public List<LogEvent> getLoggingEvents() {
        List logs = new ArrayList();
        try {
            MemoryAppender appender1 = getAppender();
            if (appender1 != null) {
                LinkedList<LogEvent> logEvents = appender1.getLogEvents();
                for (LogEvent log : logEvents) {
                    String msg = "";
                    StringBuilder msgT = new StringBuilder();

                    String source = "";
                    String level = "";
                    String time = "";
                    try {
                        level = "" + log.getLevel();
                        time = "" + UtilDate.formatData("yyyy-MM-dd HH:mm:ss", new Date(log.getTimeMillis()));
                        msg = "" + log.getMessage().getFormattedMessage();
                        Throwable thrown = log.getThrown();
                        if (thrown != null) {
                            for (String s : Throwables.toStringList(thrown)) {
                                msgT.append("<br />").append(s);
                            }
                        }

                        source = "" + log.getSource();
                    } catch (Exception e) {

                    }

                    logs.add(Map.of("msg", msg,
                            "throw", msgT.toString(),
                            "time", time,
                            "level", level,
                            "source", source
                    ));
                }
            }
        } catch (Exception e) {

        }
        return logs;
    }

    public LogEvent getLogAtual() {
        return logAtual;
    }

    public void setLogAtual(LogEvent logAtual) {
        this.logAtual = logAtual;
    }

    public void poupLogAtual(LogEvent logAtual) {
        this.logAtual = logAtual;
    }

    public String getStackTraceLogAtual() {
        StringBuilder sb = new StringBuilder();
        /*if (logAtual != null) {
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
        }*/
        return sb.toString();
    }

    public void restartSystem() {
        try {
            SpringMainConfig.restartApplication();
        } catch (Exception ex) {
            logutil.error("Fail on restart appt", ex);
        }
    }

    public void compileSystem() {
        String[] commandToExecute = new String[]{"/bin/bash", System.getProperty("user.home") + "/maxge/sh/compile.sh"};//getExecuteCommand(graph);
        executeCommand(commandToExecute);
    }

    public void updateSystem() {
        String[] commandToExecute = new String[]{"/bin/bash", System.getProperty("user.home") + "/maxge/sh/update.sh"};//getExecuteCommand(graph);
        executeCommand(commandToExecute);
    }

    protected void executeCommand(String[] commandToExecute) {
        Map<String, Object> response;
        try {
            logWebConsole.info(commandToExecute);
            //File execFile = new File(exec);
            //execFile.setExecutable(true);
            process = Runtime.getRuntime().exec(commandToExecute);
            InputStreamReader input = new InputStreamReader(process.getInputStream());
            BufferedReader reader = new BufferedReader(input);

            response = new HashMap<>();
            String lastLine = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                try {
                    if (line != null && !line.trim().isEmpty()) {
                        logWebConsole.info(line);
                        log.info(line);
                        String[] splits = line.split("=", 2);
                        if (splits != null && splits.length >= 2) {
                            response.put(splits[0].trim(), splits[1].trim());
                        }
                        lastLine = line;
                    }
                } catch (Exception e) {
                    logWebConsole.error("Error: " + e.getLocalizedMessage());
                    logWebConsole.error("fail on execute", e);
                    log.error("fail on execute", e);
                }
            }
            if (response.isEmpty()) {
                response.put("Result", lastLine);
            }
            int waitFor = process.waitFor();
            if (waitFor > 0) {
                response.put("Result", "Erro");
                try {
                    InputStreamReader inputError = new InputStreamReader(process.getErrorStream());
                    BufferedReader readerError = new BufferedReader(inputError);

                    String erorline = "";
                    while ((erorline = readerError.readLine()) != null) {

                        if (erorline != null && !erorline.trim().isEmpty()) {
                            logWebConsole.info("Error: " + erorline);
                        }

                    }
                } catch (Exception e) {
                    logWebConsole.error("error on update", e);
                    log.error("erro on update", e);
                }
            }
        } catch (IOException | InterruptedException ex) {
            logWebConsole.error("error", ex);
            log.error("erro on update", ex);
        }
    }

    @RequestMapping(value = {"system/console"},
            method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> processStatus(Long lastTime) {
        Map<String, Object> map = new HashMap<>();
        List<String> lines = new ArrayList<>();
        List<LogEvent> loggingEvents = null;

        long last = 0;

        if (lastTime != null && lastTime > 0) {
            loggingEvents = WebConsoleAppender.getLoggingEvents(lastTime);
            last = lastTime;
        } else {
            loggingEvents = WebConsoleAppender.getLoggingEvents();
        }

        if (loggingEvents != null) {
            for (LogEvent e : loggingEvents) {
                lines.add("" + e);
                /*Object message = e.getMessage();
                lines.add("" + message);
                if (e.getTimeStamp() > last) {
                    last = e.getTimeStamp();
                }*/
            }
        }

        map.put("processing", "");
        map.put("last", last);
        map.put("output", lines);
        map.put("result", "");
        return map;
    }
}
