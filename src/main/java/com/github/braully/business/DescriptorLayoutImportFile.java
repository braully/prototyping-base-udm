package com.github.braully.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author normal
 */
public abstract class DescriptorLayoutImportFile {

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

    public boolean assinadoPara(String codigoLayout, String nomeArquivo, Class classe) {
        boolean ret = false;
        if (codigoLayout != null && !codigoLayout.trim().isEmpty()
                && codigoLayout.trim().equals(getCogigoLayout())) {
            ret = true;
        }
        if (nomeArquivo != null && nomeArquivo.trim().equals(getNomeLayout())) {
            ret = true;
        }
        if (classe != null && this.getClasse().isAssignableFrom(classe)) {
            ret = true;
        }
        return ret;
    }

    public abstract String getNomeLayout();

    public abstract String getCogigoLayout();

    public abstract Class getClasse();

    public IDescrpitorFieldLayout getDescritorId() {
        IDescrpitorFieldLayout desc = null;
        IDescrpitorFieldLayout[] descritorCampos = this.getDescritorCampos();
        if (descritorCampos != null && descritorCampos.length > 0) {
            desc = descritorCampos[0];
        }
        return desc;
    }

    public abstract IDescrpitorFieldLayout[] getDescritorCampos();

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
}
