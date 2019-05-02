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
package com.github.braully.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author normal
 */
public class UtilCloud {

    //TODO: Translate and refactor
    public static final String CAMINHO_DIRETORIO_ARMAZENAMENTO_NUVEM;
    public static final String CAMINHO_DEFAULT = System.getProperty("user.home") + File.separator + ".erp-brs" + File.separator + "nuvem";

    static {
        String tmp = UtilProperty.getProperty("application.properties", "caminhoDiretorioNuvem");
        if (tmp == null || tmp.trim().isEmpty()) {
            tmp = CAMINHO_DEFAULT;
        }
        CAMINHO_DIRETORIO_ARMAZENAMENTO_NUVEM = tmp;
    }

    public static synchronized void salvarNuvem(InputStream stream, String diretorio, String nomeArquivo) throws IOException {
        if (stream != null && stream.available() > 0) {
            String dirFinal = CAMINHO_DIRETORIO_ARMAZENAMENTO_NUVEM + File.separator + diretorio;
            File dir = new File(dirFinal);
            dir.mkdirs();
            String strCaminhoFinal = dirFinal.concat(File.separator).concat(nomeArquivo);
            salvarNuvemCaminhoFinal(strCaminhoFinal, stream);
        }
    }

    public static synchronized void salvarNuvem(InputStream stream, String nomeArquivo) throws IOException {
        if (stream != null && stream.available() > 0) {
            String strCaminhoFinal = CAMINHO_DIRETORIO_ARMAZENAMENTO_NUVEM + File.separator + nomeArquivo;
            salvarNuvemCaminhoFinal(strCaminhoFinal, stream);
        }
    }

    public static void salvarNuvemCaminhoFinal(String strCaminhoFinal, InputStream stream) throws IOException, FileNotFoundException {
        OutputStream outstream = new FileOutputStream(strCaminhoFinal);
        UtilIO.copy(stream, outstream);
        UtilIO.close(stream);
        UtilIO.close(outstream);
    }

    public static synchronized InputStream donwloadNuvem(String caminhoArquivo) throws FileNotFoundException {
        InputStream in = null;
        if (caminhoArquivo != null && !caminhoArquivo.trim().isEmpty()) {
            String strCaminhoFinal = CAMINHO_DIRETORIO_ARMAZENAMENTO_NUVEM + File.separator + caminhoArquivo;
            in = donwloadNuvemCaminhoFinal(strCaminhoFinal);
        }
        return in;
    }

    public static synchronized InputStream donwloadNuvemCaminhoFinal(String caminhoCompletoArquivo) throws FileNotFoundException {
        InputStream in = null;
        in = new FileInputStream(caminhoCompletoArquivo);
        return in;
    }

    public static synchronized void removerNuvem(String nomeArquivo, String fonteDados) {

    }

}
