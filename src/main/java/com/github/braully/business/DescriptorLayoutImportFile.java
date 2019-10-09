package com.github.braully.business;

import com.github.braully.util.UtilConversor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author normal
 */
public abstract class DescriptorLayoutImportFile {

    public static final Logger log = LogManager.getLogger(DescriptorLayoutImportFile.class);

    //
    public static final String LAYOUT_CODIGO = "Layout:".toUpperCase();
    public static final String LAYOUT_NOME = "Nome:".toUpperCase();
    //
    public static final List<String> PROPRIEDADES_LAYOUT = List.of(LAYOUT_CODIGO,
            LAYOUT_NOME);
    //
    public static final String SEPARADOR_PADRAO = ",";
    public static final int TAM_STR_PEQUENO = 25;
    public static final int TAM_STR_MEDIO = 50;
    public static final int TAM_STR = 100;
    //

    protected Map<String, String> mapPropriedades;

    public List<String> getNomeColunaPropriedades() {
        List<String> str = null;
        IDescrpitorFieldLayout[] descs = getDescritorCampos();
        if (descs != null) {
            str = new ArrayList<>();
            for (IDescrpitorFieldLayout desc : descs) {
                String strNomeColUp = desc.getNomeColuna().trim().toUpperCase();
                str.add(strNomeColUp);
            }
        }
        return str;
    }

    boolean assinadoPara(Map<String, Object> propriedadesArquivo) {
        Object layoutNome = propriedadesArquivo.get(LAYOUT_NOME);
        Object layoutVersao = propriedadesArquivo.get(LAYOUT_CODIGO);
        return assinadoPara(UtilConversor.getStringValue(layoutVersao),
                UtilConversor.getStringValue(layoutNome), null);
    }

    public boolean assinadoPara(String codigoLayout, String nomeArquivo, Class classe) {
        boolean ret = false;
        if (codigoLayout != null && !codigoLayout.trim().isEmpty()
                && codigoLayout.trim().equalsIgnoreCase(getCodigoLayout())) {
            ret = true;
        }
        if (nomeArquivo != null && nomeArquivo.trim().equalsIgnoreCase(getNomeLayout())) {
            ret = true;
        }
        if (classe != null && this.getClasse().isAssignableFrom(classe)) {
            ret = true;
        }
        return ret;
    }

    public IDescrpitorFieldLayout getDescritorId() {
        IDescrpitorFieldLayout desc = null;
        IDescrpitorFieldLayout[] descritorCampos = this.getDescritorCampos();
        if (descritorCampos != null && descritorCampos.length > 0) {
            desc = descritorCampos[0];
        }
        return desc;
    }

    public Map<String, String> getMapPropriedadesBean() {
        if (this.mapPropriedades == null) {
            this.mapPropriedades = new HashMap<>();
            for (IDescrpitorFieldLayout desc : this.getDescritorCampos()) {
                String nomeColuna = desc.getNomeColuna();
                String propriedade = desc.getPropriedade();
                this.mapPropriedades.put(propriedade, propriedade);
            }
        }
        return this.mapPropriedades;
    }

    protected String getString(Enum camposImportacao, Object[] arr) {
        String ret = null;
        if (camposImportacao != null && arr.length > camposImportacao.ordinal()) {
            int ordinal = camposImportacao.ordinal();
            Object o = arr[ordinal];
            if (o != null) {
                ret = UtilConversor.getStringValue(o);
            }
            if (ret != null) {
                ret = ret.trim();
            }
        }
        return ret;
    }

    protected Long getLong(Enum camposImport, Object[] arr) {
        Long ret = null;
        if (camposImport != null && camposImport.ordinal() < arr.length) {
            int ordinal = camposImport.ordinal();
            Object o = arr[ordinal];
            ret = UtilConversor.getLongValue(o);
        }
        return ret;
    }

    protected boolean isExisteElementoNaoNulo(Object[] arr) {
        boolean ret = false;
        if (arr != null && arr.length > 0) {
            int i = 0;
            while (!ret && i < arr.length) {
                ret = arr[i++] != null;
            }
        }
        return ret;
    }

    public String getTipo() {
        return getNomeLayout() + ":" + getCodigoLayout();
    }

    public abstract String getNomeLayout();

    public abstract String getCodigoLayout();

    public abstract Class getClasse();

    public abstract IDescrpitorFieldLayout[] getDescritorCampos();

    public abstract void importar(Object[] arr);

    void importarSeTemElemento(Object[] arr) {
        if (isExisteElementoNaoNulo(arr)) {
            importar(arr);
        }
    }

    @Override
    public String toString() {
        return "Descriptor-" + getNomeLayout() + " (#" + getCodigoLayout() + ')';
    }
}
