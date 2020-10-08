package com.github.braully.business;

import com.github.braully.domain.Address;
import com.github.braully.domain.City;
import com.github.braully.domain.Organization;
import com.github.braully.domain.Partner;
import com.github.braully.domain.Phone;
import com.github.braully.persistence.GenericDAO;
import com.github.braully.util.UtilDate;
import com.github.braully.util.UtilEncode;
import com.github.braully.util.UtilFoneticPTBR;
import com.github.braully.util.UtilReflection;
import com.github.braully.util.UtilString;
import com.github.braully.util.UtilValidation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.NoResultException;
import lombok.Getter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braully
 */
@Component
public class DescriptorFilePartner extends DescriptorLayoutImportFile {

    @Getter
    public final String nomeLayout = "Importação de pessoas";
    @Getter
    public final String codigoLayout = UtilEncode.appendDv("PESSOA-V1.0");
    @Getter
    public final Class classe = Partner.class;
    //
    @Resource(name = "genericDAO")
    private GenericDAO genericDAO;

    public static enum DescritorCamposPessoa implements IDescrpitorFieldLayout {
        COD("Cod", "id"), NOME("Nome", "name"),
        CPF("CPF", "fiscalCode"), DATA_NASCIMENTO("Data Nascimento"),
        CIDADE_NASCIMENTO("Cidade Nacimento"), ESTADO_CIVIL("Estado Civil"),
        PROFISSAO("Profissão"), RG("RG"), ORGAO_EMISSOR_RG("Orgão Emissor RG"),
        DATA_EMISSAO_RG_RESPOSNAVEL("Data Emissão RG"),
        TELEFONES("Telefones", "telefonesFormatado"),
        CIDADE("Cidade", "endereco.cidade", "Endereço Pessoa"),
        BAIRRO("Bairro", "endereco.bairro", "Endereço Pessoa"),
        COMPLEMENTO("Complemento", "endereco.enderecoComplemento", "Endereço Pessoa"),
        CEP("CEP", "endereco.cep", "Endereço Pessoa"),
        E_MAIL("Email", true);
        //
        @Getter
        private final String nomeColuna;
        @Getter
        private final String propriedade;
        @Getter
        private final String grupo;
        @Getter
        private boolean atualizavel = false;

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
        public int getIndice() {
            return this.ordinal();
        }
    }

    public static final String[] propriedadesArrayPlanilhaArquitetura;

    static {
        List<String> tmp = new ArrayList<>();
        for (IDescrpitorFieldLayout ci : DescritorCamposPessoa.values()) {
            tmp.add(ci.getNomeColuna());
        }
        propriedadesArrayPlanilhaArquitetura = tmp.toArray(new String[0]);
    }

    @Override
    public IDescrpitorFieldLayout[] getDescritorCampos() {
        return DescritorCamposPessoa.values();
    }

    @Override
    public void importar(Collection arr) {
        this.importPartner(arr);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void importData(Collection arr) {
        importPartner(arr);
    }

    @Transactional
    protected Partner importPartner(Collection arr) {
        Partner pessoa = null;
        try {
            if (arr != null) {
                String cpf = getString(DescritorCamposPessoa.CPF, arr);
                if (cpf != null && !cpf.trim().isEmpty()) {
                    pessoa = partnerByFiscalCodeCpf(cpf);
                    if (pessoa == null) {
                        pessoa = new Partner();
                        pessoa.setNumeroComprovantePessoa(cpf);
                    } else {
                        pessoa = this.genericDAO.loadEntityFetch(pessoa, "infoExtra", "infoExtra.genericValues",
                                "infoExtra.simpleStringValues", "infoExtra.simpleNumberValues",
                                "contact", "contact.extraPhones", "contact.extraAddresses");
                    }

                    try {
                        String nameCapitalized = UtilFoneticPTBR.capitalize(getString(DescritorCamposPessoa.NOME, arr));
                        UtilReflection.setPropertyIfNull(pessoa, "name", nameCapitalized.trim());
                    } catch (Exception e) {

                    }

                    if (pessoa.getBirthDate() == null) {
                        String strDtNascimentoAluno = getString(DescritorCamposPessoa.DATA_NASCIMENTO, arr);
                        if (strDtNascimentoAluno != null && !strDtNascimentoAluno.trim().isEmpty()) {
                            Date dataNascimento = UtilDate.parseData(strDtNascimentoAluno);
                            if (dataNascimento != null) {
                                pessoa.setDataNascimento(dataNascimento);
                            }
                        }
                    }

                    if (pessoa.getBirthCity() == null) {
                        String strNaturalidade = getString(DescritorCamposPessoa.CIDADE_NASCIMENTO, arr);
                        City naturalidade = this.cityByName(strNaturalidade);
                        if (naturalidade != null && naturalidade.isPersisted()) {
                            pessoa.setNaturalidade(naturalidade);
                            //this.genericDAO.saveEntity(naturalidade);
                        }
                    }

                    //TODO: Busca cidade exata
                    if (!pessoa.contact().hasPhone()) {
                        for (Phone t : parserTelefones(getString(DescritorCamposPessoa.TELEFONES, arr))) {
                            pessoa.contact().add(t);
                        }
                    }
                    Address endereco = pessoa.getAddress();
                    if (endereco == null) {
                        //endereco = new legado.Endereco();
                        pessoa.setAddress(new Address());
                    }
                    String strEnderecoCidade = getString(DescritorCamposPessoa.CIDADE, arr);
                    City cidade = cityByName(strEnderecoCidade);
                    endereco.setCity(cidade);

                    UtilReflection.setPropertyIfNull(endereco, "zip", getString(DescritorCamposPessoa.CEP, arr));
                    UtilReflection.setPropertyIfNull(endereco, "district", getString(DescritorCamposPessoa.BAIRRO, arr));
                    UtilReflection.setPropertyIfNull(endereco, "addressLine1", getString(DescritorCamposPessoa.COMPLEMENTO, arr));

                    genericDAO.saveEntity(pessoa);
                }
            }
        } catch (Exception e) {
            log.error("Falha ao carregar ", e);
        }
        return pessoa;
    }

    protected List<Phone> parserTelefones(String strTelefones) {
        List<Phone> telefones = new ArrayList<>();
        if (strTelefones != null && !(strTelefones = strTelefones.trim()).isEmpty()) {
            String[] splitTelefones = strTelefones.split(SEPARADOR_PADRAO);
            if (splitTelefones != null && splitTelefones.length > 0) {
                for (String strTel : splitTelefones) {
                    String numero = strTel;
                    String prefixo = null;
                    String observcao = "";
                    try {
                        observcao = UtilString.extract(strTel, "(", ")");
                    } catch (Exception e) {

                    }
                    String[] split = strTel.split(" - ");
                    if (split.length == 2) {
                        numero = split[0];
                        observcao = split[1];
                    }
                    if (numero != null && numero.length() > TAM_STR_PEQUENO) {
                        String novNumero = numero.substring(0, TAM_STR_PEQUENO - 1);
                        String novaObStirng = numero.substring(TAM_STR_PEQUENO, numero.length() - 1);
                        numero = novNumero;
                        if (observcao != null) {
                            observcao = novaObStirng + " " + observcao;
                        } else {
                            observcao = novaObStirng;
                        }
                    }
                    if (numero != null && !numero.trim().isEmpty()) {
                        Phone telefone = new Phone();
                        if (UtilValidation.isStringValid(observcao)) {
                            telefone.type(observcao);
                            numero = numero.replaceAll("(" + observcao + ")", "");
                        }
                        telefone.number(numero);
                        telefones.add(telefone);
                    }
                }
            }
        }
        return telefones;
    }

    public City cityByName(String strCity) {
        if (!UtilValidation.isStringValid(strCity)) {
            return null;
        }
        City city = null;

        try {
            String nome = strCity;
            String uf = "";
            if (strCity.contains("-")) {
                nome = strCity.substring(0, strCity.lastIndexOf("-"));
                uf = strCity.substring(strCity.lastIndexOf("-") + 1);
            }
            List resultList = this.genericDAO.createQuery("SELECT c FROM City c "
                    + "WHERE LOWER(c.name) LIKE LOWER(:name) AND c.state LIKE :state")
                    .setParameter("name", nome)
                    .setParameter("state", uf).getResultList();
            if (!resultList.isEmpty()) {
                city = (City) resultList.get(0);
            }
        } catch (Exception e) {
            log.error("Fail on search city", e);
        }
        //return (City) this.genericDAO.queryObject(City.class, "name", strCity);
        return city;
    }

    public Partner partnerByFiscalCodeCpf(String cpf) {
        if (cpf == null) {
            cpf = "";
        }
        cpf = cpf.replaceAll("\\D+", "");
        String zeros = "00000000000";
        int rest = 11 - cpf.length();
        cpf = zeros.substring(0, rest) + cpf;
        return partnerByFiscalCode(cpf);
    }

    public Partner partnerByFiscalCode(String fiscalCode) {
        try {
            //return (Partner) this.genericDAO.loadEntityWhere(Partner.class, "fiscalCode", fiscalCode);
            String cpf = fiscalCode.replaceAll("\\D+", "");
            if (!UtilValidation.isStringValid(cpf)) {
                return null;
            }
            return (Partner) this.genericDAO.queryObject(
                    "SELECT e FROM Partner e "
                    + "LEFT JOIN FETCH e.infoExtra i "
                    + "LEFT JOIN FETCH i.simpleStringValues ssv "
                    + "LEFT JOIN FETCH i.simpleNumberValues snv "
                    + "LEFT JOIN FETCH e.contact c "
                    + "WHERE REGEXP_REPLACE(e.fiscalCode, '[^0-9]+', '', 'g') = ?1",
                    cpf);
        } catch (EmptyResultDataAccessException | NoResultException e) {
            return null;
        }
    }

    @Transactional
    public Organization organization(String strInstituicaoNome, String strInstituicaoCnpj) {
        Organization instituicao = organizationByFicalCode(strInstituicaoCnpj);
        if (instituicao == null) {
            instituicao = new Organization();
            instituicao.setName(strInstituicaoNome);
            instituicao.setFiscalCode(strInstituicaoCnpj);
            this.genericDAO.saveEntity(instituicao);
            log.info("Gerando nova instituição: " + instituicao);
        }
        return instituicao;
    }

    public Organization organizationByFicalCode(String fiscalCode) {
        Organization o = null;
        try {
            //return (Partner) this.genericDAO.loadEntityWhere(Partner.class, "fiscalCode", fiscalCode);
            String fiscalCodeNumbers = fiscalCode.replaceAll("\\D+", "");
            o = (Organization) this.genericDAO.queryObject("SELECT e FROM Organization e WHERE REGEXP_REPLACE(e.fiscalCode, '[^0-9]+', '', 'g')  = ?1", fiscalCodeNumbers);
        } catch (EmptyResultDataAccessException | NoResultException e) {
            return null;
        } catch (Exception e) {
            log.debug("Error on find organization", e);
        }
        return o;
    }

    @Transactional
    public Partner importPartner(String strCpfPartner, String strNomePartner) {
        Partner pf = null;
        if (strNomePartner != null && strCpfPartner != null
                && !strCpfPartner.trim().isEmpty()
                && !strNomePartner.trim().isEmpty()) {
            try {
                pf = this.partnerByFiscalCode(strCpfPartner);
                if (pf == null) {
                    pf = new Partner();
                    pf.setName(strNomePartner.trim());
                    pf.setFiscalCode(strCpfPartner);
                    genericDAO.saveEntity(pf);
                    log.info("Gerando novo parceiro");
                    log.info(pf);
                }
            } catch (Exception e) {
                log.debug("falha ao importar professor", e);
            }
        }
        return pf;
    }
}
