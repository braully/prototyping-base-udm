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
package com.github.braully.domain.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Currency;
import javax.persistence.Column;
import com.github.braully.constant.LocalizedConfiguration;

/**
 * Façace to Money
 *
 * @author braully
 */
public class MoneyTO implements Comparable<MoneyTO> {

    private static final long FATOR_UNIDADE = 100;
    /*
     * 
     */
    @Column(name = "valor")
    private long valor;
//    @Column(name = "moeda", length = 3)
//    private Currency moeda;

    public MoneyTO() {
        super();
//        moeda = br.com.braully.dominio.ConfiguracoesRegionais.MOEDA;
    }

    public MoneyTO(BigDecimal valorBig) {
        this();
        if (valorBig != null) {
            valor = valorBig.unscaledValue().longValue();
        }
    }

    public MoneyTO(Double d) {
        this();
        if (d != null) {
            int tmp = (int) (d * FATOR_UNIDADE);
            this.valor = tmp;
        }
    }

    public MoneyTO(Long valor) {
        this();
        if (valor != null) {
            this.valor = valor;
        }
    }

    public MoneyTO(long valor) {
        this();
        this.valor = valor;
    }

    public MoneyTO(long valor, Currency moeda) {
        this(moeda, valor);
    }

    public MoneyTO(Currency moeda, long valor) {
        this();
        this.valor = valor;
    }

    public MoneyTO(String arg2) {
        this();
        this.parser(arg2);
    }

    public long getValor() {
        return valor;
    }

    public void setValor(long valor) {
        this.valor = valor;
    }

    public Currency getMoeda() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (valor ^ (valor >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        MoneyTO other = (MoneyTO) obj;
        if (valor != other.valor) {
            return false;
        }
        return true;
    }

    public MoneyTO adiciona(MoneyTO preco) {
        if (preco == null) {
            return this;
        }
        return new MoneyTO(preco.valor + this.valor);
    }

    public MoneyTO subtrai(MoneyTO preco) {
        MoneyTO res = this;
        if (preco != null) {
            res = new MoneyTO(this.valor - preco.valor);
        }
        return res;
    }

    public String getDescricaoFormatada() {
        return formatar();
    }

    public String toString() {
        return this.formatar();
    }

    public String formatar() {
        StringBuilder sb = new StringBuilder();
        if (this.getMoeda() != null) {
            sb.append(this.getMoeda().getSymbol());
            sb.append(" ");
        }
        sb.append(formatarValor());
        return sb.toString();
    }

    public String formatarValor() {
//        StringBuilder sb = new StringBuilder();
//        long unidade = this.getUnidade();
//        sb.append(unidade);
//        sb.append(",");
//        long centavos = this.getSubUnidade();
//        if (centavos >= 0 && centavos < 10) {
//            sb.append(0);
//        }
//        if (centavos < 0 && unidade != 0) {
//            centavos = Math.abs(centavos);
//        }
//        sb.append(centavos);
//        return sb.toString();
        return formatar(valor);
    }

    //TODO: Alterar todos formatadores dessa classe para esse metodo
    public static String formatar(long valor) {
        double val = ((double) valor / FATOR_UNIDADE);
        return LocalizedConfiguration.CURRENCY_FORMAT.format(val);
    }

    public static final Number parserNumber(String valor) throws ParseException {
        return LocalizedConfiguration.CURRENCY_FORMAT.parse(valor);
    }

    public final void parser(String arg2) {
        this.valor = this.parseValorFormatado(arg2);
    }

    public long getUnidade() {
        return this.valor / FATOR_UNIDADE;
    }

    public long getSubUnidade() {
        return this.valor % FATOR_UNIDADE;
    }

    public MoneyTO multiplica(long escalar) {
        MoneyTO res = this;
        res = new MoneyTO(this.valor * escalar);
        return res;
    }

    public MoneyTO porcentagemFator(long l) {
        return this.porcentagem(((double) l / FATOR_UNIDADE));
    }

    public MoneyTO porcentagem(long l) {
        return this.porcentagem((double) l);
    }

    public MoneyTO porcentagem(double descontoPontualidade) {
        MoneyTO res = this;
        res = new MoneyTO(Math.round((double) this.valor
                * (double) ((double) descontoPontualidade / (double) 100)));
        return res;
    }

//    public static void main(String[] args) {
//        System.out.println(new MoneyTo("R$ 10,00"));
//        System.out.println(new MoneyTo("R$ 10"));
//        System.out.println(new MoneyTo("10"));
//        System.out.println(new MoneyTo("10,0"));
//    }
    @Override
    public int compareTo(MoneyTO t) {
        int ret = 0;
        if (t != null) {
            ret = (int) (this.valor - t.valor);
        }
        return ret;
    }

    public Double getValorDouble() {
        return this.getValorDouble(valor);
    }

    public Double getValorDouble(long val) {
        return ((double) val / FATOR_UNIDADE);
    }

    private long parseValorFormatado(String arg2) {
        String valorFormatado = arg2;
        String unidade = "0";
        String fracao = "0";
        long valor;

        int parcial = valorFormatado.indexOf(" ");
        if (parcial > 0 && parcial < valorFormatado.length()) {
            valorFormatado = valorFormatado.substring(parcial + 1);
        }

        parcial = valorFormatado.indexOf(",");
        if (parcial < 0) {
            parcial = valorFormatado.indexOf(".");
        }
        if (parcial > 0) {
            unidade = valorFormatado.substring(0, parcial);
            fracao = valorFormatado.substring(parcial + 1, valorFormatado.length());
        } else {
            unidade = unidade + valorFormatado;
        }
        if (fracao.length() <= 1) {
            fracao = fracao + 0;
        } else if (fracao.length() > 2) {
            fracao = fracao.substring(0, 2);
        }
        unidade = unidade.replaceAll("\\D", "");
        valor = Long.parseLong(unidade) * FATOR_UNIDADE;
        valor += Long.parseLong(fracao);
        return valor;
    }

    public MoneyTO subtraiValorAbsoluto(MoneyTO debito) {
        if (debito != null && debito.valor != 0) {
            return new MoneyTO(this.valor - Math.abs(debito.valor));
        }
        return this;
    }

    public MoneyTO adicionaValorAbsoluto(MoneyTO credito) {
        if (credito != null && credito.valor != 0) {
            return new MoneyTO(this.valor + Math.abs(credito.valor));
        }
        return this;
    }

    public MoneyTO divisaoArredondandoTeto(long escalar) {
        MoneyTO res = this;
        long tmpValro = (long) Math.ceil((double) this.valor / escalar);
        res = new MoneyTO(tmpValro);
        return res;
    }

    public MoneyTO[] divide(int quantidadeRestante) {
        if (quantidadeRestante == 0) {
            throw new IllegalArgumentException("Divisão por zero não é permitida");
        }
        MoneyTO[] res = new MoneyTO[quantidadeRestante];
        long tmpValor = this.valor / quantidadeRestante;
        long restante = this.valor % quantidadeRestante;
        for (int i = 0; i < quantidadeRestante - 1; i++) {
            res[i] = new MoneyTO(tmpValor);
        }
        res[quantidadeRestante - 1] = new MoneyTO(tmpValor + restante);
        return res;
    }

    public long getEscala() {
        return FATOR_UNIDADE;
    }

    public BigDecimal getValorBig() {
        BigDecimal bigDecimal = new BigDecimal(valor);
//        bigDecimal = bigDecimal.setScale(2, RoundingMode.DOWN);
        bigDecimal = bigDecimal.movePointLeft(2);
        return bigDecimal;
    }

//    public static void main(String... args) {
//        Money money = new MoneyTo(1000);
//        System.out.println("Money: " + money.formatarValor());
//        System.out.println("BD:" + money.getValorBig());
//        String format = new DecimalFormat("¤ #,##0.00").format(money.getValorBig());
//        System.out.println("DecimalFormat: " + format);
//    }
    public boolean isZero() {
        return valor == 0;
    }
}
