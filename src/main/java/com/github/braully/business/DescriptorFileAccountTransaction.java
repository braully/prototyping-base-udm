package com.github.braully.business;

import com.github.braully.util.logutil;
import com.github.braully.constant.GenericEntityType;
import com.github.braully.domain.Account;
import com.github.braully.domain.AccountTransaction;
import com.github.braully.domain.FinancialAccount;
import com.github.braully.domain.Organization;
import com.github.braully.domain.Partner;
import com.github.braully.persistence.GenericDAO;
import com.github.braully.util.UtilDate;
import com.github.braully.util.UtilEncode;
import com.github.braully.util.UtilReflection;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braully
 */
@Component
public class DescriptorFileAccountTransaction extends DescriptorLayoutImportFile {

    public static final String DEFAULT_FINANCIAL_ACCOUNT_NAME = "Tesouraria";

    @Getter
    public final String nomeLayout = "Importação de contas a receber";
    @Getter
    public final String codigoLayout = UtilEncode.appendDv("CONTA-RECEBER-V1.0");
    @Getter
    public final Class classe = AccountTransaction.class;

    @Resource(name = "genericDAO")
    private GenericDAO genericDAO;

    @Autowired
    DescriptorFilePartner descriptorFilePartner;

    /*public static enum DescritorCamposBoletoEmitido implements IDescrpitorFieldLayout {
        COD_CR("Código Recebível", "codigoExportacao", "Dados Cliente"),
        COD_PESSOA("Código Pessoa", "pessoa.id", "Dados Cliente"),
        PESSOA_NOME("Nome", "descricaoPessoa", "Dados Cliente"),
        PESSOA_INFO("Referencia", "recebimentoPrevisto.pessoaInformacao", "Dados Cliente"),
        PESSOA_INFO_EXTRA("Referente", "recebimentoPrevisto.pessoaInformacaoExtra", "Dados Cliente"),
        CPF("CPF", "pessoa.numeroComprovantePessoaFormatado", "Dados Cliente"),
        CIDADE("Cidade", "pessoa.endereco.cidade", "Dados Cliente"),
        BAIRRO("Bairro", "pessoa.endereco.bairro", "Dados Cliente"),
        ENDERECO("Endereço", "pessoa.endereco.enderecoComplemento", "Dados Cliente"),
        CEP("CEP", "pessoa.endereco.cep", "Dados Cliente"),
        HISTORICO("Histórico", "descricaoRecebimento", "Dados Recebível"),
        CONTA("Conta", "descricaoConta", "Dados Recebível"),
        SITUACAO("Situação", "situacao", "Dados Recebível"),
        VENCIMENTO("Vencimento", "dataVencimento", "Dados Recebível"),
        VALOR_ORIGINAL("Valor Original", "valorOriginal", "Dados Recebível"),
        DESCONTO("Desconto", "valorAlteracao", "Dados Recebível"),
        DESCONTO_EXTRA("Desconto Extra", "valorAlteracaoExtra", "Dados Recebível"),
        VALOR_FINAL("Valor a Receber", "valor", "Dados Recebível"),
        COD_MOVIMENTO_CAIXA("Código Movimento", "caixaRegistro.id", "Movimento Caixa"),
        METODO_MOVIMENTO("Método", "metodoCaixaRegistro", "Movimento Caixa"),
        DESCRICAO("Descrição", "descricaoBaixa", "Movimento Caixa"),
        DATA_RECEBIMENTO("Data Recebimento", "caixaRegistro.data", "Movimento Caixa"),
        VALOR_RECEBIDO("Valor Recebido", "valorBaixa", "Movimento Caixa"),;

        @Getter
        private final String nomeColuna;
        @Getter
        private final String propriedade;
        @Getter
        private final String grupo;

        DescritorCamposBoletoEmitido(String label) {
            this.nomeColuna = label;
            this.propriedade = label.toLowerCase();
            this.grupo = GRUPO_DEFAULT;
        }

        private DescritorCamposBoletoEmitido(String nomeColuna, String propriedade) {
            this.nomeColuna = nomeColuna;
            this.propriedade = propriedade;
            this.grupo = GRUPO_DEFAULT;
        }

        private DescritorCamposBoletoEmitido(String nomeColuna, String propriedade, String grupo) {
            this.nomeColuna = nomeColuna;
            this.propriedade = propriedade;
            this.grupo = grupo;
        }

        @Override
        public int getIndice() {
            return this.ordinal();
        }
    }*/
    @Override
    public IDescrpitorFieldLayout[] getDescritorCampos() {
        return CamposImportacaoCobranca.values();
    }
    Account contaReceita;

    private Account contaPrincipalReceita() {
        if (contaReceita == null) {
            List<Account> receitas = this.genericDAO.loadCollectionWhere(Account.class, "typeGL", Account.AccountType.C);
            if (receitas != null && !receitas.isEmpty()) {
                contaReceita = receitas.get(0);
            }
        }
        return contaReceita;
    }

    @Transactional
    //TODO: Refactor to financialController
    public FinancialAccount defaultFinancialAccount(Organization organization) {
        FinancialAccount caixaPrinciapl = null;
        try {
            caixaPrinciapl = this.genericDAO.loadEntityWhere(FinancialAccount.class,
                    "organization", organization, "type", GenericEntityType.DEFAULT.name());
        } catch (Exception e) {
            log.info("Not financial account for organization: " + organization);
        }
        if (caixaPrinciapl == null) {
            caixaPrinciapl = new FinancialAccount();
            caixaPrinciapl.setOrganization(organization);
            caixaPrinciapl.setName(DEFAULT_FINANCIAL_ACCOUNT_NAME);
            caixaPrinciapl.setType(GenericEntityType.DEFAULT.name());
            this.genericDAO.saveEntity(caixaPrinciapl);
        }
        return caixaPrinciapl;
    }

    //
    static enum CamposImportacaoCobranca implements IDescrpitorFieldLayout {

        NOME("Nome"),
        DATA_NASCIMENTO("Data Nascimento"),
        CIDADE_NASCIMENTO("Cidade Nacimento"), CPF("CPF"),
        ESTADO_CIVIL("Estado Civil"), PROFISSAO("Profissão"), RG("RG"),
        ORGAO_EMISSOR_RG("Orgão Emissor RG"),
        DATA_EMISSAO_RG_RESPOSNAVEL("Data Emissão RG"), E_MAIL("E-mail"), TELEFONES("Telefones"),
        CEP("CEP"), CIDADE("Cidade"), BAIRRO("Bairro"), COMPLEMENTO("Complemento"),
        //
        CODIGO_COBRANCA("Código Cobrança"),
        INSTITUICAO("Instituição"), CNPJ("CNPJ"),
        DESCRICAO_COBRANCA("Descrição"),
        PLANO_CONTAS("Plano Contas"),
        DATA_VENCIMENTO("Data Vencimento"),
        VALOR("Valor"),
        DATA_RECEBIMENTO("Data Recebimento"),
        VALOR_RECEBIDO("Valor Recebido"), EXTRA("Reservado");
        private String label;

        CamposImportacaoCobranca(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public String toString() {
            return label;
        }

        @Override
        public String getNomeColuna() {
            return label;
        }

        @Override
        public String getPropriedade() {
            return label;
        }
    }

    static String[] propriedadesArrayPlanilhaCobranca;

    static {
        List<String> tmp = new ArrayList<String>();
        for (CamposImportacaoCobranca ci : CamposImportacaoCobranca.values()) {
            tmp.add(ci.label);
        }
        propriedadesArrayPlanilhaCobranca = tmp.toArray(new String[0]);
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void importarDadosCobranca(Collection arr) {
        importarAccountTransaction(arr);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //protected EntradaCaixaPrevisto importarEntradaCaixaPrevisto(Collection arr) {
    protected AccountTransaction importarAccountTransaction(Collection arr) {
        AccountTransaction ecp = null;
        if (arr != null && isExisteElementoNaoNulo(arr)) {
            //preImportarEntradaCaixaPrevisto(arr);
            String codCobranca = getString(CamposImportacaoCobranca.CODIGO_COBRANCA, arr);
            ecp = accountTransactionByUniqueCode(codCobranca);
            if (ecp == null) {
                ecp = new AccountTransaction();
                ecp.setUniqueCode(codCobranca);
            }

            UtilReflection.setPropertyIfNull(ecp, "memo",
                    getString(CamposImportacaoCobranca.DESCRICAO_COBRANCA, arr)
            );

            UtilReflection.setPropertyIfNull(ecp, "datePrevist",
                    UtilDate.parseData(getString(CamposImportacaoCobranca.DATA_VENCIMENTO, arr))
            );
            Long valLong = null;
            try {
                valLong = getLong(CamposImportacaoCobranca.VALOR, arr);
                if (valLong != null) {
                    valLong = valLong * 100;
                }
            } catch (NumberFormatException e) {
                try {
                    Double aDouble = getDouble(CamposImportacaoCobranca.VALOR, arr);
                    if (aDouble != null) {
                        valLong = (long) (aDouble * 100.0d);
                    }
                } catch (Exception ex) {
                    log.debug("Can't parse value valor_recdebido: " + getString(CamposImportacaoCobranca.VALOR_RECEBIDO, arr));
                }
            }
            if (valLong != null) {
                UtilReflection.setPropertyIfNull(ecp, "creditTotal", BigDecimal.valueOf(valLong, 2));
            }

            UtilReflection.setPropertyIfNull(ecp, "dateExecuted",
                    UtilDate.parseData(getString(CamposImportacaoCobranca.DATA_RECEBIMENTO, arr))
            );

            Long valRecebido = null;
            try {
                Long valrec = getLong(CamposImportacaoCobranca.VALOR_RECEBIDO, arr);
                if (valrec != null) {
                    valRecebido = valrec * 100;
                }
            } catch (NumberFormatException e) {
                try {
                    Double valrec = getDouble(CamposImportacaoCobranca.VALOR_RECEBIDO, arr);
                    if (valrec != null) {
                        valRecebido = (long) (valrec * 100d);
                    }
                } catch (Exception ex) {
                    log.debug("Can't parse value valor_recdebido: " + getString(CamposImportacaoCobranca.VALOR_RECEBIDO, arr));
                }
            }

            if (valRecebido != null) {
                BigDecimal valueOf = BigDecimal.valueOf(valRecebido, 2);
                UtilReflection.setPropertyIfNull(ecp, "valueExecuted", valueOf);
            }

            if (ecp.getAccount() == null) {
                Account conta = accountByName(getString(CamposImportacaoCobranca.PLANO_CONTAS, arr));
                if (conta == null) {
                    conta = contaPrincipalReceita();
                }
                ecp.setAccount(conta);
            }

            FinancialAccount financialAccount = ecp.getFinancialAccount();

            if (financialAccount == null) {
                String strNome = getString(CamposImportacaoCobranca.INSTITUICAO, arr);
                String strCpf = getString(CamposImportacaoCobranca.CNPJ, arr);
                //TODO: Translate arr properites to subimport
                //pessoa = descriptorFilePartner.importPartner(arr);
                Organization organization = descriptorFilePartner.organization(strNome, strCpf);
                financialAccount = this.defaultFinancialAccount(organization);
                ecp.setFinancialAccount(financialAccount);

            }

            if (valRecebido != null && valRecebido > 0) {
                UtilReflection.setPropertyIfNull(ecp, "financialAccount", financialAccount);
            }

            Partner pessoa = ecp.getPartner();
            if (pessoa == null) {
                String strNome = getString(CamposImportacaoCobranca.NOME, arr);
                String strCpf = getString(CamposImportacaoCobranca.CPF, arr);
                //TODO: Translate arr properites to subimport
                //pessoa = descriptorFilePartner.importPartner(arr);
                pessoa = descriptorFilePartner.importPartner(strCpf, strNome);
                ecp.setPartner(pessoa);
            }
            genericDAO.saveEntity(ecp);
        }
        //genericDAO.flush();
        return ecp;
    }

    public FinancialAccount principalFinancialAccount() {
        FinancialAccount caixa = null;
        List<FinancialAccount> carregarColecao = genericDAO.loadCollection(FinancialAccount.class);
        if (carregarColecao != null && !carregarColecao.isEmpty()) {
            caixa = carregarColecao.get(0);
        }
        return caixa;
    }

    //    @Transactional
    protected AccountTransaction accountTransactionByUniqueCode(String codCobranca) {
        AccountTransaction ecp = null;
        try {
            ecp = this.genericDAO.loadEntityWhere(AccountTransaction.class, "uniqueCode", codCobranca);
        } catch (NoResultException e) {
        } catch (EmptyResultDataAccessException e) {
        } catch (Exception e) {
            log.info("Fail on search accountTransaction: " + codCobranca, e);
        }
        return ecp;
    }

    protected Account accountByName(String strPlanoContas) {
        Account conta = null;
        try {
            if (strPlanoContas != null && !(strPlanoContas = strPlanoContas.trim()).isEmpty()) {
                Query query = genericDAO.createQuery("SELECT e FROM Account e "
                        + "WHERE :name like lower(e.name) ");
                query.setParameter("name", strPlanoContas.trim().toLowerCase());
                List list = query.getResultList();
                if (list != null && !list.isEmpty()) {
                    conta = (Account) list.get(0);
                }
            }
        } catch (NoResultException e) {

        }
        return conta;
    }

    @Override
    public void importar(Collection arr) {
        this.importarAccountTransaction(arr);
    }
}
