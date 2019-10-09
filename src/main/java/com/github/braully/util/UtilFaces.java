package com.github.braully.util;

import com.github.braully.constant.MimeTypeFile;
import com.github.braully.web.jsf.MessageUtilJSF;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author strike
 */
public class UtilFaces extends MessageUtilJSF {

    public static void sendFileToClient(File pdfAsFile, String string) {
        try {
            mostrarArquivo(pdfAsFile, string);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Legacy
    private static final int DEFAULT_BUFFER_SIZE = 512;

    public synchronized static void enviarMultiplosArquivoZipado(String nomeArquivoFinal, String offsetDir, Iterable<String> caminhos) {
        ZipOutputStream output = null;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivoFinal + ".zip\"");
            output = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE));
            response.setContentType("application/zip");

            for (String fileId : caminhos) {
                InputStream input = null;
                try {
                    String fileName = fileId;
                    File file = new File(offsetDir, fileId);
                    input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
                    output.putNextEntry(new ZipEntry(fileName));
                    for (int length = 0; (length = input.read(buffer)) > 0;) {
                        output.write(buffer, 0, length);
                    }
                    output.closeEntry();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException logOrIgnore) {
                            /**/ }
                    }
                }
            }
        } catch (IOException ex) {
            UtilLog.error(ex);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException logOrIgnore) {
                    /**/ }
            }
        }
    }

    public synchronized static void mostrarArquivo(byte[] arquivo, String mimeType, String fileName)
            throws IOException {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
            //            fileName = fileName.replaceAll(" ", "\\ ");
            String attach = "attachment; filename=\"" + fileName + "\"";
            response.setHeader("Content-disposition", attach);
            response.setContentType(mimeType);
            OutputStream out = response.getOutputStream();
            out.write(arquivo);
            out.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            ctx.responseComplete();
        }
    }

    public static void mostrarArquivo(InputStream in, String mimeType, String fileName)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int dado;
        while ((dado = in.read()) != -1) {
            out.write(dado);
        }
        out.flush();
        byte[] dados = out.toByteArray();
        mostrarArquivo(dados, mimeType, fileName);
        in.close();
    }

    public static void mostrarArquivo(InputStream in, String string)
            throws IOException {
        String tipoArquivo = getTipoArquivo(string);
        mostrarArquivo(in, tipoArquivo, string);
    }

    private static String getTipoArquivo(String string) {
        String tipoArquivo = null;
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("Nome de arquivo invalido");
        }
        MimeTypeFile tipoArquivo1 = MimeTypeFile.getTipoArquivo(string);
        if (tipoArquivo1 != null) {
            tipoArquivo = tipoArquivo1.getMime();
        }
        return tipoArquivo;
    }

    public static void mostrarArquivo(File outFile, String string)
            throws FileNotFoundException, IOException {
        mostrarArquivo(new FileInputStream(outFile), string);
    }

    public static void mostrarArquivo(byte[] dados, String string)
            throws IOException {
        mostrarArquivo(dados, getTipoArquivo(string),
                string);
    }

    public static void sendFileToClient(byte[] pdfs, String boletospdf) {
        try {
            mostrarArquivo(pdfs, boletospdf);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
