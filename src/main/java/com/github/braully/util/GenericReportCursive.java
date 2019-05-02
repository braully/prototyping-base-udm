package com.github.braully.util;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.beanutils.PropertyUtils;

@SuppressWarnings({"rawtypes"})
public class GenericReportCursive extends GenericReport implements Serializable {

    /**
     *
     */

    /* */
    private List<GenericColumn> detalhes = new ArrayList<GenericColumn>();

    /**/
    public GenericReportCursive() {
    }

    public GenericReportCursive(Object dado, String titulo, String subtitulo, String introducao, Map parametros) {
        this.dado = dado;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.introducao = introducao;
        this.parametros = parametros;
    }

    public JasperPrint gerarRelatorioJasper(String titutlo, String subtitulo,
            String introducao, Object dado, Map parms)
            throws ColumnBuilderException, JRException {
        DynamicReportBuilder drb = new DynamicReportBuilder();
        /*
         * Parametros do relatório
         */
        Map parametros = processarParametros(parms);

        /**
         * Template de relatório padrão do EVV
         */
        drb.setTemplateFile(UtilPath.getPath(RELATORIO_TEMPLATE_CAMINHO_RELATIVO));

        /**
         * Estilos de formatação para o relatório
         */
        drb.setDefaultStyles(titleStyle, subTitleStyle, headerStyle, plain);

        /**
         * Espeficação de colunas da folha de pagamento
         */
        AbstractColumn colRel = ColumnBuilder.getNew().setTitle("").setColumnProperty("nome",
                String.class.getName()).setStyle(styleColNomeCampo).setHeaderStyle(headerStyle).build();
        colRel.setFixedWidth(true);
        colRel.setWidth(150);
        drb.addColumn(colRel);

        colRel = ColumnBuilder.getNew().setTitle("").setColumnProperty("valor",
                String.class.getName()).setStyle(plain).setHeaderStyle(headerStyle).build();
        drb.addColumn(colRel);

        drb.setPrintBackgroundOnOddRows(zebrado).setUseFullPageWidth(true);

//        Style rodapeStyle = new Style();
//
//        rodapeStyle.setFont(fontRodape);
//        drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y,
//                AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT);
        List<GenericValue> dados = new ArrayList<GenericValue>();

        for (GenericColumn det : detalhes) {
            try {
                Object prop = PropertyUtils.getProperty(dado, det.getProperty());
                GenericValue valor = new GenericValue(det, prop);
                dados.add(valor);
            } catch (Exception ex) {
                log.error("Erro ao extrair propriedade", ex);
                log.error(det);
            }
        }

        DynamicReport dr = drb.build();
        JRDataSource ds = new JRBeanCollectionDataSource(dados);
        JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr,
                new ClassicLayoutManager(), ds, parametros);
        return jp;
    }

    /**
     * Teste Local
     */
    public void adicionaGenericColumn(String nomeColuna, String nomePropriedade) {
        this.detalhes.add(new GenericColumn(nomeColuna, nomePropriedade));
    }

    public void adicionaGenericColumn(String nomeColuna, String nomePropriedade, Class tipo) {
        this.detalhes.add(new GenericColumn(nomeColuna, nomePropriedade, tipo));
    }

    public void adicionaGenericColumn(String nomeColuna, String nomePropriedade, Class tipo, String padrao) {
        this.detalhes.add(new GenericColumn(nomeColuna, nomePropriedade, tipo, padrao));
    }

    public void adicionaGenericColumn(String nomeColuna, String nomePropriedade,
            Class tipo, int tamanho) {
        this.detalhes.add(new GenericColumn(nomeColuna, nomePropriedade, tipo, tamanho));
    }

    public void adicionaGenericColumn(String nomeColuna, String nomePropriedade,
            Class tipo, int tamanho, String padrao) {
        this.detalhes.add(new GenericColumn(nomeColuna, nomePropriedade, tipo, tamanho, padrao));
    }

    public void adicionaGenericColumn(GenericColumn coluna) {
        this.detalhes.add(coluna);
    }
}
