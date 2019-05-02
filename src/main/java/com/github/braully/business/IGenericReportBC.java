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

import com.github.braully.constant.ReportType;
import com.github.braully.domain.Organization;
import com.github.braully.domain.util.ReportTemplate;
import com.github.braully.util.GenericReport;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braully
 */
public interface IGenericReportBC extends Serializable {

    Map<String, Object> gerarParametrosInstituicaoRelatorio(Organization inst);

    @Transactional(readOnly = true)
    Map<String, Object> gerarParametrosPadraoRelatorio();

    //TODO: Generalizar com componente de relatorio
    byte[] gerarRelatorioPdfJasperConexaoPadrao(String caminhoRelatorio,
            Map<String, Object> parametrosPersonalizados);

    public List<ReportTemplate> busarRelatoriosDoTipo(ReportType tipoRelatorio);

    public byte[] gerarRelatorioGenerico(GenericReport relatorioGenerico,
            String titulo, String subtitulo, String introducao, Object dado, Map parametros) throws Exception;

    public byte[] gerarRelatorioGenerico(GenericReport relatorioGenerico) throws Exception;
}
