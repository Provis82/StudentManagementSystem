package sms.util;

public class ValidationUtil {

    public static boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    public static boolean isNumeric(String value) {

        try {
            Double.parseDouble(value);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }
}