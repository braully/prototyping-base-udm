package com.github.braully.report;

import com.github.braully.report.ReportGeneratorJasper;
import com.github.braully.report.ReportGeneratorDynamic;
import com.github.braully.report.ReportGeneratorMSXExcel;
import com.github.braully.report.ReportGeneratorMSXOffice;
import com.github.braully.interfaces.IReportGenerator;
import com.github.braully.util.UtilValidation;
import java.io.InputStream;

public class GenericReportGenerator {

    public static final ReportGeneratorMSXExcel generatorReportXlsx = new ReportGeneratorMSXExcel();
    public static final ReportGeneratorMSXOffice generatorReportDocx = new ReportGeneratorMSXOffice();
    public static final ReportGeneratorJasper generatorReportJasper = new ReportGeneratorJasper();
    public static final ReportGeneratorDynamic defaultReport = new ReportGeneratorDynamic();

    public InputStream generate(GenericDataReport datareport) {
        InputStream input = null;
        String templateExtension = datareport.getTemplateExtension();
        IReportGenerator reportGenerator = defaultReport;
        if (!UtilValidation.isStringValid(templateExtension)) {
            templateExtension = "";
        }
        switch (templateExtension) {
            case "xlsx":
                reportGenerator = generatorReportXlsx;
                break;
            case "docx":
                reportGenerator = generatorReportDocx;
                break;
            case "jasper":
                reportGenerator = generatorReportJasper;
                break;
            default:
                reportGenerator = defaultReport;
        }
        input = generate(reportGenerator, datareport);
        return input;
    }

    public InputStream generate(IReportGenerator generator, GenericDataReport datareport) {
        InputStream input = null;
        input = generator.generate(datareport);
        return input;
    }
}
