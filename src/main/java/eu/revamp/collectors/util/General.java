package eu.revamp.collectors.util;

public class General {

    public static boolean isInt(Object o) {
        try {
            Integer.parseInt(String.valueOf(o));
            return true;
        }
        catch (Exception error) {
            return false;
        }
    }
}
