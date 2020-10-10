package com.github.braully.report;

import com.github.braully.util.logutil;
import com.github.braully.business.DescriptorLayoutImportFile;
import com.github.braully.domain.ReportTemplate;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.github.braully.business.IDescrpitorFieldLayout;
import com.github.braully.domain.BinaryFile;
import com.github.braully.interfaces.IReportGenerator;
import com.github.braully.persistence.IEntity;
import com.github.braully.util.UtilIO;
import com.github.braully.util.logutil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.util.CellReference;

/**
 *
 * @author user
 */
public class ReportGeneratorMSXExcel implements IReportGenerator {

    private static final String MARCADOR_INICIO_TEMPLATE = "GRUPO";
    private static final String MARCADOR_NOME_LAYOUT = "LAYOUT:";
    private static final Logger log = LogManager.getLogger(ReportGeneratorMSXExcel.class);
    /* */
    private static final int MAX_LINHAS_CONSIDERAR = 5000;
    public static final String GERADOR_TIPO = ".xlsx";
    public static final String TMP_DEFAULT_SUFIX = ".erpbrs.tmp";
    private static final String VALOR_DEFAULT = "--";

    private XSSFWorkbook documentoAtual;
    private XSSFSheet planilhaAtual;
    private Integer linhaPropriedades;
    private XSSFCellStyle cellStyleDatePlanilhaAtuall;
    private Map<String, Integer> indiceColunaPropriedades = new HashMap<>();
    private Map<String, XSSFCell> propriedadesEncontradas = new HashMap<>();

    public ReportGeneratorMSXExcel() {
    }

    public ReportGeneratorMSXExcel(String template) throws IOException {
        this(UtilIO.loadStreamFromFilePath(template));
    }

    public ReportGeneratorMSXExcel(InputStream input) throws IOException {
        this.documentoAtual = new XSSFWorkbook(input);
        this.planilhaAtual = documentoAtual.getSheetAt(0);
    }

    /* */
    public Collection inserirDadosTemplate(byte[] arquivoMSExcel, List<String> propriedades) {
        return extrairDadosTemplate(arquivoMSExcel, propriedades, MAX_LINHAS_CONSIDERAR);
    }

    public Collection extrairDadosTemplate(byte[] arquivoMSExcel, List<String> propriedades) {
        return extrairDadosTemplate(arquivoMSExcel, propriedades, MAX_LINHAS_CONSIDERAR);
    }

    public Collection extrairDadosTemplate(List<String> propriedades) {
        try {
            return extrairDadosTemplate(documentoAtual, propriedades, MAX_LINHAS_CONSIDERAR);
        } catch (Exception e) {
            log.error("Falha ao extrair template", e);
            return null;
        }
    }

    public Collection extrairDadosTemplate(byte[] arquivoMSExcel, List<String> propriedades, int maxLinhaConsiderar) {
        try {
            return extrairDadosTemplate(new XSSFWorkbook(new ByteArrayInputStream(arquivoMSExcel)), propriedades, MAX_LINHAS_CONSIDERAR);
        } catch (Exception e) {
            log.error("Falha ao extrair template", e);
            return null;
        }
    }

    public Collection extrairDadosTemplate(XSSFWorkbook arquivoMSExcel,
            List<String> propriedades,
            int maxLinhaConsiderar) {
        List linhas = null;
        if (arquivoMSExcel == null || propriedades == null || propriedades.isEmpty()) {
            return null;
        }
        XSSFWorkbook documentoAtual = arquivoMSExcel;
        XSSFSheet planilhaAtual = documentoAtual.getSheetAt(0);
        Integer linhaPropriedades = null;
        XSSFRow linhaAtual = null;
        List<String> propriedadesUpper = new ArrayList<>();
        indiceColunaPropriedades.clear();

        for (String s : propriedades) {
            if (s != null) {
                propriedadesUpper.add(s.trim().toUpperCase());
            }
        }

        try {
            log.debug("Criando arquivo");
            XSSFCell cell = null;
            linhas = new ArrayList();
            linhaPropriedades = -1;
            log.debug("Procurando inicio dos dados");
            for (Iterator rit = (Iterator) planilhaAtual.rowIterator(); rit.hasNext();) {
                linhaAtual = (XSSFRow) rit.next();
                for (Iterator cit = (Iterator) linhaAtual.cellIterator(); cit.hasNext();) {
                    cell = (XSSFCell) cit.next();
                    CellReference cellRef = new CellReference(linhaAtual.getRowNum(), cell.getColumnIndex());
                    Object valorColuna = getValorCelular(cell);
                    if (valorColuna != null) {
                        String nomeColuna = valorColuna.toString().trim().toUpperCase();
                        if (propriedadesUpper.contains(nomeColuna)) {
                            indiceColunaPropriedades.put(nomeColuna, cell.getColumnIndex());
                            linhaPropriedades = cell.getRowIndex();
                            if (linhaPropriedades < 0) {
                                linhaPropriedades = cell.getRow().getRowNum();
                            }
                            log.debug("Coluna do template encontrada");
                            log.debug(nomeColuna);
                            log.debug(linhaPropriedades);
                        }
                    }
                }
            }

            if (linhaPropriedades >= 0) {
                boolean parada = false;
                for (int i = linhaPropriedades + 1; !parada && i < maxLinhaConsiderar; i++) {
                    linhaAtual = planilhaAtual.getRow(i);
                    //Object[] linha = new Object[propriedades.size()];
                    List linha = new ArrayList(propriedades.size());
                    int j = 0;
                    if (linhaAtual != null) {
                        try {
                            for (String prop : propriedadesUpper) {
                                Integer col = indiceColunaPropriedades.get(prop);
                                Object valorCell = null;
                                if (col != null) {
                                    cell = linhaAtual.getCell(col);
                                    valorCell = getValorCelular(cell);
                                }
                                //linha[j++] = valorCell;
                                linha.add(valorCell);
                            }
                            linhas.add(linha);
                        } catch (Exception e) {
                            parada = true;
                            log.debug("Parando de procurar dados", e);
                        }
                    }
                }
            } else {
                log.debug("Nenhuma propriedade foi encontrado no arquivo");
            }

        } catch (Exception e) {
            throw new RuntimeException("Falha no arquivo", e);
        } finally {
            try {
                documentoAtual.close();
                documentoAtual = null;
                planilhaAtual = null;
                linhaAtual = null;
            } catch (IOException ex) {
                logutil.error("", ex);
            }
        }
        return linhas;
    }

    public static final synchronized Object getValorCelular(XSSFCell cell) {
        Object valorColuna = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case STRING:
                    valorColuna = cell.getRichStringCellValue();
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        valorColuna = cell.getDateCellValue();
                    } else {
                        valorColuna = cell.getRawValue();
                    }
                    break;
                case BOOLEAN:
                    valorColuna = cell.getBooleanCellValue();
                    break;
                case FORMULA:
//                    valorColuna = cell.getCellFormula();
                    valorColuna = cell.getRichStringCellValue();
                    break;
                default:
                    valorColuna = cell.getRawValue();
                    break;
            }
        }
        return valorColuna;
    }

    public byte[] aplicarTemplate(byte[] template, Map<String, String> propsSimples,
            Map<String, String> propsBean, Collection colecao) throws IOException {
        byte[] arquivo;
        XSSFWorkbook wb = null;
        XSSFRow row;
        XSSFCell cell = null;

        wb = new XSSFWorkbook(new ByteArrayInputStream(template));
        CellStyle cellStyle = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("d/m/yy"));
        XSSFSheet sheet = wb.getSheetAt(0);

        if (linhaPropriedades == null || linhaPropriedades < 0) {
            linhaPropriedades = indentificarLinhaPropriedades(sheet, propsSimples, propsBean, propriedadesEncontradas);
        }
        log.debug("Linha Indice das Propriedades: " + linhaPropriedades);

        if (linhaPropriedades != null && linhaPropriedades > 0 && colecao != null && !colecao.isEmpty()) {
            this.documentoAtual = wb;
            this.planilhaAtual = sheet;
            aplicarTemplate(colecao, propsBean);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        baos.flush();
        arquivo = baos.toByteArray();
        return arquivo;
    }

    public void aplicarTemplate(Collection colecao, Map<String, String> propsBean) {
        for (Object value : colecao) {
            aplicarTemplate(value, propsBean);
        }
    }

    public void aplicarTemplate(Object value, Map<String, String> propsBean) {
        XSSFRow row;
        XSSFCell cell = null;
        row = planilhaAtual.createRow(linhaPropriedades++);
        Object val = null;
        for (Map.Entry<String, XSSFCell> prop : propriedadesEncontradas.entrySet()) {
            String key = prop.getKey();
            String caminhoProp = propsBean.get(key);
            int columnIndex = prop.getValue().getColumnIndex();
            try {
                cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                val = PropertyUtils.getProperty(value, caminhoProp);
                if (val == null) {
                    cell.setCellValue("");
                } else if (val instanceof Date) {
                    cell.setCellValue((Date) val);
                    cell.setCellStyle(getStyleDatePlanilhaAtual());
                } else if (val instanceof Double) {
                    cell.setCellValue((Double) val);
                    //TODO: Setar date style
                } else if (val instanceof Float) {
                    cell.setCellValue((Float) val);
                } //TODO: criar para o Money
                else {
                    cell.setCellValue(val.toString());

                }
            } catch (org.apache.commons.beanutils.NestedNullException e) {
                if (cell != null) {
                    cell.setCellValue(VALOR_DEFAULT);
                }
                log.trace("Valor nulo");
            } catch (Exception e) {
                if (cell != null) {
                    cell.setCellValue(VALOR_DEFAULT);
                }
                log.debug("Falha ao aplicar template", e);
            }
        }
    }

    public byte[] gerarRelatorioByte(ReportTemplate relatorio, Map param, Collection colecao) {
        byte[] arquivo = null;
        try {
            arquivo = aplicarTemplate(relatorio.getRelatorio(), null, param, colecao);
        } catch (IOException ex) {
            //log.error("Erro ao gerar relatorio", ex);
            throw new RuntimeException(ex);
        }
        return arquivo;
    }

    public void aplicarDescritor(DescriptorLayoutImportFile descritorArquivo) {
        if (descritorArquivo == null) {
            return;
        }
        IDescrpitorFieldLayout[] descritorCampos = descritorArquivo.getDescritorCampos();
        CellReference cellRef = localizarValor(planilhaAtual, MARCADOR_NOME_LAYOUT);
        if (cellRef == null || descritorCampos == null) {
            return;
        }
        XSSFRow linhaProp = planilhaAtual.getRow(cellRef.getRow());
        linhaProp.getCell(cellRef.getCol() + 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(descritorArquivo.getCodigoLayout());
        linhaProp.getCell(cellRef.getCol() + 3, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(descritorArquivo.getNomeLayout());
        cellRef = localizarValor(planilhaAtual, MARCADOR_INICIO_TEMPLATE);
        if (cellRef == null) {
            return;
        }
        int linha = cellRef.getRow();
        int coluna = cellRef.getCol();
        XSSFRow linhaGrupo = planilhaAtual.getRow(linha);
        XSSFRow linhaColuna = planilhaAtual.getRow(linha + 1);
        XSSFRow linhaPropriedade = planilhaAtual.getRow(linha + 2);
        this.linhaPropriedades = linhaPropriedade.getRowNum();

        XSSFCellStyle cellStyleGrupo = null;
        XSSFCellStyle cellStyleColuna = null;
        XSSFCellStyle cellStyleProp = null;
        String grupoAnt = null;
        Integer colIniGrupoAnt = null;
        int idxlinhaGrupo = linhaGrupo.getRowNum();
        for (int i = 0; i < descritorCampos.length; i++) {
            IDescrpitorFieldLayout desc = descritorCampos[i];
            int colunaAtual = coluna + i;
            XSSFCell cellGrupo = linhaGrupo.getCell(colunaAtual, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            XSSFCell cellColuna = linhaColuna.getCell(colunaAtual, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            XSSFCell cellProp = linhaPropriedade.getCell(colunaAtual, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String grupo = desc.getGrupo();
            cellGrupo.setCellValue(grupo);
            cellColuna.setCellValue(desc.getNomeColuna());
            cellProp.setCellValue(desc.getPropriedade());

            this.propriedadesEncontradas.put(desc.getPropriedade(), cellProp);

            if (cellStyleGrupo == null) {
                cellStyleGrupo = cellGrupo.getCellStyle();
            } else {
                cellGrupo.setCellStyle(cellStyleGrupo);
            }
            if (cellStyleColuna == null) {
                cellStyleColuna = cellColuna.getCellStyle();
            } else {
                cellColuna.setCellStyle(cellStyleColuna);
            }
            if (cellStyleProp == null) {
                cellStyleProp = cellProp.getCellStyle();
            } else {
                cellProp.setCellStyle(cellStyleProp);
            }
            if (colIniGrupoAnt == null) {
                colIniGrupoAnt = colunaAtual;
            }
            if ((!grupo.equals(grupoAnt) && colunaAtual - colIniGrupoAnt > 1) || i == descritorCampos.length - 1) {
                int indiceInicio = colIniGrupoAnt;
                int indiceFim = colunaAtual;
                if (i != descritorCampos.length - 1) {
                    indiceFim--;
                }
                CellRangeAddress cellRangeAddress = new CellRangeAddress(idxlinhaGrupo, idxlinhaGrupo, indiceInicio, indiceFim);
                planilhaAtual.addMergedRegion(cellRangeAddress);
                colIniGrupoAnt = colunaAtual;
            }
            grupoAnt = grupo;
            if (colunaAtual > 2) {
                planilhaAtual.autoSizeColumn(colunaAtual);
            }
        }
    }

    public void aplicarTemplate(IEntity tmp, DescriptorLayoutImportFile descritorArquivo) {
        aplicarTemplate(tmp, descritorArquivo.getMapPropriedadesBean());
    }

    public InputStream toStream() throws IOException {
        InputStream in = null;
        File tempFile = File.createTempFile(RandomStringUtils.randomAlphabetic(9), TMP_DEFAULT_SUFIX);
        FileOutputStream temp = new FileOutputStream(tempFile);
        this.documentoAtual.write(temp);
        temp.flush();
        in = new FileInputStream(tempFile);
        temp.close();
        return in;
    }

    private CellReference localizarValor(XSSFSheet planilha, String marcador) {
        CellReference ret = null;
        if (planilha == null && marcador == null) {
            return null;
        }
        for (Iterator rit = (Iterator) planilha.rowIterator(); rit.hasNext();) {
            XSSFRow row = (XSSFRow) rit.next();
            XSSFCell cell = null;
            for (Iterator cit = (Iterator) row.cellIterator(); cit.hasNext();) {
                cell = (XSSFCell) cit.next();
                Object valorColuna = getValorCelular(cell);
                if (valorColuna != null) {
                    String nomePropriedade = valorColuna.toString().toUpperCase();
                    if (nomePropriedade.equals(marcador)) {
                        ret = new CellReference(cell.getRowIndex(), cell.getColumnIndex());
                        return ret;
                    }
                }
            }
        }
        return ret;
    }

    private Integer indentificarLinhaPropriedades(XSSFSheet planilha, Map<String, String> propsSimples, Map<String, String> propsBean, Map<String, XSSFCell> propriedadesEncontradas) {
        Integer ret = null;
        if (planilha == null) {
            return null;
        }
        for (Iterator rit = (Iterator) planilha.rowIterator(); rit.hasNext();) {
            XSSFRow row = (XSSFRow) rit.next();
            for (Iterator cit = (Iterator) row.cellIterator(); cit.hasNext();) {
                XSSFCell cell = (XSSFCell) cit.next();
                CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
                Object valorColuna = getValorCelular(cell);
                if (valorColuna != null) {
                    String nomePropriedade = valorColuna.toString();

                    if (propsSimples != null && propsSimples.containsKey(nomePropriedade)) {
                        Object val = propsSimples.get(nomePropriedade);
                        if (cell != null) {
                            cell.setCellValue(val.toString());
                        }
                    }
                    if (propsBean != null && propsBean.containsKey(nomePropriedade)) {
                        ret = cell.getRowIndex();
                        if (ret < 0) {
                            ret = cell.getRow().getRowNum();
                        }
                        propriedadesEncontradas.put(nomePropriedade, cell);
                    }
                }
            }
        }
        return ret;
    }

    private CellStyle getStyleDatePlanilhaAtual() {
        if (cellStyleDatePlanilhaAtuall == null) {
            cellStyleDatePlanilhaAtuall = planilhaAtual.getWorkbook().createCellStyle();
            CreationHelper createHelper = planilhaAtual.getWorkbook().getCreationHelper();
            cellStyleDatePlanilhaAtuall.setDataFormat(createHelper.createDataFormat().getFormat("d/m/yy"));
        }
        return cellStyleDatePlanilhaAtuall;
    }

    @Override
    public InputStream generate(GenericDataReport datareport) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, Object> extrairDados(BinaryFile arquivoBinario, List<String> propriedades) {
        try {
            return extrairDados(
                    new XSSFWorkbook(new ByteArrayInputStream(arquivoBinario.getFileBinary())),
                    propriedades, MAX_LINHAS_CONSIDERAR);
        } catch (IOException ex) {
            log.error("Fail on extractData", ex);
            return null;
        }
    }

    public Map<String, Object> extrairDados(XSSFWorkbook arquivoMSExcel, List<String> propriedades, int MAX_LINHAS_CONSIDERAR) {
        Map<String, Object> linhas = new HashMap<>();
        if (arquivoMSExcel == null || propriedades == null || propriedades.isEmpty()) {
            return null;
        }
        XSSFWorkbook documentoAtual = arquivoMSExcel;
        XSSFSheet planilhaAtual = documentoAtual.getSheetAt(0);
        Integer linhaPropriedades = null;
        XSSFRow linhaAtual = null;
        List<String> propriedadesUpper = new ArrayList<>();
        indiceColunaPropriedades.clear();

        try {
            log.debug("Criando arquivo");
            XSSFCell cell = null;
            log.debug("Procurando inicio dos dados");

            for (String s : propriedades) {
                if (s != null) {
                    propriedadesUpper.add(s.trim().toUpperCase());
                }
            }

            for (Iterator rit = (Iterator) planilhaAtual.rowIterator(); rit.hasNext();) {
                XSSFRow row = (XSSFRow) rit.next();
                for (Iterator cit = (Iterator) row.cellIterator(); cit.hasNext();) {
                    cell = (XSSFCell) cit.next();
                    Object valorColuna = getValorCelular(cell);
                    if (valorColuna != null) {
                        String nomePropriedade = valorColuna.toString().toUpperCase();
                        for (String prop : propriedadesUpper) {
                            if (nomePropriedade.equals(prop)) {
                                CellReference cellRef = localizarValor(planilhaAtual, prop);
                                XSSFCell cell1 = row.getCell(cellRef.getCol() + 1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                linhas.put(prop, getValorCelular(cell1));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha no arquivo", e);
        } finally {
            try {
                documentoAtual.close();
                documentoAtual = null;
                planilhaAtual = null;
                linhaAtual = null;
            } catch (IOException ex) {
            }
        }
        return linhas;
    }

    public Collection extrairDadosTemplate(BinaryFile arquivoMSExcel, List<String> propriedades) {
        //TODO: Improve this, update to InputStream
        return extrairDadosTemplate(arquivoMSExcel.getFileBinary(), propriedades, MAX_LINHAS_CONSIDERAR);
    }
}
