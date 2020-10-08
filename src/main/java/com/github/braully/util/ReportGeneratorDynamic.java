package com.github.braully.util;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import com.github.braully.app.logutil;
import com.github.braully.interfaces.IReportGenerator;
import java.awt.Color;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"rawtypes"})
public class ReportGeneratorDynamic implements IReportGenerator {

    static final Logger log = LogManager.getLogger(ReportGeneratorDynamic.class);
    /*
     *
     */
 /*
         *
     */
    public static final String RELATORIO_TEMPLATE_CAMINHO_RELATIVO = "templates/template-simplificado.jrxml";
    public static final DateFormat defaultFormat = new SimpleDateFormat("dd/MM/yyyy");

    /*
         *
     */
    protected static final Font fonteCabecalho, fontRodape, fonteCorpo;
    protected static final Style headerStyle, headerStyleGroup, plain, central, styleColNomeCampo, titleStyle, subTitleStyle;
    //
    protected List<GenericColumn> colunas = new ArrayList<GenericColumn>();
    protected boolean zebrado;
    protected String titulo, subtitulo, introducao;
    //
    protected List<GenericColumn> detalhes = new ArrayList<GenericColumn>();
    protected boolean cursivo;

    static {
        //TODO: Definir os Styles no template de relatorio (usando o ireport->nó Styles)
        fonteCabecalho = new Font(10, "Arial", "Helvetica-Bold", Font.PDF_ENCODING_CP1252_Western_European_ANSI, true);
        fonteCorpo = new Font(9, "Arial", false);
        fontRodape = new Font(7, "Arial", true);

        headerStyle = novoEstilo(fonteCabecalho, Color.LIGHT_GRAY,
                Border.PEN_1_POINT(), VerticalAlign.MIDDLE, HorizontalAlign.CENTER);
        headerStyleGroup = novoEstilo(fonteCabecalho, Color.GRAY,
                Border.PEN_1_POINT(), VerticalAlign.MIDDLE, HorizontalAlign.LEFT);
        plain = novoEstilo(Border.PEN_1_POINT(), VerticalAlign.MIDDLE);
        central = novoEstilo(Border.PEN_1_POINT(), VerticalAlign.MIDDLE, HorizontalAlign.CENTER);

        styleColNomeCampo = novoEstilo(Border.NO_BORDER(), VerticalAlign.MIDDLE,
                HorizontalAlign.RIGHT);
        styleColNomeCampo.setFont(new Font((int) Font.ARIAL_MEDIUM_BOLD.getFontSize(),
                Font.ARIAL_MEDIUM_BOLD.getFontName(), "Helvetica-Bold",
                Font.PDF_ENCODING_CP1252_Western_European_ANSI, true));

        titleStyle = new Style("titulo");
        titleStyle.setFont(Font.ARIAL_BIG_BOLD);
        titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        titleStyle.setVerticalAlign(VerticalAlign.MIDDLE);

        subTitleStyle = new Style("subtitulo");
        subTitleStyle.setParentStyleName("titulo");
        subTitleStyle.setFont(Font.ARIAL_BIG_BOLD);
        subTitleStyle.setHorizontalAlign(HorizontalAlign.LEFT);
        subTitleStyle.setVerticalAlign(VerticalAlign.MIDDLE);
    }

    static Style novoEstilo(Font fonte, Color cor, Border borda, VerticalAlign vAlinhamento, HorizontalAlign hAlinhamento) {
        Style style = new Style();
        style.setFont(fonte);
        style.setBorder(borda);
        style.setBackgroundColor(cor);
        style.setVerticalAlign(vAlinhamento);
        style.setHorizontalAlign(hAlinhamento);
        style.setTransparency(Transparency.OPAQUE);
        return style;
    }

    static Style novoEstilo(Border borda, VerticalAlign vAlinhamento,
            HorizontalAlign hAlinhamento) {
        Style style = new Style();
        style.setBorder(borda);
        style.setVerticalAlign(vAlinhamento);
        style.setHorizontalAlign(hAlinhamento);
        return style;
    }

    static Style novoEstilo(Border borda, VerticalAlign vAlinhamento) {
        return novoEstilo(borda, vAlinhamento, HorizontalAlign.LEFT);
    }

    protected Map processarParametros(Map parms) {
        Map parametros = new HashMap();
        if (parms != null) {
            parametros.putAll(parms);
            Object det = parms.get("DETALHES_EMPRESA");
            if (det != null) {
                parametros.put("CABECALHO", det);
            }
        }

        if (titulo != null && !titulo.trim().isEmpty()) {
            parametros.put("TITULO", titulo.trim());
        }
        if (subtitulo != null && !subtitulo.trim().isEmpty()) {
            parametros.put("SUBTITULO", subtitulo.trim());
        }
        if (introducao != null && !introducao.trim().isEmpty()) {
            parametros.put("INTRODUCAO", introducao.trim());
        }
        return parametros;
    }

    public InputStream generate(GenericDataReport data) {
        return this.generatePDFStream(data);
    }

    public List<GenericValue> beanToPropertieCollection(Object dado) {
        return beanToPropertieCollection(detalhes, dado);
    }

    public List<GenericValue> beanToPropertieCollection(List<? extends GenericColumn> propsColumn, Object dado) {
        List<GenericValue> dados = new ArrayList<GenericValue>();

        for (GenericColumn det : propsColumn) {
            try {
                Object prop = PropertyUtils.getProperty(dado, det.getProperty());
                GenericValue valor = new GenericValue(det, prop);
                dados.add(valor);
            } catch (Exception ex) {
                logutil.error("Erro ao extrair propriedade " + det, ex);
            }
        }
        return dados;
    }

    public void adicionaColunaAgrupada(String nomeColuna, String nomePropriedade) {
        this.colunas.add(new GenericColumn(nomeColuna, nomePropriedade, true));
    }

    public void adicionaColuna(String nomeColuna, String nomePropriedade) {
        this.colunas.add(new GenericColumn(nomeColuna, nomePropriedade));
    }

    public void adicionaColuna(String nomeColuna, String nomePropriedade, Class tipo) {
        this.colunas.add(new GenericColumn(nomeColuna, nomePropriedade, tipo));
    }

    //
    public void adicionaColunaCursiva(String nomeColuna, String nomePropriedade) {
        this.detalhes.add(new GenericColumn(nomeColuna, nomePropriedade));
    }

    public void adicionaColunaCursiva(String nomeColuna, String nomePropriedade, Class tipo) {
        this.detalhes.add(new GenericColumn(nomeColuna, nomePropriedade, tipo));
    }

    public void adicionaColunaCursiva(String nomeColuna, String nomePropriedade, Class tipo, String padrao) {
        this.detalhes.add(new GenericColumn(nomeColuna, nomePropriedade, tipo, padrao));
    }

    public void adicionaColunaCursiva(String nomeColuna, String nomePropriedade,
            Class tipo, int tamanho) {
        this.detalhes.add(new GenericColumn(nomeColuna, nomePropriedade, tipo, tamanho));
    }

    public void adicionaColunaCursiva(String nomeColuna, String nomePropriedade,
            Class tipo, int tamanho, String padrao) {
        this.detalhes.add(new GenericColumn(nomeColuna, nomePropriedade, tipo, tamanho, padrao));
    }

    public void adicionaColunaCursiva(GenericColumn coluna) {
        this.detalhes.add(coluna);
    }

    public InputStream generatePDFStream(GenericDataReport data) {
        InputStream input = null;
        JRDataSource ds = null;
        try {
            DynamicReportBuilder drb = new DynamicReportBuilder();
            /*
            * Parametros do relatório
             */
            Map parametros = processarParametros(data.getParametros());
            /**
             * Template de relatório padrão do EVV
             */
            drb.setTemplateFile(UtilPath.getPath("templates/template-simplificado.jrxml"));
            /**
             * Estilos de formatação para o relatório
             */
            drb.setDefaultStyles(titleStyle, subTitleStyle,
                    headerStyle, central);

            if (cursivo) {
                AbstractColumn colRel = ColumnBuilder.getNew().setTitle("").setColumnProperty("nome",
                        String.class.getName()).setStyle(styleColNomeCampo).setHeaderStyle(headerStyle).build();
                colRel.setFixedWidth(true);
                colRel.setWidth(150);
                drb.addColumn(colRel);

                colRel = ColumnBuilder.getNew().setTitle("").setColumnProperty("valor",
                        String.class.getName()).setStyle(plain).setHeaderStyle(headerStyle).build();
                drb.addColumn(colRel);
                ds = new JRBeanCollectionDataSource(this.beanToPropertieCollection(data.getData()));
            } else if (colunas != null && !colunas.isEmpty()) {
                for (GenericColumn coluna : colunas) {
                    Style style = central;
                    AbstractColumn colNome = ColumnBuilder.getNew().setTitle(coluna.getName())
                            .setColumnProperty(coluna.getProperty(), coluna.getType().getName())
                            .setStyle(style).setHeaderStyle(headerStyle).build();
                    if (coluna.isGroupable()) {
                        colNome.setHeaderStyle(headerStyleGroup);
                        GroupBuilder gb = new GroupBuilder();
                        DJGroup g = gb.setCriteriaColumn((PropertyColumn) colNome)
                                .setGroupLayout(GroupLayout.VALUE_IN_HEADER).build();
                        drb.addGroup(g);
                    }
                    drb.addColumn(colNome);
                }

                ds = new JRBeanCollectionDataSource(data.getCollectionData());

            }
            drb.setPrintBackgroundOnOddRows(zebrado).setUseFullPageWidth(true);
            Style rodapeStyle = new Style();
            rodapeStyle.setFont(fontRodape);
            DynamicReport dr = drb.build();
            JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr,
                    new ClassicLayoutManager(), ds, parametros);
            input = ReportGeneratorJasper.jasperPrintToInputStream(jp);
        } catch (Exception ex) {
            logutil.error("Fail on load", ex);
            throw new IllegalStateException("Fail on generic report generator", ex);
        }
        return input;
    }
}
