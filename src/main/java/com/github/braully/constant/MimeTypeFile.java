package com.github.braully.constant;

import com.github.braully.app.logutil;

public enum MimeTypeFile {

    PDF("application/pdf"),
    XLS("application/msexcel"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    ODS("application/vnd.oasis.opendocument.text"),
    CSV("text/csv"), ODT("application/vnd.oasis.opendocument.text"),
    DOC("application/msword"), DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    JASPER("application/jasper"), TXT("text/plain");

    private MimeTypeFile(String mime) {
        this.mime = mime;
    }

    private final String mime;

    public String getMime() {
        return mime;
    }

    public static MimeTypeFile getTipoArquivo(String string) {
        MimeTypeFile tipoArquivo = null;
        if (string != null) {
            string = string.toLowerCase();
        }
        if (string.endsWith("odt")) {
            tipoArquivo = ODT;
        } else if (string.endsWith("pdf")) {
            tipoArquivo = PDF;
        } else if (string.endsWith("ods")) {
            tipoArquivo = ODS;
        } else if (string.endsWith("csv")) {
            tipoArquivo = CSV;
        } else if (string.endsWith("doc")) {
            tipoArquivo = DOC;
        } else if (string.endsWith("docx")) {
            tipoArquivo = DOCX;
        } else if (string.endsWith("xls")) {
            tipoArquivo = XLS;
        } else if (string.endsWith("xlsx")) {
            tipoArquivo = XLSX;
        } else if (string.endsWith("jasper")) {
            tipoArquivo = JASPER;
        } else if (string.endsWith("txt")) {
            tipoArquivo = TXT;
        } else {
            throw new IllegalArgumentException("Arquivo não suportado");
        }
        return tipoArquivo;
    }

    public static boolean isOfx(String extensaoArquivo) {
        if (extensaoArquivo != null) {
            return extensaoArquivo.toLowerCase().endsWith("ofx");
        }
        return false;
    }

    public static boolean isXlsx(String extensaoArquivo) {
        if (extensaoArquivo != null) {
            return extensaoArquivo.toLowerCase().endsWith("xlsx");
        }
        return false;
    }

    public boolean isXlsx() {
        return this == XLS;
    }

}
