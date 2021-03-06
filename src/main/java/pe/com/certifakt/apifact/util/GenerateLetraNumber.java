package pe.com.certifakt.apifact.util;

import java.util.regex.Pattern;

public class GenerateLetraNumber {


    private static final String[] UNIDADES = {"", "un ", "dos ", "tres ", "cuatro ", "cinco ", "seis ", "siete ", "ocho ", "nueve "};
    private static final String[] DECENAS = {"diez ", "once ", "doce ", "trece ", "catorce ", "quince ", "dieciseis ", "diecisiete ",
            "dieciocho ", "diecinueve", "veinte ", "treinta ", "cuarenta ", "cincuenta ", "sesenta ",
            "setenta ", "ochenta ", "noventa "};
    private static final String[] CENTENAS = {"", "ciento ", "doscientos ", "trescientos ", "cuatrocientos ", "quinientos ", "seiscientos ",
            "setecientos ", "ochocientos ", "novecientos "};

    private GenerateLetraNumber() {
    }

    /**
     * Convierte un valor numérico a una cadena en letras para las facturas
     * @param numero número a convertir
     * @param desMon descripción de la moneda
     * @param mayusculas indicador para retornar el valor en mayusculas (true) o
     * minusculas (false)
     * @return una cadena con la descripción del numero en letras
     */
    public static String Convertir(String numero, String desMon, boolean mayusculas) {
        String letras;
        String parte_decimal;

        if (numero == null) {
            return null;
        }

        /**
         * Si el numero utiliza (.) en lugar de (,) -> se reemplaza*
         */
        numero = numero.replace(".", ",");

        /**
         * Si el numero no tiene parte decimal, se le agrega ,00*
         */
        if (!numero.contains(",")) {
            numero = numero + ",00";
        }

        /**
         * Se valida formato de entrada -> 0,00 y 999 999 999,00*
         */
        if (Pattern.matches("\\d{1,9},\\d{1,2}", numero)) {

            /**
             * Se divide el numero 0000000,00 -> entero y decimal*
             */
            String Num[] = numero.split(",");

            /**
             * Se da formato al numero decimal*
             */
            parte_decimal = Num[1] + "/100 " + desMon + ". ";

            /**
             * Se convierte el numero a letras*
             */
            if (Integer.parseInt(Num[0]) == 0) {//si el valor es cero
                letras = "cero ";
            } else if (Integer.parseInt(Num[0]) > 999999) {//si es millon
                letras = getMillones(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 999) {//si es miles
                letras = getMiles(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 99) {//si es centena
                letras = getCentenas(Num[0]);
            } else if (Integer.parseInt(Num[0]) > 9) {//si es decena
                letras = getDecenas(Num[0]);
            } else {
                /**
                 * sino unidades -> 9*
                 */
                letras = getUnidades(Num[0]);
            }

            /**
             * Devuelve el resultado en mayusculas o minusculas*
             */
            if (mayusculas) {
                return (letras + "CON "+parte_decimal).toUpperCase().trim();

            } else {
                return (letras +"con "+ parte_decimal).trim();
            }

        } else {
            /**
             * Error, no se puede convertir*
             */
            return null;
        }
    }

    /**
     * Funciones para convertir los numeros a Letras*
     */
    private static String getUnidades(String numero) {// 1 - 9
        /**
         * si tuviera algun 0 antes se lo quita -> 09 = 9 o 009=9*
         */
        String num = numero.substring(numero.length() - 1);
        return UNIDADES[Integer.parseInt(num)];
    }

    private static String getDecenas(String num) {// 99
        int n = Integer.parseInt(num);
        if (n < 10) {
            /**
             * para casos como -> 01 - 09*
             */
            return getUnidades(num);
        } else if (n > 19) {
            /**
             * para 20...99*
             */
            String u = getUnidades(num);
            if (u.equals("")) {
                /**
                 * para 20,30,40,50,60,70,80,90*
                 */
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8];

            } else {
                return DECENAS[Integer.parseInt(num.substring(0, 1)) + 8] + "y " + u;
            }
        } else {
            /**
             * numeros entre 11 y 19*
             */
            return DECENAS[n - 10];
        }
    }

    private static String getCentenas(String num) {//999 o 099
        if (Integer.parseInt(num) > 99) {
            /**
             * Es centena*
             */
            if (Integer.parseInt(num) == 100) {
                /**
                 * Caso especial*
                 */
                return " cien ";
            } else {
                return CENTENAS[Integer.parseInt(num.substring(0, 1))] + getDecenas(num.substring(1));
            }

        } else {
            /**
             * Por Ej. 099*
             */
            /**
             * Se quita el 0 antes de convertir a decenas*
             */
            return getDecenas(Integer.parseInt(num) + "");
        }
    }

    private static String getMiles(String numero) {// 999 999
        /**
         * Obtiene las centenas*
         */
        String c = numero.substring(numero.length() - 3);
        /**
         * Obtiene los miles*
         */
        String m = numero.substring(0, numero.length() - 3);
        String n = "";
        /**
         * Se comprueba que miles tenga valor entero*
         */
        if (Integer.parseInt(m) > 0) {
            n = getCentenas(m);
            return n + "mil " + getCentenas(c);
        } else {
            return "" + getCentenas(c);
        }
    }

    private static String getMillones(String numero) { //000 000 000
        /**
         * Se obtiene los miles*
         */
        String miles = numero.substring(numero.length() - 6);
        /**
         * Se obtiene los millones*
         */
        String millon = numero.substring(0, numero.length() - 6);
        String n;
        if (millon.length() > 1) {
            n = getCentenas(millon) + "millones ";
        } else {
            n = getUnidades(millon) + "millon ";
        }
        return n + getMiles(miles);
    }


}
