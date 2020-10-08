/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.braully.business;

import static com.github.braully.business.DescriptorLayoutImportFile.SEPARADOR_PADRAO;
import com.github.braully.domain.BinaryFile;
import com.github.braully.persistence.GenericDAO;
import com.github.braully.persistence.IEntity;
import com.github.braully.constant.MimeTypeFile;
import com.github.braully.util.ReportGeneratorMSXExcel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.braully.util.UtilCloud;
import com.github.braully.util.UtilCollection;
import com.github.braully.util.UtilValidation;
import java.util.ArrayList;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author strike
 */
@Service
public class ImportFileProcessor extends BinaryFileProcessor {

    protected static final Logger log = LogManager.getLogger(ImportFileProcessor.class);
    //
    public static final String CAMINHO_ARQUIVO_IMPORTACAO = "arquivo" + File.separator + "importacao";
    public static final String DATABASE_DEFAULT = "default";
    public static final String TYPE_PROCESSOR = "Importação";
    //
    public static final int TAM_STR_PEQUENO = 25;
    public static final int TAM_STR_MEDIO = 50;
    public static final int TAM_STR = 100;
    //

    @Autowired(required = false)
    private SecurityContext securityContext;
    @Resource(name = "genericDAO")
    private GenericDAO genericDAO;

    @Autowired
    private List<DescriptorLayoutImportFile> descritoresArquivoImportacao;

    @Override
    public String getType() {
        return TYPE_PROCESSOR;
    }

    @Override
    public boolean isProcessable(BinaryFile arquivo) {
        boolean ret = false;
        if (arquivo != null && arquivo.getName() != null) {
            try {
                String extensaoArquivo = arquivo.getExtensaoArquivo();
                ret = MimeTypeFile.isXlsx(extensaoArquivo);
            } catch (Exception e) {

            }
        }
        return ret;
    }

    public Map<String, Object> extrairPropriedadesArquivo(BinaryFile arquivoBinario) {
        String extensaoArquivo = arquivoBinario.getExtensaoArquivo();
        MimeTypeFile tipoArquivo = MimeTypeFile.getTipoArquivo(extensaoArquivo);
        Map<String, Object> valores = null;
        if (tipoArquivo == null || tipoArquivo != MimeTypeFile.XLSX) {
            throw new IllegalArgumentException("Formato de arquivo não suportado: " + extensaoArquivo);
        }

        byte[] arquivo = arquivoBinario.getArquivo();

        if (arquivo == null || arquivo.length == 0) {
            throw new IllegalArgumentException("O arquivo está vazio");
        }

        if (tipoArquivo == MimeTypeFile.XLSX) {
            ReportGeneratorMSXExcel relatorio = new ReportGeneratorMSXExcel();
            valores = relatorio.extrairDados(arquivoBinario, DescriptorLayoutImportFile.PROPRIEDADES_LAYOUT);
        }
        return valores;
    }

    @Override
    public void processFile(BinaryFile arquivo) {
        if (descritoresArquivoImportacao != null) {
            boolean process = false;
            Map<String, Object> propriedadesArquivo = this.extrairPropriedadesArquivo(arquivo);
            for (DescriptorLayoutImportFile descriptor : descritoresArquivoImportacao) {
                if (descriptor.assinadoPara(propriedadesArquivo)) {
                    log.info("Processando: " + arquivo + " " + descriptor);
                    importar(arquivo, descriptor);
                    process = true;
                }
            }
            if (!process) {
                log.warn("Arquivo não processado: " + arquivo);
            }
        } else {
            log.info("Não existem descritores de arquivo");
        }
    }

    public DescriptorLayoutImportFile buscarDescritorLayout(String codigoLayout, String nomeArquivo) {
        return this.buscarDescritorLayout(codigoLayout, nomeArquivo, null);
    }

    public DescriptorLayoutImportFile buscarDescritorLayout(String codigoLayout,
            String nomeArquivo, Class classe) {
        DescriptorLayoutImportFile desc = null;
        if (codigoLayout != null && nomeArquivo != null
                && descritoresArquivoImportacao != null
                && !descritoresArquivoImportacao.isEmpty()) {
            Optional<DescriptorLayoutImportFile> compativel
                    = descritoresArquivoImportacao.stream()
                            .filter(d -> d.assinadoPara(codigoLayout, nomeArquivo, classe))
                            .findAny();
            if (compativel != null && compativel.isPresent()) {
                desc = compativel.get();
            }
        }
        return desc;
    }

    @Transactional
    public BinaryFile importarDadosArquitetura(InputStream in, String nomeArquivo) throws IOException {
        BinaryFile arq = null;
        if (in != null && in.available() > 0) {
            arq = new BinaryFile();
            arq.setNome(nomeArquivo);
            genericDAO.saveEntity(arq);
            String dataBaseNameFromSecurity = getDataBaseNameFromSecurity(securityContext);
            if (dataBaseNameFromSecurity == null || dataBaseNameFromSecurity.trim().isEmpty()) {
                dataBaseNameFromSecurity = DATABASE_DEFAULT;
            }
            String caminhoCompleto = dataBaseNameFromSecurity + File.separator + CAMINHO_ARQUIVO_IMPORTACAO;
            UtilCloud.salvarNuvem(in, caminhoCompleto, arq.getPathCloud());
            arq.setPathCloud(caminhoCompleto);
        }
        return arq;
    }

    private String getDataBaseNameFromSecurity(SecurityContext security) {
        String ret = null;
        try {
            if (security != null) {
                Authentication authentication = security.getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    Object details = authentication.getDetails();
                    if (details != null && details instanceof String) {
                        ret = (String) details;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Falha ao determinar dominio de banco de dados", e);
        }
        return ret;
    }

    //
    protected Collection parserArquivoPlanilhaDados(BinaryFile arquivoBinario, List<String> propriedades)
            throws IllegalArgumentException {
        String extensaoArquivo = arquivoBinario.getExtensaoArquivo();
        Collection colecaoDados = null;
        MimeTypeFile tipoArquivo = MimeTypeFile.getTipoArquivo(extensaoArquivo);
        if (tipoArquivo == null || tipoArquivo != MimeTypeFile.XLSX) {
            throw new IllegalArgumentException("Formato de arquivo não suportado: " + extensaoArquivo);
        }
        byte[] arquivo = arquivoBinario.getArquivo();
        if (arquivo == null || arquivo.length == 0) {
            throw new IllegalArgumentException("O arquivo está vazio");
        }
        if (tipoArquivo == MimeTypeFile.XLSX) {
            ReportGeneratorMSXExcel relatorio = new ReportGeneratorMSXExcel();
            colecaoDados = relatorio.extrairDadosTemplate(arquivo, propriedades);
        }
        if (colecaoDados == null || colecaoDados.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não contem dados para importação.");
        }
        return colecaoDados;
    }

    public InputStream exportarDadosArquitetura(Collection<? extends IEntity> entidades) throws IOException {
        InputStream in = null;
        if (entidades != null && !entidades.isEmpty()) {
            Object o = entidades.stream().findAny().get();
            DescriptorLayoutImportFile descritorLayout = this.buscarDescritorLayout(DATABASE_DEFAULT, SEPARADOR_PADRAO, o.getClass());
            if (descritorLayout == null) {
                throw new IllegalArgumentException("Não é possivel encontrar layot para o tipo de dado: " + o.getClass().getSimpleName());
            }
            in = this.exportarDadosArquitetura(entidades, descritorLayout);
        }
        return in;
    }

    public InputStream exportarDadosArquitetura(Collection<? extends IEntity> entidades,
            DescriptorLayoutImportFile descritorArquivo) throws IOException {
        InputStream ret = null;
        if (entidades == null || entidades.isEmpty()) {
            return ret;
        }
        ReportGeneratorMSXExcel relatorioMSExcel = new ReportGeneratorMSXExcel("WEB-INF/template/layout-exportacao-dados.xlsx");
        try {
            relatorioMSExcel.aplicarDescritor(descritorArquivo);
            entidades.stream().forEach((ent) -> {
                aplicarEntidadeTemplateExcel(ent, relatorioMSExcel, descritorArquivo);
            });
            ret = relatorioMSExcel.toStream();
        } catch (Exception e) {
            log.debug("Falha ao gerar arquivo", e);
        }
        return ret;
    }

    @Transactional
    public void aplicarEntidadeTemplateExcel(IEntity ent,
            ReportGeneratorMSXExcel relatorioMSExcel,
            DescriptorLayoutImportFile descritorArquivo) {
        IEntity tmp = (IEntity) this.genericDAO.loadEntity(ent);
        relatorioMSExcel.aplicarTemplate(tmp, descritorArquivo);
    }

    public void importar(BinaryFile arquivoBinario, DescriptorLayoutImportFile descritor) {
        String extensaoArquivo = arquivoBinario.getExtensaoArquivo();
        Collection colecaoDados = null;
        MimeTypeFile tipoArquivo = MimeTypeFile.getTipoArquivo(extensaoArquivo);
        IDescrpitorFieldLayout[] descritorCampos = descritor.getDescritorCampos();

        List<String> propriedades = new ArrayList<String>();
        for (IDescrpitorFieldLayout ci : descritorCampos) {
            propriedades.add(ci.getNomeColuna());
        }

        if (tipoArquivo == null || tipoArquivo != MimeTypeFile.XLSX) {
            throw new IllegalArgumentException("Formato de arquivo não suportado: " + extensaoArquivo);
        }

        byte[] arquivo = arquivoBinario.getArquivo();

        if (arquivo == null || arquivo.length == 0) {
            throw new IllegalArgumentException("O arquivo está vazio");
        }

        if (tipoArquivo == MimeTypeFile.XLSX) {
            ReportGeneratorMSXExcel relatorio = new ReportGeneratorMSXExcel();
            colecaoDados = relatorio.extrairDadosTemplate(arquivoBinario, propriedades);
        }

        if (colecaoDados == null || colecaoDados.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não contem dados para importação.");
        }

        for (Object o : colecaoDados) {
            if (o instanceof Object[]) {
                Object[] arr = (Object[]) o;
                try {
                    //descritor.importarSeTemElemento(arr);
                } catch (Exception e) {
                    log.info("fail on import data: " + UtilCollection.printArray(arr), e);
                }
            } else if (o instanceof Collection) {
                Collection list = null;
                try {
                    list = (Collection) o;
                    descritor.importarSeTemElemento(list);
                } catch (Exception e) {
                    log.info("fail on import data: " + UtilCollection.printCollection(list), e);
                }
            }
        }
        if (UtilValidation.isStringEmpty(arquivoBinario.getType())) {
            arquivoBinario.setType(this.getType());
            arquivoBinario.setSubtype(descritor.getTipo());
        }
    }
}
