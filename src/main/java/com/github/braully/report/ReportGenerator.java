package com.github.braully.report;

import com.github.braully.domain.util.ClassDiscover;
import com.github.braully.domain.util.ReportTemplate;
import com.github.braully.interfaces.IRelatorioMisto;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ReportGenerator implements IRelatorioMisto {

    public static final String PACOTE_BUSCA_GERADORES = "br.com.github.braully.report";

    private static final Logger log = LogManager.getLogger(ReportGenerator.class);
    /**
     *
     */
    public static final String NOME_CAMPO = "GERADOR_TIPO";

    @Override
    public abstract byte[] gerarRelatorioByte(ReportTemplate relatorio, Map param, Collection colecao);

    public byte[] gerarRelatorioByte(ReportTemplate relatorio, Map param, Object bean) {
        return this.gerarRelatorioByte(relatorio, param, null);
    }

    @Override
    public byte[] gerarRelatorioByte(ReportTemplate relatorio, Map param) {
        return this.gerarRelatorioByte(relatorio, param, null);
    }

    public byte[] gerarRelatorioByte(JasperPrint jasperPrint) throws JRException, IOException {
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);
        pdfStream.flush();
        byte[] saida = pdfStream.toByteArray();
        return saida;
    }

    public abstract InputStream generate(GenericDataReport datareport);

    @Override
    public byte[] gerarRelatorioByte(ReportTemplate relatorio, Map paramSimples, Map paramBean, Collection colecao) {
        Map parametrosFim = new HashMap();
        if (paramSimples != null) {
            parametrosFim.putAll(paramSimples);
        }
        if (paramBean != null) {
            parametrosFim.putAll(paramBean);
        }
        return this.gerarRelatorioByte(relatorio, parametrosFim, colecao);
    }

    public static synchronized ReportGenerator getGerador(ReportTemplate relatorio) {
        ReportGenerator gr = null;
        String ext = relatorio.getExtensaoArquivo();
        if (relatorio.isStatico()) {
            try {
                Class classRelatorio = Class.forName(relatorio.getClasseStatica());
                gr = (ReportGenerator) classRelatorio.newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                log.error("erro", e);
            }
        } else {
            try {
                ClassDiscover discoClass = new ClassDiscover();
                gr = discoClass.getInstancia(PACOTE_BUSCA_GERADORES, ReportGenerator.class, NOME_CAMPO, ext);
            } catch (RuntimeException e) {
                log.error("erro", e);
                if (".jasper".equalsIgnoreCase(ext.trim())) {
                    //FIXME: Dependencia ciclica de classe
//                    gr = new ReportJasper();
                }
            }
        }
        return gr;
    }

    public String resolverCaminhoRecurso(String recurso) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url = classLoader.getResource(recurso);

        String path = url.getPath();

        String caminhoWar = path.substring(0, path.indexOf(".war") + 4);
        if (caminhoWar.contains(".war") && !new File(caminhoWar).isDirectory()) {
            path = gerarArquivoTemporario(recurso);
        }
        return path;
    }

    private String gerarArquivoTemporario(String recurso) {
        String path = null;
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(recurso);
            if (is != null) {
                File fileTmp = File.createTempFile(recurso.replace("/", "-"), ".tmp");
                FileOutputStream fos = new FileOutputStream(fileTmp);
                byte[] buffer = new byte[100];
                int tam = 0;
                while ((tam = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, tam);
                }
                fos.close();
                is.close();
                path = fileTmp.getAbsolutePath();
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return path;
    }

}
