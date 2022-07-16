package com.zpedroo.voltzmining.utils.formatter;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;

public class NumberFormatter {

    private static NumberFormatter instance;
    public static NumberFormatter getInstance() { return instance; }

    private final BigInteger THOUSAND = BigInteger.valueOf(1000);
    private final NavigableMap<BigInteger, String> FORMATS = new TreeMap<>();
    private final List<String> NAMES = new LinkedList<>();

    public NumberFormatter(FileConfiguration file) {
        instance = this;
        NAMES.addAll(file.getStringList("Number-Formatter"));

        for (int i = 0; i < NAMES.size(); i++) {
            FORMATS.put(THOUSAND.pow(i+1), NAMES.get(i));
        }
    }

    public BigInteger filter(String str) {
        String onlyNumbers = str.replaceAll("[^0-9]+", "");
        if (onlyNumbers.isEmpty()) return BigInteger.ZERO; // invalid amount

        BigInteger number = new BigInteger(onlyNumbers);

        String onlyLetters = str.replaceAll("[^A-Za-z]+", "");

        int i = -1;
        if (NAMES.contains(onlyLetters)) {
            for (String format : NAMES) {
                ++i;

                if (StringUtils.equals(format, onlyLetters)) break;
            }
        }

        if (i != -1) number = number.multiply(THOUSAND.pow(i + 1));

        return number;
    }

    public String format(BigInteger value) {
        Map.Entry<BigInteger, String> entry = FORMATS.floorEntry(value);
        if (entry == null) return value.toString();

        BigInteger key = entry.getKey();
        BigInteger divide = key.divide(THOUSAND);
        BigInteger divide1 = value.divide(divide);
        float f = divide1.floatValue() / 1000f;
        float rounded = ((int)(f * 100))/100f;

        if (rounded % 1 == 0) return ((int) rounded) + "" + entry.getValue();

        return rounded + "" + entry.getValue();
    }

    public String formatDecimal(double number) {
        DecimalFormat formatter = new DecimalFormat("##.##");
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(number);
    }

    public String formatThousand(double number) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(number);
    }

    public String convertToRoman(int number) {
        StringBuilder builder = new StringBuilder();
        if (number < 3999) {

            while (number >= 1) {
                int firstNumber = number / Integer.parseInt("1" + getPaddedString("0", ("" + number).length() - 1));
                if (firstNumber == 4) {
                    builder.append(getCharForDecimalPlace(("" + number).length(), false)).append(getCharForDecimalPlace(("" + number).length(), true));

                    number -= Integer.parseInt("" + firstNumber + getPaddedString("0", ("" + number).length() - 1));
                } else if (firstNumber == 9) {
                    builder.append(getCharForDecimalPlace(("" + number).length(), false)).append(getCharForDecimalPlace(("" + number).length() + 1, false));

                    number -= Integer.parseInt("" + firstNumber + getPaddedString("0", ("" + number).length() - 1));
                } else {
                    if(firstNumber >= 5) {
                        builder.append(getCharForDecimalPlace(("" + number).length(), true));
                    } else {
                        builder.append(getCharForDecimalPlace(("" + number).length(), false));
                    }

                    number -= getAsNormalFigure(builder.charAt(builder.length() - 1));
                }
            }
        }

        return builder.length() > 0 ? builder.toString() : String.valueOf(number);
    }

    private char getCharForDecimalPlace(int place, boolean biggest) {
        switch(place) {
            case 1: {
                return biggest ? 'V' : 'I';
            }
            case 2: {
                return biggest ? 'L' : 'X';
            }
            case 3: {
                return biggest ? 'D' : 'C';
            }
            default: {
                return 'M';
            }
        }
    }

    private String getPaddedString(String base, int amount) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < amount; i++) {
            builder.append(base);
        }

        return builder.toString();
    }

    private int getAsNormalFigure(char c) {
        switch (Character.toUpperCase(c)) {
            case 'I': {
                return 1;
            }
            case 'V': {
                return 5;
            }
            case 'X': {
                return 10;
            }
            case 'L': {
                return 50;
            }
            case 'C': {
                return 100;
            }
            case 'D': {
                return 500;
            }
            case 'M': {
                return 1000;
            }
        }

        return 0;
    }
}