/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.braully.util;

/**
 *
 * @author strike
 */
//TODO: Refatorar e simplificar
public class UtilMath {

    public static String calcularDVModulo11Sicoob(String numero) {
        int dv = 0;
        int[] values = new int[numero.length()];
        for (int i = 0; i < numero.length(); i++) {
            values[i] = Integer.parseInt(numero.charAt(i) + "");
        }
        int soma = 0;
        int vetpos = numero.length() - 1;
        while (vetpos >= 0) {
            for (int i = 2; i < 10; i++) {
                soma += values[vetpos] * i;
                vetpos--;
                if (vetpos < 0) {
                    break;
                }
            }
        }

        if (soma < 11) {
            dv = soma - 11;
        } else {
            int resto = soma % 11;
            dv = 11 - resto;
        }

        if (dv > 9) {
            dv = 0;
        }
        return "" + Math.abs(dv);
    }

    public static String calculeDVModulo11(String numero) {
        int dv = 0;
        int[] values = new int[numero.length()];
        for (int i = 0; i < numero.length(); i++) {
            values[i] = Integer.parseInt(numero.charAt(i) + "");
        }
        int soma = 0;
        int vetpos = numero.length() - 1;
        while (vetpos >= 0) {
            for (int i = 2; i < 10; i++) {
                soma += values[vetpos] * i;
                vetpos--;
                if (vetpos < 0) {
                    break;
                }
            }
        }

        if (soma < 11) {
            dv = soma - 11;
        } else {
            int resto = soma % 11;
            dv = 11 - resto;
        }

        if (dv > 9) {
            dv = 0;
        }
        return "" + Math.abs(dv);
    }

    public static int getMod11(String num, int base, int r) {
        /**
         * Autor: Douglas Tybel <dty...@yahoo.com.br>
         *
         * Função: Calculo do Modulo 11 para geracao do digito verificador de
         * boletos bancarios conforme documentos obtidos da Febraban -
         * www.febraban.org.br
         *
         * Entrada: $num: string numérica para a qual se deseja calcularo digito
         * verificador; $base: valor maximo de multiplicacao [2-$base] $r:
         * quando especificado um devolve somente o resto
         *
         * Saída: Retorna o Digito verificador.
         *
         * Observações: - Script desenvolvido sem nenhum reaproveitamento de
         * código existente. - Script original de Pablo Costa
         * <pa...@users.sourceforge.net>
         * - Transportado de php para java - Exemplo de uso:
         * getMod11(nossoNumero, 9,1) - 9 e 1 são fixos de acordo com a base -
         * Assume-se que a verificação do formato das variáveis de entrada é
         * feita antes da execução deste script.
         */
        base = 9;
        r = 0;

        int soma = 0;
        int fator = 2;
        String[] numeros, parcial;
        numeros = new String[num.length() + 1];
        parcial = new String[num.length() + 1];

        /* Separacao dos numeros */
        for (int i = num.length(); i > 0; i--) {
            // pega cada numero isoladamente 
            numeros[i] = num.substring(i - 1, i);
            // Efetua multiplicacao do numero pelo falor 
            parcial[i] = String.valueOf(Integer.parseInt(numeros[i])
                    * fator);
            // Soma dos digitos 
            soma += Integer.parseInt(parcial[i]);
            if (fator == base) {
                // restaura fator de multiplicacao para 2 
                fator = 1;
            }
            fator++;

        }

        /* Calculo do modulo 11 */
        if (r == 0) {
            soma *= 10;
            int digito = soma % 11;
            if (digito == 10) {
                digito = 0;
            }
            return digito;
        } else {
            int resto = soma % 11;
            return resto;
        }
    }
}
