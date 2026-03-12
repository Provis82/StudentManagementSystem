package sms.util;

public class ValidationUtil {
    public static boolean isValidEmail(String email) {
        return email != null && 
               email.contains("@") && 
               email.contains(".") &&
               email.indexOf('@') < email.lastIndexOf('.') &&
               email.indexOf('@') > 0 &&
               email.lastIndexOf('.') < email.length() - 1;
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
    
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z\\s-']+$");
    }
    
    public static boolean isValidStudentId(String id) {
        return id != null && !id.trim().isEmpty() && id.matches("^[A-Z0-9-]+$");
    }
}