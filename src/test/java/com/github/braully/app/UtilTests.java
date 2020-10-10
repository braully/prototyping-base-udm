/*
 Copyright 2109 Braully Rocha

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 
 */
package com.github.braully.app;

import com.github.braully.domain.AccountTransaction;
import com.github.braully.domain.Money;
import com.github.braully.util.UtilExpression;
import com.github.braully.util.UtilString;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.springframework.security.access.vote.AffirmativeBased;

/**
 *
 * @author braully
 */
public class UtilTests {

    @Ignore
    @Test
    public void testSplitBeanMethod() {
        com.sun.faces.config.ConfigureListener c = null;
        String name = "Bean.method";
        String[] split = name.split("\\.");
        AffirmativeBased a = null;
        assertTrue(split.length == 2);
    }

    @Test
    public void testUtilString() {
        String ref = "string de teste (valor esperado)";
        String extract = UtilString.extract(ref, "(", ")");
        assertEquals(extract, "valor esperado");
    }

    @Ignore
    @Test
    public void testUtilExpression() {
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setDatePrevist(new Date());
        accountTransaction.setCreditTotal(new Money(10000).getValorBig());
        Map<String, Object> propsExtra = new HashMap();
        //

        propsExtra.put("desconto", 10);
        String expCond = "desconto > 0";
        Object cond = UtilExpression.executarExpressao(accountTransaction, propsExtra, expCond);
        System.out.println(cond);

        propsExtra.put("valor", accountTransaction.getCreditTotal());
        propsExtra.put("descontoextra", 20);
        String expValor = "DINHEIRO(valor - (desconto + descontoextra))";
        Object valor = UtilExpression.executarExpressao(accountTransaction, propsExtra, expValor);
        System.out.println(valor);

        propsExtra.put("valorstr", "R$ 10,00");
        expValor = "DINHEIRO(valorstr)";
        valor = UtilExpression.executarExpressao(accountTransaction, propsExtra, expValor);
        System.out.println(valor);

        propsExtra.put("vencimento", new Date());
        String expData = "DATA(ANO(vencimento), MES(vencimento), 10)";
        Object data = UtilExpression.executarExpressao(accountTransaction, propsExtra, expData);
        System.out.println(data);
    }
}
