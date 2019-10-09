package com.github.braully.app;

import com.github.braully.boleto.BoletoFacade;
import java.io.File;
import org.jrimum.bopepo.view.BoletoViewer;
import org.junit.Test;

/**
 *
 * @author Braully Rocha da Silva
 */
public class FinancialBilletTicketTest {

    @Test
    public void tetBilletPdf() {
        BoletoFacade boletoFacade = new BoletoFacade();
        boletoFacade.sacado("Sacado da Silva Sauro").sacadoCpf("0");
        boletoFacade.cedente("Cedente da Silva Sauro").cedenteCnpj("0");
        boletoFacade.banco("1").agencia("1").conta("1-1").carteira("1");
        boletoFacade.numeroDocumento("1")
                .nossoNumero("12345678901")
                .dataVencimento("01/01/2019").valor(100.23);
        BoletoViewer create = BoletoViewer.create(boletoFacade);
        File pdfAsFile = create.getPdfAsFile("/dados/tmp/teste.pdf");
    }
}
