package com.github.braully.app;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author braully
 */
public class url {

    public static Map<String, String> redirect = new TreeMap<>();

    static {
        redirect.put("/", "/index");
        redirect.put("/home", "/index");
        redirect.put("/index", "/autogen/entityDummy.xhtml");
    }

    public static Map<String, String> rewrite_forward = new TreeMap<>();

    static {
        rewrite_forward.put("/entrar", "/jsp/login.jsp");        
        rewrite_forward.put("/inicio", "/jsf/index.xhtml");
        
        /*
        (1, 10, 'Colaborador', 'colaborador', '/administrativo/colaborador', 'ico-colab'),
        (1, 11, 'Folha', 'folha', '/administrativo/folha', 'ico-folha'),
        (1, 12, 'Tarefa', 'tarefa', '/administrativo/tarefa', 'ico-tarefa')
         */

        rewrite_forward.put("/administrativo/colaborador", "/jsf/app/professores.xhtml");
        rewrite_forward.put("/administrativo/folha", "/jsf/app/folha-pagamento.xhtml");
        rewrite_forward.put("/administrativo/tarefa", "/autogen/task.xhtml");
        /*
        (2, 20, 'Caixa', 'caixa', '/financiero/caixa', 'ico-caixa'),
        (2, 21, 'Recebimento', 'recebimento', '/financiero/recebimento', 'ico-recebimento'),
        (2, 22, 'Pagamento', 'pagamento', '/financiero/pagamento', 'ico-pagamento'),
        (2, 23, 'Estoque', 'estoque', '/financiero/estoque', 'ico-estoque'),
        (2, 24, 'Venda', 'venda', '/financiero/venda', 'ico-venda'),
         */

        rewrite_forward.put("/financiero/caixa", "/jsf/app/fluxo-caixa.xhtml");
        rewrite_forward.put("/financiero/recebimento", "/jsf/app/contas-receber.xhtml");
        rewrite_forward.put("/financiero/pagamento", "/jsf/app/contas-pagar.xhtml");
        rewrite_forward.put("/financiero/estoque", "/jsf/app/estoque.xhtml");
        rewrite_forward.put("/financiero/venda", "/jsf/app/venda.xhtml");
        /* 
        (3, 30, 'Aluno', 'aluno', '/secretaria/aluno', 'ico-aluno'),
        (3, 31, 'Turma', 'turma', '/secretaria/turma', 'ico-turma'),
        (3, 32, 'Nota/Falta', 'nota-falta', '/secretaria/nota-falta', 'ico-nota-falta'),
         */

        rewrite_forward.put("/secretaria/aluno", "/jsf/app/alunos.xhtml");
        rewrite_forward.put("/secretaria/turma", "/jsf/app/turmas.xhtml");
        rewrite_forward.put("/secretaria/nota-falta", "/jsf/app/notas-faltas.xhtml");
        /* 
        (4, 40, 'Aula', 'aula', '/pedagogico/aula', 'ico-aula'),
        (4, 41, 'Plano', 'plano', '/pedagogico/plano', 'ico-plano'),
        (4, 42, 'Chamada', 'chamada', '/pedagogico/chamada', 'ico-chamada'),
        (4, 43, 'Diário', 'diario', '/pedagogico/diario', 'ico-diario'),
        (4, 44, 'Calendário', 'calendario', '/pedagogico/calendario', 'ico-calendario'),
        (4, 45, 'Horário', 'horario', '/pedagogico/horario', 'ico-horario'),
         */

        rewrite_forward.put("/pedagogico/aula", "/jsf/app/coordenacao-aula.xhtml");
        rewrite_forward.put("/pedagogico/plano", "/jsf/app/coordenacao-plano-aula.xhtml");
        rewrite_forward.put("/pedagogico/chamada", "/jsf/app/coordenacao-chamada-turma.xhtml");
        rewrite_forward.put("/pedagogico/diario", "/jsf/app/coordenacao-diario.xhtml");
        rewrite_forward.put("/pedagogico/calendario", "/jsf/app/coordenacao-calendario-academico.xhtml");
        rewrite_forward.put("/pedagogico/horario", "/jsf/app/coordenacao-horario.xhtml");
        /*         
        (5, 50, 'Relatório', 'relatorio', '/sistema/relatorio', 'ico-relatorio'),
        (5, 51, 'Configuração', 'configuracao', '/sistema/configuracao', 'ico-configuracao'),
        (5, 52, 'Importação', 'importacao', '/sistema/importacao', 'ico-importacao');
         */
        rewrite_forward.put("/sistema/relatorio", "/jsf/app/relatorios.xhtml");
        rewrite_forward.put("/sistema/configuracao", "/jsf/app/configuracao-geral.xhtml");
        rewrite_forward.put("/sistema/importacao", "/mb/importacao-arquivo");
    }

}
