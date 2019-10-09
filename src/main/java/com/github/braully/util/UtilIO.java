package com.github.braully.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author braullyrocha
 */
public class UtilIO {

    private static final org.apache.log4j.Logger log = org.apache.log4j.LogManager.getLogger(UtilComparator.class);

    /* */
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final String PDF_EXTENSION = ".pdf";

    public static byte[] loadFile(String pathImagem) throws FileNotFoundException, IOException {
        byte[] buffSaida = null;
        File file = new File(pathImagem);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int l;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

            while ((l = fis.read(buffer)) > 0) {
                baos.write(buffer, 0, l);
            }
            fis.close();
            baos.flush();
            buffSaida = baos.toByteArray();
            baos.close();
        } else if (pathImagem.contains("jar!")) {
            String jarurl = "jar:" + pathImagem;
            System.out.println("Url: " + pathImagem);
            System.out.println("Jar-Url: " + jarurl);
            URL url = new URL(jarurl);
            URLConnection openConnection = url.openConnection();
            buffSaida = loadStream(openConnection.getInputStream());
        }
        return buffSaida;
    }

    public static byte[] loadStream(InputStream stream) throws FileNotFoundException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int l;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        while ((l = stream.read(buffer)) > 0) {
            baos.write(buffer, 0, l);
        }

        baos.flush();
        byte[] buffSaida = baos.toByteArray();
        baos.close();
        return buffSaida;
    }

    public static String loadFileFromPathAsString(String pathImagem) throws FileNotFoundException, IOException {
        byte[] loadFile = loadFile(UtilPath.getPath(pathImagem));
        return new String(loadFile);
    }

    public static byte[] loadFileFromPath(String pathImagem) throws FileNotFoundException, IOException {
        return loadFile(UtilPath.getPath(pathImagem));
    }

    /**
     * Função auxiliar para save imagem ou um arquivo no sistema de arquivos
     *
     * @param nomeFile Nome do arquivo para ser salvo
     * @param buff Buffer quer será escrito no arquivo
     * @throws FileNotFoundException
     * @throws IOException
     */
    public synchronized static void saveFile(String nomeFile, byte[] buff) throws FileNotFoundException, IOException {
        log.info("Salvando arquivo: " + nomeFile);
        FileOutputStream out = new FileOutputStream(nomeFile);
        out.write(buff);
        out.flush();
        out.close();
    }

    public synchronized static void saveFile(String nomeFile, InputStream in) throws FileNotFoundException, IOException {
        log.info("Salvando arquivo: " + nomeFile);
        FileOutputStream out = new FileOutputStream(nomeFile);
        copy(in, out);
        out.flush();
        out.close();
        in.close();
    }

    public static String findFile(String pathFileResultado) throws FileNotFoundException {
        URL pathArqTeste = Thread.currentThread().getContextClassLoader().getResource(pathFileResultado);
        if (pathArqTeste == null) {
            throw new FileNotFoundException(pathFileResultado);
        }
        return pathArqTeste.getPath();
    }

    public synchronized static void saveObject(String path, Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(baos);
        os.writeObject(object);
        os.flush();
        os.close();
        baos.flush();
        baos.close();
        byte[] toByteArray = baos.toByteArray();
        saveFile(path, toByteArray);
    }

    public synchronized static Object loadObject(String path) throws IOException, ClassNotFoundException {
        byte[] arquivo = loadFile(path);
        ByteArrayInputStream bais = new ByteArrayInputStream(arquivo);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object object = ois.readObject();
        ois.close();
        bais.close();
        return object;
    }

    public static void generateZip(byte[] array) {
    }

    public static void generateZip(List<byte[]> listArray) {
    }

    public static byte[] generateZip(Map<String, byte[]> mapArray) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(out);

            zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);
            for (Map.Entry<String, byte[]> entrySet : mapArray.entrySet()) {
                zipOut.putNextEntry(new ZipEntry(entrySet.getKey() + PDF_EXTENSION));
                zipOut.write(entrySet.getValue());
                zipOut.closeEntry();
            }
            out.close();
            zipOut.close();
            return out.toByteArray();
        } catch (IllegalArgumentException iae) {
            log.error("erro", iae);
            throw iae;
        } catch (Exception e) {
            log.error("erro", e);
            throw new RuntimeException(e);
        }
    }

    public static void mkdirs(String dir) {
        new File(dir).mkdirs();
    }

    public static InputStream loadStreamFromFilePath(String string) throws FileNotFoundException {
        String path = string;

        try {
            path = UtilPath.getPath(string);
        } catch (Exception e) {
            log.debug("File not found on path " + string, e);
        }

        return new FileInputStream(path);

    }

    public static void copy(InputStream stream, OutputStream out) throws IOException {
        byte[] buff = new byte[DEFAULT_BUFFER_SIZE];
        int count;
        while ((count = stream.read(buff)) != -1 && count > 0) {
            out.write(buff, 0, count);
        }
    }

    public static void close(Closeable stream) {
        try {
            stream.close();
        } catch (Exception exc) {
            log.error("Falha ao fechar estream", exc);
        }
    }

    public static String homeUserDir() {
        return System.getProperty("user.home");
    }

    public static InputStream outputStreamToInput(ByteArrayOutputStream baos) {
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
