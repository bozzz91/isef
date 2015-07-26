package ru.desu.home.isef.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {

    public static String formatDate(Date date) {
        final SimpleDateFormat nf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        return nf.format(date);
    }
    
    public static String formatDouble(double stock) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat aDF = new DecimalFormat("#0.000", otherSymbols);
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
