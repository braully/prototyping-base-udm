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
import com.github.braully.constant.StatusBug;
import com.github.braully.domain.App;
import com.github.braully.domain.Task;
import com.github.braully.domain.Bug;
import com.github.braully.domain.LogEntryError;
import com.github.braully.domain.LogEntryErrorView;
import java.util.*;
import javax.annotation.Resource;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braullyrocha
 */
@Service
public class BugBC {

    @Resource(name = "genericDAO")
    private GenericDAO genericoDAO;

    //@Override
    public List<App> getProjetos() {
        return genericoDAO.loadCollection(App.class);
    }

    //@Override
    public List<Bug> pesquisarErros(App projeto, StatusBug statusBug, Date dataInicio, Date dataFim) {
        List<Bug> bugs = new ArrayList<Bug>();
        List<LogEntryErrorView> visoes = this.pesquisarVisoes(projeto, dataInicio, dataFim);
        if (visoes != null) {
            for (LogEntryErrorView vis : visoes) {
                Bug bug = pesquisarBug(vis.getId());
                if (bug == null) {
                    bug = new Bug(vis);
                }
                bugs.add(bug);
            }
        }
        Collections.sort(bugs, new Comparator<Bug>() {

            //@Override
            public int compare(Bug t, Bug t1) {
                int ret = 0;
                try {
                    ret = t1.getVisaoLog().getLastOccurrence().compareTo(t.getVisaoLog().getLastOccurrence());
                } catch (Exception e) {
                }
                return ret;
            }
        });
        return bugs;
    }

    //@Override
    public List<Bug> pesquisarBugs(App projeto, StatusBug statusBug, Date dataInicio, Date dataFim) {
        List<Bug> bugs = new ArrayList<Bug>();
        Criteria criteria = this.genericoDAO.getSession().createCriteria(Bug.class);
        criteria.createAlias("visaoLog", "vl");

        if (projeto != null && projeto.isPersisted()) {
            criteria.add(Restrictions.eq("vl.aplicacao", projeto.getName()));
        }

        if (dataInicio != null && dataFim != null) {
            criteria.add(Restrictions.or(Restrictions.between("dataCriacao", dataInicio, dataFim), Restrictions.between("vl.ultimaOcorrencia", dataInicio, dataFim)));
        }

        if (statusBug != null) {
            criteria.add(Restrictions.eq("statusBug", statusBug));
        }

        List<Bug> bugsTmp = criteria.list();
        if (bugsTmp != null) {
            bugs.addAll(bugsTmp);
        }
        Collections.sort(bugs, new Comparator<Bug>() {

            //@Override
            public int compare(Bug t, Bug t1) {
                int ret = 0;
                try {
                    ret = t1.getVisaoLog().getLastOccurrence().compareTo(t.getVisaoLog().getLastOccurrence());
                } catch (Exception e) {
                }
                return ret;
            }
        });
        return bugs;
    }

    private List<LogEntryErrorView> pesquisarVisoes(App projeto, Date dataInicio, Date dataFim) {
        Criteria criteria = genericoDAO.getSession().createCriteria(LogEntryErrorView.class);
        if (projeto != null && projeto.isPersisted()) {
            criteria.add(Restrictions.ilike("aplicacao", projeto.getName()));
        }
        if (dataInicio != null && dataFim != null) {
            criteria.add(Restrictions.between("ultimaOcorrencia", dataInicio, dataFim));
        }
        return criteria.list();
    }

    private Bug pesquisarBug(int numLog) {
        Bug bug = null;
        try {
            bug = (Bug) this.genericoDAO.queryObject("select b from Bug b where b.visaoLog.id=?", numLog);
        } catch (javax.persistence.NoResultException e) {
        }
        return bug;
    }

    //@Override
    public void reportarBugRedmine(Bug bug, App projeto, String redmineHost, String nomeUsuario, String senha) {
//        try {
//            RedmineManager mgr;//= new RedmineManager(redmineHost, apiAccessKey);
//            mgr = new RedmineManager(redmineHost, nomeUsuario, senha);
//            Issue issue = new Issue();
//            issue.setSubject(bug.getAssunto());
//            issue.setDescription(bug.getDescricao());
//            issue.setCreatedOn(new Date());
//            issue.setNotes(bug.getNotas());
//            issue = mgr.createIssue(projeto.getCaminhoProjeto(), issue);
//            bug.setNumRedMine("" + issue.getId());
//            bug.setStatusBug(StatusBug.REPORTADO);
//            this.saveEntity(bug);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            throw new RuntimeException("Erro ao repotar no RedMine", ex);
//        }
    }

    @Transactional
    //@Override
    public void reportarBug(Bug bug) {
        if (!bug.isPersisted()) {
            //TODO: Associar o bug ao projeto correto e a tarefa as tags correspondentes
            Task tarefa = new Task();
            this.genericoDAO.saveEntity(tarefa);
            bug.setTarefa(tarefa);
        }
        bug.setStatusBug(StatusBug.REPORTADO);
        this.genericoDAO.saveEntity(bug);
    }

    //@Override
    public Bug carregarBug(Bug bugTmp) {
        Bug bug = this.genericoDAO.loadEntityFetch(bugTmp, "projeto", "visaoLog", "pai", "tarefa");
        List<LogEntryError> mensagens = this.genericoDAO.queryList("SELECT e FROM EntradaLog e "
                + "WHERE e.localizacao = ?1 ORDER BY e.dataOcorrencia DESC", bugTmp.getVisaoLog().getLocation());
        bug.setMensagens(mensagens);
        return bug;
    }

    @Transactional
    //@Override
    public List<Task> carregarCadeiaTarefas(Bug bugTmp) {
        List<Task> ts = new ArrayList<>();
        if (bugTmp != null) {
            Bug bug = (Bug) this.genericoDAO.queryObject("SELECT b FROM Bug b JOIN FETCH b.tarefa t WHERE b.id = ?1", bugTmp.getId());
            if (bug != null) {
                Task t = bug.getTarefa();
                if (t != null) {
                    ts.add(t);
                    ts.addAll(t.getChildrens());
                }
            }
        }
        return ts;
    }
}
