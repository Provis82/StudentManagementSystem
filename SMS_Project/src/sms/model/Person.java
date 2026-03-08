package sms.model;

public abstract class Person {
    protected String name;
    protected String email;

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }

    // Setters
    public void setName(String name) { this.name = toTitleCase(name); }
    public void setEmail(String email) { this.email = email; }

    // Title Case string manipulation
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1))
                  .append(" ");
        }
        return result.toString().trim();
    }

    public abstract void displayInfo();
}