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
import com.github.braully.domain.util.ReportTemplate;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;

@SuppressWarnings({"rawtypes"})
public class GenericReport extends ReportGenerator {

    static final Logger log = Logger.getLogger(GenericReport.class);
    /*
     *
     */
    public static final String RELATORIO_TEMPLATE_CAMINHO_RELATIVO = "WEB-INF/relatorio/template-simplificado.jrxml";
    public static final DateFormat defaultFormat = new SimpleDateFormat("dd/MM/yyyy");

    /*
     *
     */
    protected static final Font fonteCabecalho, fontRodape, fonteCorpo;
    protected static final Style headerStyle, headerStyleGroup, plain, central, styleColNomeCampo, titleStyle, subTitleStyle;
    protected List<GenericColumn> colunas = new ArrayList<GenericColumn>();
    protected boolean zebrado;
    protected String titulo, subtitulo, introducao;
    protected Object dado;
    protected Map parametros;

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
        styleColNomeCampo.setFont(new Font(Font.ARIAL_MEDIUM_BOLD.getFontSize(),
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

    public byte[] gerarRelatorioPDFByte() throws Exception {
        JasperPrint jp = this.gerarRelatorioJasper(titulo, subtitulo,
                introducao, dado, parametros);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, baos);
        baos.flush();
        byte[] ret = baos.toByteArray();
        baos.close();
        return ret;
    }

    public byte[] gerarRelatorioPDFByte(Map parametros) throws Exception {
        JasperPrint jp = this.gerarRelatorioJasper(titulo, subtitulo,
                introducao, dado, parametros);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, baos);
        baos.flush();
        byte[] ret = baos.toByteArray();
        baos.close();
        return ret;
    }

    public byte[] gerarRelatorioPDFByte(Object dado, Map parametros) throws Exception {
        JasperPrint jp = this.gerarRelatorioJasper(titulo, subtitulo,
                introducao, dado, parametros);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, baos);
        baos.flush();
        byte[] ret = baos.toByteArray();
        baos.close();
        return ret;
    }

    public byte[] gerarRelatorioPDFByte(String titulo, String subtitulo,
            String introducao, Object dado, Map parametros) throws Exception {
        JasperPrint jp = this.gerarRelatorioJasper(titulo, subtitulo,
                introducao, dado, parametros);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, baos);
        baos.flush();
        byte[] ret = baos.toByteArray();
        baos.close();
        return ret;
    }

    public byte[] gerarRelatorioPDFByte(Collection dados, Map params) throws Exception {
        JasperPrint jp = this.gerarRelatorioJasper(titulo, subtitulo, introducao, dados, params);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, baos);
        baos.flush();
        byte[] ret = baos.toByteArray();
        baos.close();
        return ret;
    }

    public byte[] gerarRelatorioPDFByte(Collection dados) throws Exception {
        JasperPrint jp = this.gerarRelatorioJasper(dados);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, baos);
        baos.flush();
        byte[] ret = baos.toByteArray();
        baos.close();
        return ret;
    }

    public JasperPrint gerarRelatorioJasper(Collection dados) throws Exception {
        return this.gerarRelatorioJasper(titulo, subtitulo, introducao, dados, parametros);
    }

    public JasperPrint gerarRelatorioJasper(String titutlo, String subtitulo,
            String introducao, Object dado, Map parms) throws Exception {
        DynamicReportBuilder drb = new DynamicReportBuilder();
        /*
         * Parametros do relatório
         */
        Map parametros = processarParametros(parms);

        /**
         * Template de relatório padrão do EVV
         */
        drb.setTemplateFile(UtilPath.getPath("WEB-INF/relatorio/template-simplificado.jrxml"));

        /**
         * Estilos de formatação para o relatório
         */
        drb.setDefaultStyles(titleStyle, subTitleStyle, headerStyle, central);

        /**
         * Espeficação de colunas da folha de pagamento
         */
        if (colunas != null && !colunas.isEmpty()) {
            for (GenericColumn coluna : colunas) {
                Style style = central;
                AbstractColumn colNome = ColumnBuilder.getNew().setTitle(coluna.name).setColumnProperty(coluna.property, coluna.type.getName()).setStyle(style).setHeaderStyle(headerStyle).build();
                if (coluna.isGroupable()) {
                    colNome.setHeaderStyle(headerStyleGroup);
                    GroupBuilder gb = new GroupBuilder();
                    DJGroup g = gb.setCriteriaColumn((PropertyColumn) colNome).setGroupLayout(GroupLayout.VALUE_IN_HEADER).build();
                    drb.addGroup(g);
                }
                drb.addColumn(colNome);
            }
        }

        drb.setPrintBackgroundOnOddRows(
                zebrado).setUseFullPageWidth(true);

        Style rodapeStyle = new Style();

        rodapeStyle.setFont(fontRodape);

        DynamicReport dr = drb.build();
        JRDataSource ds = new JRBeanCollectionDataSource((Collection) dado);
        JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr,
                new ClassicLayoutManager(), ds, parametros);
        return jp;
    }

    /**
     * Teste Local
     */
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

    public void adicionaColunaAgrupada(String nomeColuna, String nomePropriedade) {
        this.colunas.add(new GenericColumn(nomeColuna, nomePropriedade, true));
    }

    public void adicionaColuna(String nomeColuna, String nomePropriedade) {
        this.colunas.add(new GenericColumn(nomeColuna, nomePropriedade));
    }

    public void adicionaColuna(String nomeColuna, String nomePropriedade, Class tipo) {
        this.colunas.add(new GenericColumn(nomeColuna, nomePropriedade, tipo));
    }

    public boolean isZebrado() {
        return zebrado;
    }

    public void setZebrado(boolean zebrado) {
        this.zebrado = zebrado;
    }

    @Override
    public byte[] gerarRelatorioByte(ReportTemplate relatorio, Map param, Collection colecao) throws RuntimeException {
        try {
            return this.gerarRelatorioPDFByte(colecao, param);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getIntroducao() {
        return introducao;
    }

    public void setIntroducao(String introducao) {
        this.introducao = introducao;
    }

    public Map getParametros() {
        return parametros;
    }

    public void setParametros(Map parametros) {
        this.parametros = parametros;
    }

    public Object getDado() {
        return dado;
    }

    public void setDado(Object dado) {
        this.dado = dado;
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
}
