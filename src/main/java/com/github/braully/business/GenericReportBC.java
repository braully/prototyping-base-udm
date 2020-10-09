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
import com.github.braully.domain.util.ReportProperty;
import com.github.braully.report.report_properties;
import com.github.braully.domain.util.ReportTemplate;
import com.github.braully.interfaces.IFormatable;
import com.github.braully.interfaces.IOrganiztionEntityDependent;
import com.github.braully.report.GenericDataReport;
import com.github.braully.report.GenericReportGenerator;
import com.github.braully.report.ReportGeneratorDynamic;
import com.github.braully.util.UtilDate;
import com.github.braully.util.UtilPath;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braully
 */
@Service
public class GenericReportBC {

    private static final Logger log = LogManager.getLogger(GenericReportBC.class);

    @Resource(name = "genericDAO")
    private GenericDAO genericoDAO;

    @Resource//(name = "userDataSource")
    private DataSource userDataSource;

    GenericReportGenerator genericReportGenerator = new GenericReportGenerator();

    @Transactional(readOnly = true)
    public Map<String, Object> gerarParametrosPadraoRelatorio() {
//        return this.gerarParametrosInstituicaoRelatorio(this.instituicaoDAO.getInstituicaoPrincipal());
        return this.gerarParametrosInstituicaoRelatorio(null);
    }

    private Map<String, Object> gerarParametrosPadrao() {
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("DETALHES_EMPRESA", "");
        parametros.put("CABECALHO", "");
        try {
            parametros.put("USUARIO", SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (Exception e) {
        }
        parametros.put("DATA_ATUAL", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        parametros.put("DATA_HORA_ATUAL", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
        return parametros;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> gerarParametrosInstituicaoRelatorio(Organization inst) {
        Map<String, Object> parametros = new HashMap<String, Object>();
        String detalhesEmpresa = "";
        byte[] logo = null;
        if (inst != null) {
            inst = this.genericoDAO.loadEntity(inst);
            detalhesEmpresa = inst.getDescription();
            try {
                logo = inst.getImagem();
            } catch (Exception e) {

            }

            //
            //parametros.put("instituição.nome", inst.getName());
            parametros.put("instituicao.nome", inst.getName());
            //parametros.put("instituição.cnpj", inst.getCnpjFormatado());
            parametros.put("instituicao.cnpj", inst.getCnpjFormatado());
            //
            String instEndereco = "";
            try {
                instEndereco = inst.getMainAddress().getCity().format();
            } catch (Exception e) {
            }
            //parametros.put("instituição.endereço.cidade", instEndereco);
            parametros.put("instituicao.endereco.cidade", instEndereco);
            try {
                instEndereco = inst.getMainAddress().format();
            } catch (Exception e) {
            }
            //
            //parametros.put("instituição.endereço", instEndereco);
            parametros.put("instituicao.endereco", instEndereco);

        }

        parametros.put("LOGO_INSTITUICAO", logo);
        parametros.put("DETALHES_EMPRESA", detalhesEmpresa);
        parametros.put("CABECALHO", detalhesEmpresa);

        return parametros;
    }

    //TODO: Generalizar com componente de relatorio
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

    public List<ReportTemplate> busarRelatoriosDoTipo(ReportType tipoRelatorio) {
        List<ReportTemplate> relatorios = null;
        if (tipoRelatorio != null) {
            relatorios = genericoDAO.loadCollectionWhere(ReportTemplate.class, "tipoRelatorio", tipoRelatorio);
        }
        return relatorios;
    }

    public byte[] gerarRelatorioGenerico(ReportGeneratorDynamic relatorioGenericoCursivo)
            throws Exception {
        if (relatorioGenericoCursivo != null) {
//            return this.gerarRelatorioGenerico(relatorioGenericoCursivo, relatorioGenericoCursivo.getTitulo(),
//                    relatorioGenericoCursivo.getSubtitulo(), relatorioGenericoCursivo.getIntroducao(),
//                    relatorioGenericoCursivo.getDado(), relatorioGenericoCursivo.getParametros());
        }
        return null;
    }

    public byte[] gerarRelatorioGenerico(ReportGeneratorDynamic relatorioGenericoCursivo,
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
//            ret = relatorioGenericoCursivo.gerarRelatorioPDFByte(titulo, subtitulo, introducao, dado, pars);
        }
        return ret;
    }

    private void processParameters(GenericDataReport report) {
        if (report == null) {
            return;
        }
        Map<String, Object> parametrosPadrao = this.gerarParametrosPadrao();
        report.mergeParameters(parametrosPadrao);

        if (report.entity != null) {
            if (report.entity instanceof IOrganiztionEntityDependent) {
                parametrosPadrao = this
                        .gerarParametrosInstituicaoRelatorio(((IOrganiztionEntityDependent) report.entity)
                                .getOrganization());
                report.mergeParameters(parametrosPadrao);
            }

            List<ReportProperty> propriedadesTipoRelatorio
                    = this.propriedadesTipoRelatorio(report.entity.getClass());
            Map<String, String> parametrosGerados = this.gerarParametros(report.entity, propriedadesTipoRelatorio);
            report.mergeParameters(parametrosGerados);
        }
    }

    public InputStream genericEntityReportStream(GenericDataReport report) {
        processParameters(report);
        InputStream stream = genericReportGenerator.generate(report);
        return stream;
    }

    public List<ReportProperty> propriedadesTipoRelatorio(Class tipoRelatorio) {
        List<ReportProperty> propriedadeRelatorios = new ArrayList<ReportProperty>();
        try {
            //report_properties propriedadesRelatorioPadrao = new report_properties("propriedadesRelatorioPadrao.properties");
            List<ReportProperty> propsTmp = report_properties.getPropriedadeRelatorio(tipoRelatorio);//propriedadesRelatorioPadrao.getPropriedadeRelatorios(tipoRelatorio);
            if (propsTmp != null) {
                propriedadeRelatorios.addAll(propsTmp);
            }
            propsTmp = genericoDAO.loadCollectionWhere(ReportProperty.class, "tipoRelatorio", tipoRelatorio.getName());
            if (propsTmp != null) {
                propriedadeRelatorios.addAll(propsTmp);
            }
        } catch (Exception e) {
            log.error("Falha ao buscar propriedades do relatorio", e);
        }
        return propriedadeRelatorios;
    }

    @Transactional(readOnly = true)
    public Map<String, String> gerarParametros(Object bean, List<ReportProperty> propriedades) {
        Map<String, String> mapaDados = new HashMap<String, String>();
        String valorDefault = "__________________________";

        String nomeDaPropriedade = null;
        Object valor = null;

        for (ReportProperty prop : propriedades) {
            nomeDaPropriedade = prop.getPropriedadeRelatorio();
            valor = null;
            try {
                valor = PropertyUtils.getProperty(bean, prop.getPropriedadeBean());
//                valor = BeanUtils.getProperty(bean, prop.getPropriedadeBean());
            } catch (Exception e) {
                if (prop != null && prop.getPropriedadeAlternativaBean() != null) {
                    try {
                        valor = PropertyUtils.getProperty(bean, prop.getPropriedadeAlternativaBean());
                    } catch (Exception ex) {
                        log.debug("Não foi possivel recuperar propriedade: " + nomeDaPropriedade, ex);
                    }
                } else {
                    log.debug("Falha ao identificar propriedade de bean", e);
                }
            }
            if (valor == null) {
                mapaDados.put(nomeDaPropriedade, valorDefault);
            } else {
                String strValor = valor.toString();
                if (valor instanceof Date) {
                    strValor = UtilDate.formatData((Date) valor);
                } else if (valor instanceof IFormatable) {
                    strValor = ((IFormatable) valor).format();
                }
                mapaDados.put(nomeDaPropriedade, strValor);
                for (String s : prop.apelidosPropridade) {
                    mapaDados.put(s, strValor);
                }
            }
        }
        mapaDados.put("data.hoje", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        return mapaDados;
    }

}
