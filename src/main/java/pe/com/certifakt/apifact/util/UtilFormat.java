/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.com.certifakt.apifact.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilFormat {

    private static DateFormat dFecha = new SimpleDateFormat("dd/MM/yyyy");
    private static DateFormat dHora = new SimpleDateFormat("hh:mm:ss a");
    private static DateFormat dFechaHora = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    private static DecimalFormat dDecimal = new DecimalFormat("0.00");

    public static String fecha(Long date, String... format) {
        if (date != null) {
            if (format != null && format.length > 0 && format[0] != null && !format[0].trim().isEmpty()) {
                dFecha = new SimpleDateFormat(format[0]);
            }
            return dFecha.format(new Date(date));
        } else {
            return "";
        }
    }

    public static String fecha(Date date, String... format) {
        if (date != null) {
            if (format != null && format.length > 0 && format[0] != null && !format[0].trim().isEmpty()) {
                dFecha = new SimpleDateFormat(format[0]);
            }
            return dFecha.format(date);
        } else {
            return "";
        }
    }

    public static String hora(Long date, String... format) {
        if (date != null) {
            if (format != null && format.length > 0 && format[0] != null && !format[0].trim().isEmpty()) {
                dHora = new SimpleDateFormat(format[0]);
            }
            return dHora.format(new Date(date));
        } else {
            return "";
        }
    }

    public static String hora(Date date, String... format) {
        if (date != null) {
            if (format != null && format.length > 0 && format[0] != null && !format[0].trim().isEmpty()) {
                dHora = new SimpleDateFormat(format[0]);
            }
            return dHora.format(date);
        } else {
            return "";
        }
    }

    public static String hora(String fechaHora) {
    	try {
	        if (fechaHora != null) {
	        	dFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 Date fecha = dFecha.parse(fechaHora);
				 dHora = new SimpleDateFormat("hh:mm:ss");
	            return dHora.format(fecha);
	        }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		return null;
    }
    public static String fechaHora(Date date) {
        if (date != null) {
            return dFechaHora.format(date).toLowerCase();
        } else {
            return "";
        }
    }

    public static String format(Date date) {
        if (date != null) {
            return dFechaHora.format(date);
        } else {
            return "";
        }
    }

    public static String format(BigDecimal monto) {
        if (monto != null) {
            return monto.setScale(2, BigDecimal.ROUND_HALF_UP).toString().replace(",", ".");
        } else {
            return "0.00";
        }
    }
    public static String format3(BigDecimal monto) {
        if (monto != null) {
            return (monto.setScale(3, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()).replace(",", ".");
        } else {
            return "0.000";
        }
    }
    public static String format4(BigDecimal monto) {
        if (monto != null) {
            return (monto.setScale(4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()).replace(",", ".");
        } else {
            return "0.0000";
        }
    }
    public static String format7(BigDecimal monto) {
        if (monto != null) {
            return (monto.setScale(7, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()).replace(",", ".");
        } else {
            return "0.0000000";
        }
    }
    public static BigDecimal roundSunat(BigDecimal monto) {
        if (monto != null) {
            return monto.setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }

    public static String concat(String... values) {
        if (values == null) {
            return "";
        } else {
            StringBuilder concat = new StringBuilder();
            for (String value : values) {
                concat = concat.append(value);
            }
            return concat.toString();
        }
    }

    public static Date fechaDate(String date) {

    	Date fecha = null;
    	try {
        	if (date != null) {
    			dFecha = new SimpleDateFormat("yyyy-MM-dd");
    			fecha = dFecha.parse(date);
        	}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return fecha;
    }

    private static DateFormat dFechayyyymmdd = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatoYYYYmmDD(Date date) {
        if (date != null) {
            return dFechayyyymmdd.format(date);
        } else {
            return "";
        }
    }

}
