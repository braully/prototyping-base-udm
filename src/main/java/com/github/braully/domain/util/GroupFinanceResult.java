package com.github.braully.domain.util;

import com.github.braully.domain.util.LineGroup;
import com.github.braully.constant.StatusExecutionCycle;
import com.github.braully.domain.Account;
import com.github.braully.domain.util.MoneyTO;
import com.github.braully.util.UtilDate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author braully
 */
public class GroupFinanceResult {

    private static final Logger log = LogManager.getLogger(GroupFinanceResult.class);
    private static final DateFormat formatadorData = new SimpleDateFormat("yyMMdd:MMM/yy");
    private static final DateFormat formatadorDataEvolucao = new SimpleDateFormat("yyMM:MMM/yy");

    /*
    
     */
    List entidades;
    MoneyTO valorTotal = new MoneyTO();
    long quantidadeTotal;
    Map<StatusExecutionCycle, MoneyTO> valorSituacao = new EnumMap<>(StatusExecutionCycle.class);
    Map<StatusExecutionCycle, Long> quantidadeSituacao = new EnumMap<>(StatusExecutionCycle.class);
    Map<Account, MoneyTO> valorConta = new HashMap<>();
    Map<Account, Long> quantidadeConta = new HashMap<>();
    List<StatusExecutionCycle> situacoes = null;
    private final Map<String, MoneyTO> evolucaoTemporal = new HashMap<>();

    private Date dataInicio;
    private Date dataFim;

    public long getQuantidadeTotal() {
//        new net.sf.jasperreports.engine.data.JRMapCollectionDataSource(valorSituacao);
        return quantidadeTotal;
    }

    public MoneyTO getValorTotal() {
        return valorTotal;
    }

    public List getEntidades() {
        return entidades;
    }

    public Map<Account, Long> getQuantidadeConta() {
        return quantidadeConta;
    }

    public Map<StatusExecutionCycle, Long> getQuantidadeSituacao() {
        return quantidadeSituacao;
    }

    public Map<Account, MoneyTO> getValorConta() {
        return valorConta;
    }

    public Map<StatusExecutionCycle, MoneyTO> getValorSituacao() {
        return valorSituacao;
    }

    void contabilizar(Account conta, StatusExecutionCycle situacaoAvaliada, MoneyTO valor) {
        if (valor == null) {
            return;
        }
        quantidadeTotal++;
        valorTotal = valorTotal.adicionaValorAbsoluto(valor);
        if (conta != null) {
            MoneyTO valConta = valorConta.get(conta);
            if (valConta == null) {
                valConta = new MoneyTO();
            }
            valConta = valConta.adicionaValorAbsoluto(valor);
            valorConta.put(conta, valConta);
            Long qntConta = quantidadeConta.get(conta);
            if (qntConta == null) {
                qntConta = 0l;
            }
            qntConta++;
            quantidadeConta.put(conta, qntConta);
        }
        if (situacaoAvaliada != null) {
            MoneyTO valSit = valorSituacao.get(situacaoAvaliada);
            if (valSit == null) {
                valSit = new MoneyTO();
            }
            valSit = valSit.adicionaValorAbsoluto(valor);
            valorSituacao.put(situacaoAvaliada, valSit);
            Long quantSit = quantidadeSituacao.get(situacaoAvaliada);
            if (quantSit == null) {
                quantSit = 0l;
            }
            quantSit++;
            quantidadeSituacao.put(situacaoAvaliada, quantSit);
        }
    }

    public List<StatusExecutionCycle> getSituacoes() {
        if (situacoes == null) {
            situacoes = new ArrayList<StatusExecutionCycle>();
            Set<StatusExecutionCycle> keySet = quantidadeSituacao.keySet();
            if (keySet != null) {
                situacoes.addAll(keySet);
                Collections.sort(situacoes);
            }
        }
        return situacoes;
    }

    public StatusExecutionCycle getPrevisto() {
        return StatusExecutionCycle.READY;
    }

    public StatusExecutionCycle getExecutado() {
        return StatusExecutionCycle.DONE;
    }

    public StatusExecutionCycle getVencido() {
        return StatusExecutionCycle.RUNNING;
    }

    public List<LineGroup> getLinhasSituacao() {
        List<LineGroup> linhas = new ArrayList<LineGroup>();
        try {
            for (StatusExecutionCycle sit : StatusExecutionCycle.values()) {
                Long quantidade = quantidadeSituacao.get(sit);
                MoneyTO get = valorSituacao.get(sit);
                if (quantidade == null) {
                    quantidade = 0l;
                }
                if (get == null) {
                    get = new MoneyTO();
                }
                linhas.add(new LineGroup(sit, get, quantidade, valorTotal, quantidadeTotal));
            }
            Collections.sort(linhas);
        } catch (Exception e) {
            log.error("falha ao agregar resultados", e);
        }
        return linhas;
    }

    public List<LineGroup> getLinhasConta() {
        List<LineGroup> linhas = new ArrayList<>();
        try {
            Set<Account> keySet = valorConta.keySet();
            if (keySet != null) {
                for (Account sit : keySet) {
                    Long quantidade = quantidadeConta.get(sit);
                    MoneyTO get = valorConta.get(sit);
                    linhas.add(new LineGroup(sit, get, quantidade, valorTotal, quantidadeTotal));
                }
            }
            Collections.sort(linhas);
        } catch (Exception e) {
            log.error("falha ao agregar resultados", e);
        }
        return linhas;
    }

    public void setDataInicio(Date dataInicioPagamento) {
        this.dataInicio = dataInicioPagamento;
    }

    public void setDataFim(Date dataFimPagamentoBusca) {
        this.dataFim = dataFimPagamentoBusca;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    void informarRegistroFinanceiro(Account ct, Date dataVencimento, MoneyTO valor, StatusExecutionCycle situacao) {
        if (situacao == StatusExecutionCycle.READY
                && UtilDate.isAntesHoje(dataVencimento)) {
            situacao = StatusExecutionCycle.RUNNING;
        }
        contabilizar(ct, situacao, valor);
        contabilizarEvolucao(dataVencimento, situacao, valor);
    }

    public Map<String, MoneyTO> getEvolucaoTemporal() {
        return evolucaoTemporal;
    }

    void contabilizarEvolucao(Date data, StatusExecutionCycle situacao, MoneyTO valor) {
        if (valor != null && !valor.isZero() && data != null && situacao != null && situacao != StatusExecutionCycle.CANCELED) {
            try {
                String format = formatadorDataEvolucao.format(data);
                MoneyTO get = evolucaoTemporal.get(format);
                if (get == null) {
                    get = new MoneyTO();
                }
                get = get.adicionaValorAbsoluto(valor);
                evolucaoTemporal.put(format, get);
            } catch (Exception e) {
                log.warn("dificuldades ao contabilizar resultados financeiros", e);
            }
        }
    }

    public MoneyTO getValorTotalSituacao(StatusExecutionCycle sit) {
        MoneyTO ret = null;
        if (sit != null && valorSituacao != null) {
            return valorSituacao.get(sit);
        }
        return ret;
    }
}
