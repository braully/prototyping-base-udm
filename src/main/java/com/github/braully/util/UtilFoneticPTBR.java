package com.github.braully.util;

/**
 * Busca fonetica encontrada em:
 * https://github.com/giullianomorroni/SearchEngineMongo/blob/master/SearchEngineMongo/src/main/java/br/com/motorbusca/modelo/Fonetica.java
 * Aprimorado com artigo:
 * http://www.brunoportfolio.com/arquivos/pdf/BuscaBR_Fonetica.pdf
 * http://sourceforge.net/p/metaphoneptbr/code/ci/master/tree/
 * <p>
 * A busca por fonética consiste em:
 * <p>
 * - converter todas as letras para minúsculo - transformar letras duplas em
 * unidades - converter consoantes com o mesmo fonema -
 * remover acentuações - remover caracteres especiais - corrigir erro comuns da
 * lingua (lingua portuguesa)
 * <p>
 * Ainda assim esta funcionalidade precisa ser melhorada, pois pode causar
 * fonemas inválidos para algumas palavras. Autor: giulliano
 */
public class UtilFoneticPTBR {

    //FIXME Aperfeiçoar esta funcionalidade...
    public static synchronized String createFonema(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return valor;
        }

        //converter todas as letras para minisculo
        valor = valor.toLowerCase();

        //transformar letras duplas em unidades
        valor = valor.replace("aa", "a");
        valor = valor.replace("ee", "e");
        valor = valor.replace("ii", "i");
        valor = valor.replace("oo", "o");
        valor = valor.replace("uu", "u");
        valor = valor.replace("pp", "p");
        valor = valor.replace("rr", "r");
        valor = valor.replace("tt", "t");
        valor = valor.replace("ll", "l");
        valor = valor.replace("vv", "v");
        valor = valor.replace("bb", "b");
        valor = valor.replace("mm", "m");
        valor = valor.replace("nn", "n");
        valor = valor.replace("zz", "z");
        valor = valor.replace("xx", "x");
        valor = valor.replace("cc", "c");
        valor = valor.replace("kk", "k");
        valor = valor.replace("jj", "j");
        valor = valor.replace("gg", "g");
        valor = valor.replace("ff", "f");
        valor = valor.replace("dd", "d");
        valor = valor.replace("yy", "y");
        valor = valor.replace("ww", "w");

        //remover acentuações
        valor = valor.replace("ã", "a");
        valor = valor.replace("á", "a");
        valor = valor.replace("à", "a");
        valor = valor.replace("â", "a");
        valor = valor.replace("ẽ", "e");
        valor = valor.replace("é", "e");
        valor = valor.replace("è", "e");
        valor = valor.replace("ê", "e");
        valor = valor.replace("ĩ", "i");
        valor = valor.replace("í", "i");
        valor = valor.replace("ì", "i");
        valor = valor.replace("î", "i");
        valor = valor.replace("õ", "o");
        valor = valor.replace("ó", "o");
        valor = valor.replace("ò", "o");
        valor = valor.replace("ô", "o");
        valor = valor.replace("ũ", "u");
        valor = valor.replace("ú", "u");
        valor = valor.replace("ù", "u");
        valor = valor.replace("û", "u");

        //converter consoantes com o mesmo fonema (ss e ç)
        valor = valor.replace("ss", "s");
        valor = valor.replace("ç", "s");
        valor = valor.replace("z", "s");
        valor = valor.replace("x", "s");
        valor = valor.replace("k", "c");
        valor = valor.replace("y", "i");
        valor = valor.replace("w", "v");

        //converter consoantes com o mesmo fonema h
        valor = valor.replace("sch", "x");
        valor = valor.replace("lh", "l");
        valor = valor.replace("sh", "x");
        valor = valor.replace("nh", "n");
        valor = valor.replace("ch", "x");

        //converter consoantes com fonems proximos
        valor = valor.replace("ex", "x");
        valor = valor.replace("pr", "p");
        valor = valor.replace("g", "j");

        //caracteres especiais
        valor = valor.replace("-", "");
        valor = valor.replace("@", "");
        valor = valor.replace("!", "");
        valor = valor.replace("$", "");
        valor = valor.replace("%", "");
        valor = valor.replace("&", "");
        valor = valor.replace("*", "");
        valor = valor.replace("+", "");
        valor = valor.replace("-", "");
        valor = valor.replace(".", "");
        valor = valor.replace(",", "");
        valor = valor.replace("|", "");
        valor = valor.replace("/", "");
        valor = valor.replace(">", "");
        valor = valor.replace("<", "");
        valor = valor.replace(":", "");
        valor = valor.replace(";", "");
        valor = valor.replace("?", "");
        valor = valor.replace("\\", "");
        valor = valor.replace("{", "");
        valor = valor.replace("}", "");
        valor = valor.replace("[", "");
        valor = valor.replace("]", "");
        valor = valor.replace("(", "");
        valor = valor.replace(")", "");
        valor = valor.replace("=", "");
        valor = valor.replace("'", "");
        valor = valor.replace("\"", "");

        //remover o h
        valor = valor.replace("h", "");

        //corrigir erro comuns da lingua
        valor = valor.replace("np", "mp");
        valor = valor.replace("nb", "mb");

        return valor;
    }

    /*
    
     LETRAS 	-> REPRESENTAÇÃO FONÉTICA (COMENTARIOS)
     ------------------------------------------
     ^v	-> v (ou seja, copia a vogal)
     B   	-> B
     C[AOU]  -> K
     Cc  	-> K
     C[EI]  	-> S
     CHR	-> K
     CH	-> X (regra mais genérica aplica-se por último)
     C$	-> K
     Ç	-> S
     D   	-> D
     F	-> F
     G[AOU]	-> G
     G[EI]	-> J
     GH[EI]	-> J
     GHc	-> G
     ^Hv	-> v
     H	-> 0 (Outros casos de aparição de H devem ser ignorados)
     J	-> J
     K	-> K
     LH	-> 1 (não tem representacao no metaphone original)
     Lv	-> L
     M	-> M
     N$	-> M
     NH	-> 3 (não tem representação no metaphone original)
     P	-> P
     PH	-> F
     Q	-> K
     ^R	-> 2 (não tem representação no metaphone original)
     R$	-> 2 (não tem representação no metaphone original)
     RR	-> 2
     vRv	-> R
     .Rc	-> R
     cRv	-> R
     SS	-> S
     SH	-> X
     SC[EI]	-> S
     SC[AUO]	-> SK (sim - duas letras porque tem sons distintos quando acompanhadas de U ou O).
     SCH	-> X
     Sc	-> S
     S	-> S (caso as outras regras não se apliquem, esta entra em vigor)
     T	-> T
     TH	-> T
     V	-> V
     Wv	-> V
     Wc	-> 0
     X$	-> X (o mais adequado seria KS, mas há de se considerar alguns nomes separados por espaços, tornando assim X uma representação mais interessante, embora não perfeitamente acurada)
     ^EXv	-> Z 
     .EX[EI]	-> X 
     .EX[AOU]-> X 
     EX[EI]	-> X 
     EX[AOU]	-> KS
     EX[PTC]	-> S
     EX.	-> KS
     [vCKGLRX][AIOU]X	-> X
     [DFMNPQSTVZ][AIOU]X	-> KS
     X	-> X (caso nenhuma caso acima case com a palavra, aplica-se esta regra)
     Y	-> I (é tratado como vogal para todos os efeitos)
     Z$	-> S
     Z	-> Z
     */

    public static String capitalize(String str) {
        return capitalize(str, null);
    }

    public static String capitalize(String str, char[] delimiters) {
        int delimLen = (delimiters == null ? -1 : delimiters.length);
        if (str == null || str.length() == 0 || delimLen == 0) {
            return str;
        }
        int strLen = str.length();
        StringBuffer buffer = new StringBuffer(strLen);
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);
            if (isDelimiter(ch, delimiters)) {
                buffer.append(ch);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                buffer.append(ch);
            }
        }
        replaceAll(buffer, "De ", "de ");
        replaceAll(buffer, "Do ", "do ");
        replaceAll(buffer, "Da ", "da ");
        replaceAll(buffer, " E ", " e ");
        String strf = buffer.toString();
        return strf;
    }

    public static void replaceAll(StringBuffer builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length();
            index = builder.indexOf(from, index);
        }
    }

    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (int i = 0, isize = delimiters.length; i < isize; i++) {
            if (ch == delimiters[i]) {
                return true;
            }
        }
        return false;
    }
}
