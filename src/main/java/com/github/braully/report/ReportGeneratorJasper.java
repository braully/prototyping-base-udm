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
package com.github.braully.report;

import com.github.braully.domain.ReportTemplate;
import com.github.braully.interfaces.IReportGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author braullyrocha
 */
public class ReportGeneratorJasper implements IReportGenerator {

    public static final String GERADOR_TIPO = ".jasper";
    private static final Logger log = LogManager.getLogger(ReportGenerator.class);

    public JasperPrint gerarRelatorio(String caminhoArquivo, Map paramentros,
            Collection colecao) throws JRException {
        JasperPrint jasperPrint = JasperFillManager.fillReport(caminhoArquivo,
                paramentros, new JRBeanCollectionDataSource(colecao));
        return jasperPrint;
    }

    public byte[] gerarRelatorioByte(String caminhoArquivo, String caminhoSubReport,
            Map paramentros, Collection colecao) throws JRException, IOException {
        if (caminhoSubReport != null && !paramentros.containsKey("SUBREPORT_DIR")) {
            try {
                String dirReport = caminhoSubReport.substring(0, caminhoSubReport.lastIndexOf("/") + 1);
                paramentros.put("SUBREPORT_DIR", dirReport);
            } catch (RuntimeException e) {
                log.error("erro", e);
            }
        }
        return gerarRelatorioByte(caminhoArquivo, paramentros, colecao);
    }

    public byte[] gerarRelatorioByte(String caminhoArquivo, Map paramentros,
            Collection colecao) throws JRException, IOException {
        JasperPrint jasperPrint = JasperFillManager.fillReport(caminhoArquivo,
                paramentros, new JRBeanCollectionDataSource(colecao));
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);
        pdfStream.flush();
        byte[] saida = pdfStream.toByteArray();
        return saida;
    }

    public byte[] gerarRelatorioByte(ReportTemplate relatorio, Map param, Collection colecao) {
        byte[] saida = null;
        try {
            relatorio.getNomeArquivo();
            String extensao = relatorio.getExtensaoArquivo();
            JasperPrint jasperPrint = JasperFillManager.fillReport(new ByteArrayInputStream(relatorio.getRelatorio()),
                    param, new JRBeanCollectionDataSource(colecao));
            ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);
            pdfStream.flush();
            saida = pdfStream.toByteArray();
        } catch (JRException | IOException e) {
            log.error("Erro ao gerar relatorio", e);
            throw new RuntimeException(e);
        }
        return saida;
    }

    public boolean assinadoPara(String nomeRelatorio) {
        return nomeRelatorio != null && nomeRelatorio.endsWith(GERADOR_TIPO);
    }

    public JasperPrint gerarRelatorioJasperPrint(ReportTemplate relatorio, Map param, Object bean) {
        JasperPrint jp = null;
//        try {
//        } catch (Exception e) {
//            log.error("", e);
//        }
        return jp;
    }

    public static InputStream jasperPrintToInputStream(JasperPrint jasperPrint) throws JRException, IOException {
        InputStream input;
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);
        pdfStream.flush();
        input = new ByteArrayInputStream(pdfStream.toByteArray());
        return input;
    }

    InputStream generatePDFStream(GenericDataReport dataReport) {
        InputStream input = dataReport.getTemplateStream();
        InputStream inputStream = null;
        JasperPrint jasperPrint;
        //TODO: Improv memory performance, use temporary file to stream

        try {
            jasperPrint = JasperFillManager.fillReport(input,
                    dataReport.getAllProperties(),
                    new JRBeanCollectionDataSource(dataReport.getCollectionData()));
            inputStream = jasperPrintToInputStream(jasperPrint);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao gerar relatorio ", ex);
        }
        return inputStream;
    }

    @Override
    public InputStream generate(GenericDataReport datareport) {
        return this.generatePDFStream(datareport);
    }
}
