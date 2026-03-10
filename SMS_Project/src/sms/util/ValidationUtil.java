package sms.util;

public class ValidationUtil {
    public static boolean isValidEmail(String email) {
        return email != null && 
               email.contains("@") && 
               email.contains(".") &&
               email.indexOf('@') < email.lastIndexOf('.') &&
               email.indexOf('@') > 0;
    }

    public static boolean isNumeric(String value) {
        if (value == null || value.isEmpty()) return false;
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}