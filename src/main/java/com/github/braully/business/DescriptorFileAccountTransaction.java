package com.github.braully.business;

import static com.github.braully.business.IDescrpitorFieldLayout.GRUPO_DEFAULT;
import com.github.braully.domain.AccountTransaction;
import com.github.braully.util.UtilEncode;
import org.springframework.stereotype.Component;

/**
 *
 * @author braully
 */
@Component
public class DescriptorFileAccountTransaction extends DescriptorLayoutImportFile {

    public static final String NOME_LAYOUT = "Importação de contas a receber v1";
    public static final String CODIGO_LAYOUT = UtilEncode.encodeSimples("CONTA-RECEBER-V1.0");
    public static final Class CLASSE = AccountTransaction.class;

    public static enum DescritorCamposBoletoEmitido implements IDescrpitorFieldLayout {

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

        private final String nomeColuna;
        private final String propriedade;
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
        public String getGrupo() {
            return grupo;
        }

        @Override
        public int getIndice() {
            return this.ordinal();
        }

        @Override
        public String getNomeColuna() {
            return nomeColuna;
        }

        @Override
        public String getPropriedade() {
            return propriedade;
        }
    }

    @Override
    public String getNomeLayout() {
        return NOME_LAYOUT;
    }

    @Override
    public String getCogigoLayout() {
        return CODIGO_LAYOUT;
    }

    @Override
    public Class getClasse() {
        return CLASSE;
    }

    @Override
    public IDescrpitorFieldLayout[] getDescritorCampos() {
        return DescritorCamposBoletoEmitido.values();
    }
}
