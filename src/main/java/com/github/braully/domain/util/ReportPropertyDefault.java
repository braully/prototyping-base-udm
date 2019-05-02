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
import com.github.braully.util.UtilProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author zero
 */
public class ReportPropertyDefault {

    private final String SEPARADOR_PROPRIEDADES = ",";
    private final String SEPARADOR_VALORES = "->";
    private List<ReportProperty> propriedadeRelatorios;

    public ReportPropertyDefault(String nomeArquivoPropriedades) throws IOException {
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

    public List<ReportProperty> getPropriedadeRelatorios() {
        return propriedadeRelatorios;
    }

    public List<ReportProperty> getPropriedadeRelatorios(ReportType tipoRelatorio) {
        List<ReportProperty> tmp = new ArrayList<ReportProperty>();
        if (propriedadeRelatorios != null) {
            for (ReportProperty prop : propriedadeRelatorios) {
                if (prop.getTipoRelatorio() == tipoRelatorio) {
                    tmp.add(prop);
                }
            }
        }
        return tmp;
    }
}
