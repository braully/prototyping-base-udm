/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.braully.report;

import com.github.braully.interfaces.IReportGenerator;
import com.github.braully.util.UtilIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 *
 * @author braully
 */
public class ReportGeneratorMSXOffice implements IReportGenerator {

    public static final String GERADOR_TIPO = ".docx";
    private static final Logger log = LogManager.getLogger(ReportGeneratorMSXOffice.class);

    public byte[] aplicarTemplateTabela(byte[] template, Map<String, String> props, Collection colecao) throws IOException {
        return aplicarTemplate(template, null, props, colecao);
    }

    private void aplicarTemplateTabela(XWPFTable table, Collection<String> keySet, Map<String, String> props, Collection colecao) {
        log.debug("Tabble");
        List<XWPFTableRow> rows = table.getRows();
        XWPFTableRow rowTemplate = null;
        int tam = rows.size();

        boolean comecoTemplate = false;
        int indiceTemplate = -1;
        for (int i = 0; i < tam; i++) {
            XWPFTableRow row = rows.get(i);
            comecoTemplate = false;
            try {
                comecoTemplate = isInicioTemplate(row, keySet);
                if (comecoTemplate) {
                    rowTemplate = row;
                    indiceTemplate = i;
                    break;
                }
            } catch (Exception e) {
            }
        }

        if (comecoTemplate) {
            for (int i = 0; i < colecao.size() - 1; i++) {
                table.addRow(rowTemplate);
            }
            for (Object bean : colecao) {
                XWPFTableRow row = rows.get(indiceTemplate++);
                List<XWPFTableCell> tableCells = row.getTableCells();
                for (XWPFTableCell cell : tableCells) {
                    String text = cell.getText();
                    log.trace("Cell-text=" + text);
                    List<XWPFParagraph> paragraphs = cell.getParagraphs();
                    if (paragraphs != null) {
                        for (XWPFParagraph paragraph : paragraphs) {
                            aplicarTemplateParagraph(keySet, paragraph, props, bean);
                        }
                    }
                }
            }
        }
    }

    private void aplicarTemplateTabela(XWPFTable table, Collection<String> keySet, Map<String, String> props) {
        log.info("Tabble");
        List<XWPFTableRow> rows = table.getRows();
        int tam = rows.size();

        for (int i = 0; i < tam; i++) {
            XWPFTableRow row = rows.get(i);
            List<XWPFTableCell> tableCells = row.getTableCells();
            for (XWPFTableCell cell : tableCells) {
                String text = cell.getText();
                log.trace("Cell-text=" + text);
                List<XWPFParagraph> paragraphs = cell.getParagraphs();
                if (paragraphs != null) {
                    for (XWPFParagraph paragraph : paragraphs) {
                        aplicarTemplateParagraph(keySet, paragraph, props);
                    }
                }
            }
        }
    }

    public byte[] aplicarTemplate(byte[] template, Map<String, String> props,
            Map<String, String> prosBean,
            Collection colecao) throws IOException {
        return UtilIO.loadStream(aplicarTemplate(new ByteArrayInputStream(template), props, prosBean, colecao));
    }

    public InputStream aplicarTemplate(InputStream stream, Map<String, String> props,
            Map<String, String> prosBean, Collection colecao) throws IOException {
        byte[] arquivo = null;
        XWPFDocument document = new XWPFDocument(stream);
        Collection<String> keySet = keySetOrdered(props);
        boolean aplicarTemplateSimples = props != null && !props.isEmpty();

        if (aplicarTemplateSimples) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                aplicarTemplateParagraph(keySet, paragraph, props);
            }
        }

        boolean aplicarTemplateComposto = prosBean != null && !prosBean.isEmpty() && colecao != null;

        Collection<String> keySetBean = null;

        if (prosBean != null) {
            keySetBean = keySetOrdered(prosBean);
        }

        List<XWPFTable> tables = document.getTables();
        if (tables != null) {
            for (XWPFTable table : tables) {
                aplicarTemplateTabela(table, keySet, props);
                if (aplicarTemplateComposto) {
                    aplicarTemplateTabela(table, keySetBean, prosBean, colecao);
                }
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.write(baos);
        baos.flush();
        return UtilIO.outputStreamToInput(baos);
    }

    private void aplicarSubstituicaoParagrafo(XWPFParagraph paragraph, String key, String value) {
//        if (value.contains("\n")) {
//            value = value.replaceAll("\n", "\r\n");
//        }
        String text = paragraph.getText();
        Queue<Integer> posicoes = new LinkedList<Integer>();
        int i = 0;
        while ((i = text.indexOf(key, i)) >= 0) {
            posicoes.add(i);
            i++;
        }
        List<XWPFRun> runs = paragraph.getRuns();
        int iniChar = 0;
        int sobra = 0;
        int tamanho = runs.size();
        for (i = 0; i < tamanho && !posicoes.isEmpty(); i++) {
            XWPFRun charRun = runs.get(i);
            text = charRun.getText(0);

            if (text != null) {
                int keyLength = key.length();
                int textLength = text.length();

                log.trace("Runt-text=" + text);
                int proxPos = posicoes.element();
                int fimChar = iniChar + textLength;

                if (sobra > 0) {
                    if (iniChar + sobra > fimChar) {
                        charRun.setText("", 0);
                        sobra = sobra - textLength;
                    } else {
                        text = text.substring(sobra);
                        replaceInCharRun(charRun, text);
                        sobra = 0;
                        posicoes.remove();
                    }
                }

                if (proxPos >= iniChar && proxPos < fimChar) {
                    if (iniChar + keyLength <= fimChar) {
//                    if (proxPos + keyLength <= fimChar) {
                        text = text.replace(key, value);
                        replaceInCharRun(charRun, text);
                        posicoes.remove();
                    } else {
                        int inidice = proxPos - iniChar;
                        sobra = keyLength - (fimChar - proxPos);
                        replaceInCharRun(charRun, text.substring(0, inidice) + value);
                    }
                }
                iniChar = fimChar;
            }
        }
    }

    private void aplicarTemplateParagraph(Collection<String> keySet, XWPFParagraph paragraph, Map<String, String> props) {
        String text = paragraph.getText();
        log.trace("Paraghraph-text=" + text);

        for (String str : keySet) {
            if (text.contains(str)) {
                aplicarSubstituicaoParagrafo(paragraph, str, props.get(str));
            }
        }
    }

    private void aplicarTemplateParagraph(Collection<String> keySet, XWPFParagraph paragraph, Map<String, String> props, Object bean) {
        String text = paragraph.getText();
        log.trace("Paraghraph-text=" + text);
        List<XWPFRun> runs = paragraph.getRuns();
        for (XWPFRun charRun : runs) {
            text = charRun.getText(0);
            log.trace("Runt-text=" + text);
            if (text != null) {
                Iterator<String> keySetIterator = keySet.iterator();
                while (keySetIterator.hasNext()) {
                    String key = keySetIterator.next();
                    if (text.contains(key)) {
                        try {
                            Object val = PropertyUtils.getProperty(bean, props.get(key));
                            if (val != null) {
                                String value = val.toString();
                                text = text.replaceAll(key, value);
                                charRun.setText(text, 0);
                            }
                        } catch (Exception e) {
                        }
                    }
                    //TODO: Verificar casamento com agrupamentos de xwpfruns anteriores
                }
            }
        }
    }

    private boolean isInicioTemplate(XWPFTableRow row, Collection<String> keySet) {
        boolean ret = false;
        List<XWPFTableCell> tableCells = row.getTableCells();
        for (XWPFTableCell cell : tableCells) {
            String text = cell.getText();
            Iterator<String> keySetIterator = keySet.iterator();
            while (keySetIterator.hasNext()) {
                String key = keySetIterator.next();
                if (text.contains(key)) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

    private void replaceInCharRun(XWPFRun charRun, String text) {
        if (text.contains("\n")) {
            String[] lines = text.split("\n");
            charRun.setText(lines[0], 0); // set first line into XWPFRun
            for (int i = 1; i < lines.length; i++) {
                // add break and insert new text
                charRun.addBreak();
                charRun.setText(lines[i]);
            }
        } else {
            charRun.setText(text, 0);
        }
    }

    public InputStream generateDocxStream(GenericDataReport dataReport) {
        InputStream input = null;

        try {
            input = this.aplicarTemplate(dataReport.getTemplateStream(), dataReport.getParametros(), null, null);
        } catch (Exception ex) {
            log.error("Fail on generate file", ex);
            throw new IllegalArgumentException("Fail on generate file", ex);
        }

        return input;
    }

    @Override
    public InputStream generate(GenericDataReport datareport) {
        return this.generateDocxStream(datareport);
    }

    private Collection<String> keySetOrdered(Map<String, String> props) {
        List<String> keylist = new ArrayList<>();
        if (props != null) {
            keylist.addAll(props.keySet());
            keylist.sort(new Comparator<String>() {
                @Override
                public int compare(String t, String t1) {
                    int tl = 0, t1l = 0;
                    if (t != null) {
                        tl = t.length();
                    }
                    if (t1 != null) {
                        t1l = t1.length();
                    }
                    return Integer.compare(t1l, tl);
                }
            });
        }
        return keylist;
    }
}
