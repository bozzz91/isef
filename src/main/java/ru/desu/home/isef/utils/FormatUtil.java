package ru.desu.home.isef.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {

	private FormatUtil() {}

    public static String formatDate(Date date) {
        final SimpleDateFormat nf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        return nf.format(date);
    }

	public static String formatDouble(double stock) {
		return formatDouble(stock, 3);
	}

	public static String formatDouble(double stock, int precision) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
		otherSymbols.setDecimalSeparator('.');
		String format = "#0.";
		for (int i=0; i< precision; i++) {
			format += "0";
		}
		DecimalFormat aDF = new DecimalFormat(format, otherSymbols);
		return aDF.format(stock);
	}
    
    public static double roundDouble(double stock) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat aDF = new DecimalFormat("#0.00", otherSymbols);
        return Double.parseDouble(aDF.format(stock));
    }
    
    public static String formatStringMultiline(String str) {
        String res = "";
        int row = 0;
        for (String s : str.split("\n")) {
            res += s.length() > 17 ? s.substring(0, 17)+"..." : s;
            if (++row > 1)
                break;
            res += "\n";
        }
        if (row > 1 && !res.endsWith("...")) {
            res += "...";
        }
        return res;
    }
}
