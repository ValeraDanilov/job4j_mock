package ru.checkdev.notification.service;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MailGun {

    public static void main(String[] args) {
        try {
            DateFormatSymbols russianDateFormatSymbols = new DateFormatSymbols(new Locale("ru"));
            russianDateFormatSymbols.setMonths(new String[]{"янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек"});

            SimpleDateFormat format = new SimpleDateFormat("d MMM y, HH:mm", new Locale("ru"));
            format.setDateFormatSymbols(russianDateFormatSymbols);

            System.out.println(format.parse("8 авг 18, 05:15"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
