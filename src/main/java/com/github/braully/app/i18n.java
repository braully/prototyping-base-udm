package com.github.braully.app;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author braully
 */
public class i18n {

    public static Map<String, String> messages = new HashMap<>();
    public static Map<String, String> messages_pt_br = new HashMap<>();

    public static Map<String, Map<String, String>> localeMessages = new HashMap<>();

    public static final Locale pt_BR = new Locale("pt", "BR");

    public static String str(Locale loc) {
        return loc.toString().toLowerCase();
    }

    /* Messages Default */
    static {

    }

    /* Messages pt_BR */
    static {
        messages_pt_br.put("Name", "Nome");
        messages_pt_br.put("Description", "Descrição");
        messages_pt_br.put("Oficial Name", "Razão social");
        messages_pt_br.put("Fiscal Code", "CPF/CNPJ");
        messages_pt_br.put("Contact", "Contato");
        messages_pt_br.put("Organization", "Instituição");
        messages_pt_br.put("Colaborator", "Colaborador");
        messages_pt_br.put("Partner", "Parceiro");
        messages_pt_br.put("Task", "Tarefa");
        messages_pt_br.put("Payroll", "Folha");
        messages_pt_br.put("Cashier", "Caixa");
        messages_pt_br.put("Receivable", "Recebeminto");
        messages_pt_br.put("Payable", "Pagamento");
        messages_pt_br.put("Sale", "Venda");
        messages_pt_br.put("Birth Date", "Nascimento");

        messages_pt_br.put("Code Client", "Código Cliente");
        messages_pt_br.put("Code Agreement", "Código Convênio");
        messages_pt_br.put("Agreement", "Convênio");
        messages_pt_br.put("Date Process", "Data processamento");
        messages_pt_br.put("Modal Variant", "Modalidade ou Variação");

        messages_pt_br.put("Report", "Relatório");
        messages_pt_br.put("Settings", "Configuração");
        messages_pt_br.put("Import", "Importar");

        messages_pt_br.put("Parent", "Superior");
        messages_pt_br.put("Account", "Conta");
        messages_pt_br.put("Bank", "Banco");
        messages_pt_br.put("Bank Account", "Conta bancária");
        messages_pt_br.put("Wallet", "Carteira");
        messages_pt_br.put("Instruction", "Instrução");
        messages_pt_br.put("Instructions", "Instruções");
        messages_pt_br.put("Charge", "Encargos");
        messages_pt_br.put("Financial Charge", "Encargos financeiros");
        messages_pt_br.put("Memo", "Referência");
        messages_pt_br.put("Observation", "Observação");
        messages_pt_br.put("Parent", "Superior");

        messages_pt_br.put("Date Previst", "Data prevista");
        messages_pt_br.put("Date Executed", "Data realizada");
        messages_pt_br.put("Credit Total", "Crédito");
        messages_pt_br.put("Debit Total", "Débito");
        messages_pt_br.put("Type Method", "Metódo");
        messages_pt_br.put("Type Operation", "Operação");
        messages_pt_br.put("Type Transaction", "Tipo transação");
        messages_pt_br.put("Actual Balance", "Saldo atual");

        messages_pt_br.put("Financial Account", "Caixa");
        messages_pt_br.put("Parent Transaction", "Transação original");

        messages_pt_br.put("Type Factor Traffic Ticket", "Tipo do fator multa");
        messages_pt_br.put("Factor Traffic Ticket", "Fator multa");
        messages_pt_br.put("Type Period Interest", "Perído recorrência juros");
        messages_pt_br.put("Type Factor Interest Rating", "Tipo do fator juros");
        messages_pt_br.put("Factor Interest Rating", "Fator juros");
        messages_pt_br.put("Type", "Tipo");
        messages_pt_br.put("Status Execution", "Situação");
        messages_pt_br.put("Situation", "Situação");

        messages_pt_br.put("Date Maturity", "Data vencimento");

        messages_pt_br.put("Generate", "Gerar");

        messages_pt_br.put("Agency", "Agência");
        messages_pt_br.put("Number", "Número");
        messages_pt_br.put("Value Executed", "Valor executado");
        messages_pt_br.put("Stock", "Estoque");

        messages_pt_br.put("Unique Code", "Código");
        messages_pt_br.put("Status", "Situação");
        messages_pt_br.put("Remove", "Excluir");
        messages_pt_br.put("Removed", "Removido");
        messages_pt_br.put("Edit", "Editar");
        messages_pt_br.put("Save", "Salvar");
        messages_pt_br.put("Search", "Buscar");
        messages_pt_br.put("View", "Ver");
    }

    static {
        localeMessages.put("pt", messages_pt_br);
        localeMessages.put(str(pt_BR), messages_pt_br);
    }

    public static String getMessage(String msg) {
        return getMessage(Locale.getDefault(), msg);
    }

    public static String getMessage(Locale locale, String msg) {
        Map<String, String> msgs = localeMessages.get(locale);
        if (msgs == null) {
            msgs = messages;
        }
        String msgtrans = msgs.get(msg);
        if (msgtrans == null) {
            msgtrans = msg;
        }
        return msgtrans;
    }

    public static Map getMessageLanguageMap(String language) {
        Map<String, String> msgs = localeMessages.get(language);
        if (msgs == null) {
            msgs = messages;
        }
        return msgs;
    }
}
