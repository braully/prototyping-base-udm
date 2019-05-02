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

import com.github.braully.constant.StatusBug;
import com.github.braully.domain.App;
import com.github.braully.domain.Task;
import com.github.braully.domain.util.Bug;
import java.util.Date;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braully
 */
public interface IBugBC {

    Bug carregarBug(Bug bugTmp);

    @Transactional
    List<Task> carregarCadeiaTarefas(Bug bugTmp);

    List<App> getProjetos();

    List<Bug> pesquisarBugs(App projeto, StatusBug statusBug, Date dataInicio, Date dataFim);

    List<Bug> pesquisarErros(App projeto, StatusBug statusBug, Date dataInicio, Date dataFim);

    @Transactional
    void reportarBug(Bug bug);

    void reportarBugRedmine(Bug bug, App projeto, String redmineHost, String nomeUsuario, String senha);

}
