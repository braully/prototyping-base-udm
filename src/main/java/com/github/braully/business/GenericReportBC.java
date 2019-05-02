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
package com.github.braully.business;

import com.github.braully.persistence.GenericDAO;
import com.github.braully.constant.ReportType;
import com.github.braully.domain.Organization;
import com.github.braully.domain.util.ReportTemplate;
import com.github.braully.util.GenericReport;
import com.github.braully.util.UtilPath;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braully
 */
@Service
public class GenericReportBC implements IGenericReportBC {

    private static final Logger log = Logger.getLogger(GenericReportBC.class);

    @Resource(name = "genericDAO")
    private GenericDAO genericoDAO;

    @Resource//(name = "userDataSource")
    private DataSource userDataSource;

    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> gerarParametrosPadraoRelatorio() {
//        return this.gerarParametrosInstituicaoRelatorio(this.instituicaoDAO.getInstituicaoPrincipal());
        return this.gerarParametrosInstituicaoRelatorio(null);
    }

    @Override
    public Map<String, Object> gerarParametrosInstituicaoRelatorio(Organization inst) {
        Map<String, Object> parametros = new HashMap<String, Object>();
        String detalhesEmpresa = "";
        if (inst != null) {
            detalhesEmpresa = inst.getDescription();

        }
        parametros.put("DETALHES_EMPRESA", detalhesEmpresa);
        parametros.put("CABECALHO", detalhesEmpresa);
        if (inst != null) {
            byte[] logo = inst.getImagem();
            if (logo != null) {
                parametros.put("LOGO_INSTITUICAO", logo);
            }
        }

        try {
            parametros.put("USUARIO", SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (Exception e) {
        }
        return parametros;
    }

    //TODO: Generalizar com componente de relatorio
    @Override
    public byte[] gerarRelatorioPdfJasperConexaoPadrao(String caminhoRelatorio, Map<String, Object> parametrosPersonalizados) {

        byte[] saida = null;
        try {

            Map<String, Object> parametrosPadraoRelatorio = this.gerarParametrosPadraoRelatorio();
            if (parametrosPadraoRelatorio == null) {
                parametrosPadraoRelatorio = new HashMap<String, Object>();
            }

            if (parametrosPersonalizados != null) {
                parametrosPadraoRelatorio.putAll(parametrosPersonalizados);
            }

            String caminho = UtilPath.getPath(caminhoRelatorio);
            Connection conexao = userDataSource.getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(caminho, parametrosPadraoRelatorio, conexao);

            ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);
            pdfStream.flush();
            saida = pdfStream.toByteArray();
        } catch (Exception e) {
            log.error("Falha ao gerar relatorio padrao ao gerar relatorio", e);
            throw new RuntimeException("Falha ao gerar relatorio padrao ao gerar relatorio", e);
        }
        return saida;
    }

    @Override
    public List<ReportTemplate> busarRelatoriosDoTipo(ReportType tipoRelatorio) {
        List<ReportTemplate> relatorios = null;
        if (tipoRelatorio != null) {
            relatorios = genericoDAO.loadCollectionWhere(ReportTemplate.class, "tipoRelatorio", tipoRelatorio);
        }
        return relatorios;
    }

    @Override
    public byte[] gerarRelatorioGenerico(GenericReport relatorioGenericoCursivo)
            throws Exception {
        if (relatorioGenericoCursivo != null) {
            return this.gerarRelatorioGenerico(relatorioGenericoCursivo, relatorioGenericoCursivo.getTitulo(),
                    relatorioGenericoCursivo.getSubtitulo(), relatorioGenericoCursivo.getIntroducao(),
                    relatorioGenericoCursivo.getDado(), relatorioGenericoCursivo.getParametros());
        }
        return null;
    }

    @Override
    public byte[] gerarRelatorioGenerico(GenericReport relatorioGenericoCursivo,
            String titulo, String subtitulo,
            String introducao, Object dado, Map parametros) throws Exception {
        byte[] ret = null;
        Map pars = new HashMap();
        if (relatorioGenericoCursivo != null) {
            Map<String, Object> params = this.gerarParametrosPadraoRelatorio();
            if (parametros != null) {
                pars.putAll(parametros);
            }
            if (params != null) {
                pars.putAll(params);
            }
            ret = relatorioGenericoCursivo.gerarRelatorioPDFByte(titulo, subtitulo, introducao, dado, pars);
        }
        return ret;
    }
}
