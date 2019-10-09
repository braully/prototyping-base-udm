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
package com.github.braully.domain.util;

import com.github.braully.constant.ReportType;
//import com.github.braully.tmp.Matricula;
import com.github.braully.util.UtilProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author zero
 */
public class report_properties {

    private final String SEPARADOR_PROPRIEDADES = ",";
    private final String SEPARADOR_VALORES = "->";
    private List<ReportProperty> propriedadeRelatorios;

    static Map<String, List<ReportProperty>> PROPERTIES_DEFAULT = new HashMap<String, List<ReportProperty>>();

    static {
        List<ReportProperty> propsMatricula = new ArrayList<>();

//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Número de matricula aluno")
//                .propridade("aluno.matricula")
//                .apelidoPropridade("aluno.matrícula")
//                .propridadeBean("id")
//        //.propridadeAlternativaBean("id")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Profissão responsavel")
//                .propridade("aluno.responsavel.profissao")
//                .apelidoPropridade("aluno.responsável.profissão")
//                .propridadeBean("purchaseOrder.partner.profissao")
//        //.propridadeAlternativaBean("id")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Profissão responsavel")
//                .propridade("aluno.responsavel.nascimento")
//                .propridadeBean("purchaseOrder.partner.nascimentoFormatado")
//        //.propridadeAlternativaBean("id")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Data matricula")
//                .propridade("data.matricula")
//                .apelidoPropridade("data.matrícula")
//                .propridadeBean("dataFormatada")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Ano letivo da turma")
//                .propridade("turma.ano")
//                .propridadeBean("turma.anoLetivo")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Descrição da turma")
//                .propridade("turma.descricao")
//                .apelidoPropridade("turma.descrição")
//                .propridadeBean("turma.descricaoFormatada")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Telefones do aluno e responsaveis")
//                .propridade("aluno.telefones")
//                .propridadeBean("aluno.contact.telefonesFormatado")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class).nome("Valor mensalidade")
//                .propridadeBean("prestacaoServicoAcademico.totalDescontos")
//                .propridade("valor.mensalidade"));
//
//        propsMatricula.add(new ReportProperty(Matricula.class).nome("Valor taxa matrícula")
//                .propridadeBean("prestacaoServicoAcademico.totalDescontos")
//                .propridade("valor.matricula")
//                .apelidoPropridade("valor.matrícula")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class).nome("Valor bolsa de estudo")
//                .propridadeBean("prestacaoServicoAcademico.totalDescontos")
//                .propridade("valor.desconto"));
//
//        propsMatricula.add(new ReportProperty(Matricula.class).nome("Porcentagem bolsa de estudo")
//                .propridadeBean("prestacaoServicoAcademico.totalDescontos")
//                .propridade("valor.desconto.porcentagem"));
//
//        propsMatricula.add(new ReportProperty(Matricula.class).nome("Valor total de descontos")
//                .propridadeBean("prestacaoServicoAcademico.totalDescontos")
//                .propridade("valor.desconto.total"));
//
//        propsMatricula.add(new ReportProperty(Matricula.class).nome("Valor total sem desconto")
//                .propridadeBean("prestacaoServicoAcademico.totalSemDesconto")
//                .propridade("valor.total.semdesconto"));
//
//        propsMatricula.add(new ReportProperty(Matricula.class).nome("Valor total")
//                .propridadeBean("purchaseOrder.total")
//                .propridade("valor.total.comdesconto"));
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Telefones reponsável financeiro do aluno")
//                .propridadeBean("purchaseOrder.partner.telefonesFormatado")
//                .propridade("aluno.responsavel.telefones")
//                .apelidoPropridade("aluno.responsável.telefones")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Nome do aluno")
//                .propridadeBean("aluno.nome")
//                .propridade("aluno.nome"));
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Nome mãe do aluno")
//                .propridadeBean("aluno.mae.partnerSource.nome")
//                .propridade("aluno.mae.nome")
//                .apelidoPropridade("aluno.mãe.nome")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Nome pai do aluno")
//                .propridade("aluno.pai.nome")
//                .propridadeBean("aluno.pai.partnerSource.nome")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Nome responsável financeiro")
//                .propridade("aluno.responsavel.nome")
//                .apelidoPropridade("aluno.responsável.nome")
//                .propridadeBean("purchaseOrder.partner.name")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Estado civil responsável financeiro")
//                .propridade("aluno.responsavel.estadocivil")
//                .apelidoPropridade("aluno.responsável.estadocivil")
//                .propridadeBean("purchaseOrder.partner.estadoCivil")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("RG responsável financeiro")
//                .propridadeBean("purchaseOrder.partner.identidadeFormatada")
//                .propridade("aluno.responsavel.rg")
//                .propridade("aluno.responsável.rg")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Número RG responsável financeiro")
//                .propridadeBean("purchaseOrder.partner.numeroIdentidade")
//                .propridade("aluno.responsavel.rg.numero")
//                .apelidoPropridade("aluno.responsável.rg.número")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("CPF do responsável financeiro")
//                .propridade("aluno.responsavel.cpf")
//                .apelidoPropridade("aluno.responsável.cpf")
//                .propridadeBean("purchaseOrder.partner.fiscalCode")
//        );
//
//        //TODO: Replace bean property for expression dataNascimento by formatData(aluno.dataNascimento)
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Cidade nascimento aluno")
//                .propridadeBean("aluno.dataNascimento")
//                .propridade("aluno.nascimento.cidade")
//        );
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Data nascimento aluno")
//                .propridadeBean("aluno.dataNascimento")
//                .propridade("aluno.nascimento.data"));
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Data nascimento aluno")
//                .propridadeBean("aluno.nascimentoFormatado")
//                .propridade("aluno.nascimento"));
//
//        propsMatricula.add(new ReportProperty(Matricula.class)
//                .nome("Endereço responsavel financeiro")
//                .propridadeBean("purchaseOrder.partner.address")
//                .propridadeAlternativaBean("aluno.address.descricaoFormatada")
//                .propridade("aluno.responsavel.endereco")
//                .apelidoPropridade("aluno.responsável.endereço")
//        );

//        PROPERTIES_DEFAULT.put(Matricula.class.getName(), propsMatricula);

    }

    public report_properties(String nomeArquivoPropriedades) throws IOException {
        Properties props = UtilProperty.getProperties(nomeArquivoPropriedades);
        Set<Object> keySet = props.keySet();
        if (keySet != null && !keySet.isEmpty()) {
            propriedadeRelatorios = new ArrayList<ReportProperty>();
            for (ReportType tipo : ReportType.values()) {
                Object val = props.get(tipo.name());
                if (val != null) {
                    String[] strProps = val.toString().split(SEPARADOR_PROPRIEDADES);
                    if (strProps != null && strProps.length > 0) {
                        for (String strProp : strProps) {
                            String[] split = strProp.split(SEPARADOR_VALORES);
                            String propriedadeRelatorio = null;
                            if (split.length > 0 && split[0] != null) {
                                propriedadeRelatorio = split[0].trim();
                            }
                            String propriedadeBean = null;
                            if (split.length > 1 && split[1] != null) {
                                propriedadeBean = split[1].trim();
                            }
                            String descricao = null;
                            if (split.length > 2 && split[2] != null) {
                                descricao = split[2].trim();
                            }
                            this.propriedadeRelatorios.add(new ReportProperty(tipo, propriedadeRelatorio, propriedadeBean, descricao));
                        }
                    }
                }
            }
        }
    }

    public static List<ReportProperty> getPropriedadeRelatorio(Class tiporelatio) {
        try {
            List<ReportProperty> get = PROPERTIES_DEFAULT.get(tiporelatio.getName());
            if (get != null) {
                return Collections.unmodifiableList(get);
            }
        } catch (Exception e) {
        }
        return null;

    }

    public List<ReportProperty> getPropriedadeRelatorios() {
        return propriedadeRelatorios;
    }

    public List<ReportProperty> getPropriedadeRelatorios(ReportType tipoRelatorio) {
        List<ReportProperty> tmp = new ArrayList<ReportProperty>();
        if (propriedadeRelatorios != null) {
            for (ReportProperty prop : propriedadeRelatorios) {
//                if (prop.getTipoRelatorio() == tipoRelatorio.n) {
//                    tmp.add(prop);
//                }
            }
        }
        return tmp;
    }
}
