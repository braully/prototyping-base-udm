package com.github.braully.business;

import com.github.braully.app.logutil;
import com.github.braully.domain.AccountTransaction;
import com.github.braully.domain.Bank;
import com.github.braully.domain.BinaryFile;
import com.github.braully.domain.FinancialAccount;
import com.github.braully.persistence.GenericDAO;
import com.github.braully.constant.MimeTypeFile;
import com.github.braully.util.UtilEncode;
import com.github.braully.util.UtilValidation;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import lombok.Getter;
import net.sf.ofx4j.domain.data.MessageSetType;
import net.sf.ofx4j.domain.data.ResponseEnvelope;
import net.sf.ofx4j.domain.data.ResponseMessageSet;
import net.sf.ofx4j.domain.data.banking.BankAccountDetails;
import net.sf.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import net.sf.ofx4j.domain.data.banking.BankingResponseMessageSet;
import net.sf.ofx4j.domain.data.common.BalanceInfo;
import net.sf.ofx4j.domain.data.common.Transaction;
import net.sf.ofx4j.domain.data.common.TransactionList;
import net.sf.ofx4j.io.AggregateUnmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braully
 */
@Service
public class OFXFileProcessor extends BinaryFileProcessor {

    private static final String DEFAULT_ENCODING = "ISO-8859-1";
    private static final String CONTA_NOME_DEFAULT = "Conta gerada automaticamente";
    private static final String BANCO_NOME_DEFAULT = "BANCO DESCONHECIDO";
    public static final DecimalFormat FORMATADOR_BANCO_NUMERO = new DecimalFormat("000");

    @Getter
    public final String nomeLayout = "Extrato bancário";
    @Getter
    public final String codigoLayout = UtilEncode.appendDv("OFX-V1.0");
    //
    @Resource(name = "genericDAO")
    private GenericDAO genericDAO;

    @Autowired
    MaxGeIA maxGeIA;

    @Override
    public boolean isProcessable(BinaryFile arquivo) {
        if (arquivo != null && MimeTypeFile.isOfx(arquivo.getExtensaoArquivo())) {
            return true;
        }
        return false;
    }

    @Override
    public void processFile(BinaryFile arquivo) {
        this.processarArquivo(arquivo);
    }

    public void processArquivo(InputStream input, String fName) throws Exception {
        //this.processArquivo(input, fName, Boolean.FALSE);
    }

    public void processarArquivo(BinaryFile arquivo) {
        if (arquivo == null || arquivo.getFileBinary() == null) { //|| SituacaoProcessamento.EXECUTADO == arquivo.getSituacaoProcessamento()) {
            throw new IllegalArgumentException("Arquivo invalido");
        }

        String fileName = arquivo.getNome().toLowerCase();
        AggregateUnmarshaller<ResponseEnvelope> unmarshaller = new AggregateUnmarshaller<>(ResponseEnvelope.class);
        InputStreamReader inputStreamReader;
        ResponseMessageSet message = null;
        try {
            inputStreamReader = new InputStreamReader(new ByteArrayInputStream(arquivo.getFileBinary()), DEFAULT_ENCODING);
            ResponseEnvelope unmarshal = unmarshaller.unmarshal(inputStreamReader);
            MessageSetType type = MessageSetType.banking;
            message = unmarshal.getMessageSet(type);
        } catch (Exception ex) {
            logutil.debug("Fail on process file", ex);
        }

        if (message != null) {
            List<BankStatementResponseTransaction> bank = ((BankingResponseMessageSet) message).getStatementResponses();
            for (BankStatementResponseTransaction b : bank) {
                BankAccountDetails account = b.getMessage().getAccount();
                String bankId = account.getBankId();
                String accountNumber = account.getAccountNumber();
                String branchId = account.getBranchId();
                BalanceInfo ledgerBalance = b.getMessage().getLedgerBalance();
                double balancoFinal = ledgerBalance.getAmount();
                Date dataArquivo = ledgerBalance.getAsOfDate();
                TransactionList transactionList = b.getMessage().getTransactionList();
                Date dataInicio = transactionList.getStart();
                Date dataFim = transactionList.getEnd();
                List<Transaction> list = transactionList.getTransactions();

                logutil.debug("banco: " + bankId);
                logutil.debug("cc: " + accountNumber);
                logutil.debug("ag: " + branchId);
                logutil.debug("balanco final: " + balancoFinal);
                logutil.debug("data arquivo: " + dataArquivo);

                FinancialAccount conta = this.buscarContaBancaria(bankId, branchId, accountNumber);
                if (conta == null) {
                    conta = this.criarContaBancaria(bankId, branchId, accountNumber);
                }

                int numlancamento = 0;
                if (list != null) {
                    numlancamento = list.size();
                }

                for (Transaction transaction : list) {
                    try {
                        importTransaction(transaction, conta, arquivo);
                    } catch (Exception e) {
                        logutil.error("fail on import transaction (continue, ignoring) : " + transaction, e);
                    }
                }
            }
        }

        try {
            if (arquivo.getType() == null) {
                arquivo.setType(this.getNomeLayout());
            }
            this.genericDAO.saveEntity(arquivo);
        } catch (Exception e) {
            logutil.debug("Fail on stamp type on file", e);
        }
    }

    @Transactional
    public void importTransaction(Transaction transaction, FinancialAccount conta, BinaryFile arquivo) {
        String transTipo = "";
        try {
            transTipo = transaction.getTransactionType().name();
        } catch (Exception e) {

        }
        String transId = conta.getCodOfx() + ":" + transaction.getId();
        String checkNumber = transaction.getCheckNumber();
        Date datePosted = transaction.getDatePosted();
        if (datePosted != null) {
            transId = transId + ":" + datePosted.getTime();
        }
        if (UtilValidation.isStringValid(checkNumber)) {
            transId = transId + "-" + checkNumber;
        }
        Double amount = transaction.getAmount();
        String memo = transaction.getMemo();
        if (transId != null) {
            transId = transId.trim();
        }
        if (memo != null) {
            memo = memo.trim();
        }
        BigDecimal valor = new BigDecimal(amount);
        AccountTransaction lancamento = buscarLancamento(conta, transId, datePosted, memo, valor);
        if (lancamento == null) {
            lancamento = new AccountTransaction();
            lancamento.setContaBancaria(conta);
            lancamento.setTypeTransaction(transTipo);
            lancamento.setData(datePosted);
            lancamento.setDescricao(memo);
            lancamento.setObservation("Gerador por " + arquivo.getName() + " arquivo numero " + arquivo.getId());
            lancamento.setValor(valor);
            if (valor != null) {
                if (valor.intValue() < 0) {
                    lancamento.setDebitTotal(valor);
                } else {
                    lancamento.setCreditTotal(valor);
                }
            }
            lancamento.setCodigo(transId);
            try {
                lancamento.setAccount(maxGeIA.classifie(lancamento));
            } catch (Exception e) {
                logutil.warn("fail on classifie account transaction", e);
            }

        }

        logutil.debug("tipo:" + transTipo);
        logutil.debug("id:" + transId);
        logutil.debug("data:" + datePosted);
        logutil.debug("valor:" + amount);
        logutil.debug("descricao:" + memo);
        this.genericDAO.saveEntity(lancamento);

    }

    @Transactional
    private AccountTransaction buscarLancamento(FinancialAccount conta, String transId, Date datePosted, String memo, BigDecimal valor) {
        AccountTransaction lanc = null;
        try {
            Query query = this.genericDAO.createQuery("SELECT l FROM AccountTransaction l "
                    + "WHERE l.financialAccount = ?1 AND l.dateExecuted = ?2"
                    + " AND l.uniqueCode = ?3 AND l.valueExecuted = ?4");
            query.setParameter(1, conta);
            query.setParameter(2, datePosted, TemporalType.TIMESTAMP);
            query.setParameter(3, transId);
            query.setParameter(4, valor);
            lanc = (AccountTransaction) query.getSingleResult();
            logutil.warn("LANÇAMENTO JÁ ENCONTRADO NO SISTEMA");
        } catch (NoResultException e) {
            logutil.warn("LANÇAMENTO NOVO");
        }
        return lanc;
    }

    @Transactional//@Transactional("pacFinanTxM")
    private FinancialAccount buscarContaBancaria(String bankId,
            String branchId,
            String accountNumber) {
        FinancialAccount conta = null;
        Bank banco = null;
        try {
            String numConta = accountNumber;

            if (numConta != null) {
                numConta = numConta.trim().replaceAll("\\D", "");
            }

            try {
                if (bankId != null) {
                    bankId = bankId.trim();
                }
                bankId = FORMATADOR_BANCO_NUMERO.format(Integer.parseInt(bankId));
                banco = bankByCode(bankId);
            } catch (Exception e) {

            }

            numConta = "" + Long.parseLong(numConta);
            String contaSemDv = numConta;
            if (numConta.length() > 2) {
                contaSemDv = numConta.substring(0, numConta.length() - 1);
            }
            Query query = this.genericDAO.createQuery("SELECT DISTINCT c FROM FinancialAccount c "
                    + "WHERE (c.number like ?1 OR c.codOfx = ?2) AND c.bank = ?3");
            query.setParameter(1, "%" + contaSemDv + "%");
            query.setParameter(2, accountNumber);
            query.setParameter(3, banco);
            query.setMaxResults(1);
            conta = (FinancialAccount) query.getSingleResult();
        } catch (NoResultException e) {
            logutil.debug("não exite conta cadastrada");
        } catch (Exception e) {
            logutil.warn("Falha ao buscar conta", e);
        }
        return conta;
    }

    @Transactional//@Transactional("pacFinanTxM")
    private FinancialAccount criarContaBancaria(String bankId,
            String branchId,
            String accountNumber) {
        FinancialAccount conta = new FinancialAccount();
        if (bankId != null) {
            bankId = bankId.trim();
        }
        String numConta = accountNumber;
        try {
            bankId = FORMATADOR_BANCO_NUMERO.format(Integer.parseInt(bankId));
        } catch (Exception e) {
            bankId = "" + Integer.parseInt(bankId);
        }
        if (numConta != null) {
            numConta = numConta.trim().replaceAll("\\D", "");
        }
        numConta = "" + Long.parseLong(numConta);
        Bank banco = bankByCode(bankId);
        conta.setConta(numConta);
        conta.setBanco(banco);
        conta.setCodigoOfx(accountNumber);
        conta.setNome(CONTA_NOME_DEFAULT);
        this.genericDAO.saveEntity(conta);
        return conta;
    }

    Bank bankByCode(String bankId) {
        Bank ret = null;
        try {
            ret = (Bank) this.genericDAO.queryObject("SELECT b FROM Bank b WHERE b.number = ?1", bankId);
        } catch (Exception e) {
            logutil.error("fail on load", e);
        }
        if (ret == null) {
            ret = new Bank();
            ret.setNumber(bankId);
            ret.setName(BANCO_NOME_DEFAULT);
        }
        return ret;
    }

    @Override
    public String getType() {
        return this.nomeLayout;
    }
}
