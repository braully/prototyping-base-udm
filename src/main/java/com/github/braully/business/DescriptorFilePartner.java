package com.github.braully.business;

import com.github.braully.domain.Partner;
import com.github.braully.util.UtilEncode;
import org.springframework.stereotype.Component;

/**
 *
 * @author braully
 */
@Component
public class DescriptorFilePartner extends DescriptorLayoutImportFile {

    public static final String NOME_LAYOUT = "Importação de pessoas v1";
    public static final String CODIGO_LAYOUT = UtilEncode.encodeSimples("PESSOA-V1.0");
    public static final Class CLASSE = Partner.class;

    public static enum DescritorCamposPessoa implements IDescrpitorFieldLayout {

        COD("Cod", "id"), NOME("Nome"),
        CPF("CPF", "numeroComprovantePessoaFormatado"),
        TELEFONES("Telefones", "telefonesFormatado"),
        CIDADE("Cidade", "endereco.cidade", "Endereço Pessoa"),
        BAIRRO("Bairro", "endereco.bairro", "Endereço Pessoa"),
        COMPLEMENTO("Complemento", "endereco.enderecoComplemento", "Endereço Pessoa"),
        CEP("CEP", "endereco.cep", "Endereço Pessoa"),
        E_MAIL("email", true);
        private final String nomeColuna;
        private final String propriedade;
        private final String grupo;
        private Boolean atualizavel = false;

        DescritorCamposPessoa(String label) {
            this.nomeColuna = label;
            this.propriedade = label.toLowerCase();
            this.grupo = GRUPO_DEFAULT;
        }

        DescritorCamposPessoa(String label, Boolean atuaBoolean) {
            this(label);
            this.atualizavel = atuaBoolean;
        }

        private DescritorCamposPessoa(String nomeColuna, String propriedade) {
            this(nomeColuna, propriedade, GRUPO_DEFAULT);
        }

        private DescritorCamposPessoa(String nomeColuna, String propriedade, String grupo) {
            this.nomeColuna = nomeColuna;
            this.propriedade = propriedade;
            this.grupo = grupo;
        }

        @Override
        public boolean getAtualizavel() {
            return atualizavel;
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
        return DescritorCamposPessoa.values();
    }
}
