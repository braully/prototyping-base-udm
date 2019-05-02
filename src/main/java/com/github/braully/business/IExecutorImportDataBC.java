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

import com.github.braully.domain.BinaryFile;
import java.io.IOException;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braully
 */
public interface IExecutorImportDataBC {

    List<BinaryFile> buscarImportacoesPendentes();

    void importarDadosPendentes();

    //    @Transactional
    void processarImportacao(BinaryFile ai) throws IOException;

    @Transactional
    void salvarEntidadeMigravel(DescriptorLayoutImportFile descritorArquivo, Object[] linha);

}
