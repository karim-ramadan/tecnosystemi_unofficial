package it.tecnosystemi.TS.Utils;

import java.util.regex.Pattern;

public class Validation {
    public static boolean isValidEmail(String str) {
        return Pattern.compile(Constants.EMAIL_PATTERN).matcher(str).matches();
    }

    public static boolean isValidPassword(String str) {
        return str != null && str.length() >= 8 && str.matches("^(?=.*[a-zA-Z\\p{P}\\p{S} ])(?=.*\\d)[A-Za-z\\d\\p{P}\\p{S} ]{8,}$");
    }
}
