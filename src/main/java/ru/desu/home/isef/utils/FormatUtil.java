package ru.desu.home.isef.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {

    public static String formatDate(Date date) {
        final SimpleDateFormat nf = new SimpleDateFormat("dd-MM-yyyy hh-mm");
        return nf.format(date);
    }
    
    public static String formatDouble(double stock) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat aDF = new DecimalFormat("#.0", otherSymbols);
        return aDF.format(stock);
    }
}
