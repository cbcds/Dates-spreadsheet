package cbcds;

import java.text.ParseException;
import java.util.*;

public class Calculator {
    private final static int MILLIS_IN_DAY = 86400000;

    private static Date sum(Date date, int op) {
        long d1 = date.getTime();
        long d2 = op * MILLIS_IN_DAY;
        return new Date(d1 + d2);
    }

    private static Date sub(Date date, int op) {
        long d1 = date.getTime();
        long d2 = op * MILLIS_IN_DAY;
        return new Date(d1 - d2);
    }

    private static Date min(DatesTableView table, String[] data) {
        if (data.length == 1) {
            throw new IllegalArgumentException();
        }
        long min = Long.MAX_VALUE;
        for (int i = 1; i < data.length; ++i) {
            long date = getDateFromString(table, data[i]).getTime();
            if (date < min) {
                min = date;
            }
        }
        return new Date(min);
    }

    private static Date max(DatesTableView table, String[] data) {
        if (data.length == 1) {
            throw new IllegalArgumentException();
        }
        long max = Long.MIN_VALUE;
        for (int i = 1; i < data.length; ++i) {
            long date = getDateFromString(table, data[i]).getTime();
            if (date > max) {
                max = date;
            }
        }
        return new Date(max);
    }

    public static Date calculate(DatesTableView table, String formula) throws IllegalArgumentException {
        String[] s = formula.split("((?<=[=+-])|(?=[=+-]))");
        if (s.length == 1) {
            return getDateFromString(table, s[0]);
        }
        if (s[0].equals("=")) {
            if (s.length == 4) {
                String op = s[2];
                int k = Integer.parseInt(s[3]);
                Date date = getDateFromString(table, s[1]);
                switch (op) {
                    case "+": return sum(date, k);
                    case "-": return sub(date, k);
                }
            } else if (s.length == 2) {
                String[] elems = s[1].split("[(),]");
                String op = elems[0].toLowerCase();
                switch (op) {
                    case "min": return min(table, elems);
                    case "max": return max(table, elems);
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private static Date getDateFromString(DatesTableView table, String s) {
        if (Character.isAlphabetic(s.charAt(0))) {
            String[] cell = s.split("((?<=\\d)|(?=[\\d]))");
            if (cell.length != 2 || cell[0].length() != 1) {
                throw new IllegalArgumentException();
            }
            try {
                char col = cell[0].charAt(0);
                int row = Integer.parseInt(cell[1]);
                return table.getValue(row, col);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                throw new IllegalArgumentException();
            }
        } else {
            try {
                return DateUtils.toDate(s);
            } catch (ParseException e) {
                throw new IllegalArgumentException();
            }
        }
    }
}
