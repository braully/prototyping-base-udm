package com.github.braully.report;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import com.github.braully.domain.FinancialAccount;
import com.github.braully.domain.util.CashFlow;
import com.github.braully.domain.util.FinancialPeriod;
import com.github.braully.util.UtilDate;
import com.github.braully.util.UtilPath;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author braullyrocha
 */
public class CashFlowReport {

    public CashFlowReport() {

    }

    public JasperPrint buildReport(CashFlow fluxoCaixa, Map<String, Object> param) throws Exception {
        Map<String, Object> parametros = new HashMap<String, Object>();
        StringBuilder introducao = new StringBuilder();
        parametros.put("TITLE", "Cash Flow");
        introducao.append("Period: ");
        introducao.append(UtilDate.formatData("dd/MM/yyyy", fluxoCaixa.getDateIni()));
        introducao.append(" - ");
        introducao.append(UtilDate.formatData("dd/MM/yyyy", fluxoCaixa.getDateEnd()));
        introducao.append("\n");
        introducao.append("Cashier(s): ");
        List<FinancialAccount> caixas = fluxoCaixa.getFinancialAccounts();
        if (caixas != null) {
            for (int i = 0; i < caixas.size(); i++) {
                FinancialAccount caixa = caixas.get(i);
                introducao.append(caixa.getName());
                if (i < caixas.size() - 1) {
                    introducao.append(", ");
                }
            }
        }
        introducao.append("\n");
        introducao.append("\n");
        parametros.put("INTRO", introducao.toString());
        if (param != null) {
            parametros.putAll(param);
            Object det = param.get("DETAILS");
            if (det != null) {
                parametros.put("HEADER", det);
            }
        }

        Style titleStyle = new Style();
        Style plainStyle = new Style();
        Style subtitleStyle = new Style();

        Style colCentralStyle = new Style();
        colCentralStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
        colCentralStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        colCentralStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        colCentralStyle.setBorder(Border.PEN_1_POINT());

        Style colStyle = new Style();
        colStyle.setFont(Font.ARIAL_MEDIUM);
        colStyle.setBorder(Border.PEN_1_POINT());
        colStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        colStyle.setHorizontalAlign(HorizontalAlign.CENTER);

        Style headStyle = new Style();
        headStyle.setBorder(Border.PEN_1_POINT());
        headStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        headStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        headStyle.setFont(Font.ARIAL_MEDIUM_BOLD);

        Style detailStyle = new Style();
        detailStyle.setBorder(Border.PEN_1_POINT());

        DynamicReportBuilder drb = new DynamicReportBuilder();

        drb.setDefaultStyles(titleStyle, subtitleStyle, plainStyle, detailStyle);
        drb.setMargins(30, 20, 30, 15);
        drb.setTemplateFile(UtilPath.getPath("template-simple-landscape.jrxml"));
        drb.setOddRowBackgroundStyle(colCentralStyle);
        drb.setUseFullPageWidth(true);

        List dados = fluxoCaixa.getLinesRepSynthetic();

        AbstractColumn columnConteudo = ColumnBuilder.getNew()
                .setColumnProperty("name", String.class.getName())
                .setTitle("").setStyle(colStyle)
                .build();
        drb.addColumn(columnConteudo);

        int i = 0;
        for (FinancialPeriod det : fluxoCaixa.getPeriodss()) {
            columnConteudo = ColumnBuilder.getNew()
                    .setColumnProperty("value[" + i++ + "]", Object.class.getName())
                    .setTitle(det.getDescricaoFormatada())
                    .setStyle(colStyle)
                    .setHeaderStyle(headStyle).build();
            drb.addColumn(columnConteudo);
        }

        DynamicReport dr = drb.build();
        JRDataSource ds = new JRBeanCollectionDataSource(dados);
        JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr,
                new ClassicLayoutManager(), ds, parametros);
        return jp;
    }

    public byte[] gerarRelatorioByte(Map param, Object bean) {
        try {
            JasperPrint jp = this.buildReport((CashFlow) bean, param);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jp, baos);
            baos.flush();
            byte[] ret = baos.toByteArray();
            baos.close();
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
