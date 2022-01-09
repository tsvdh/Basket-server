package basket.server.security;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.lang.Character.isWhitespace;

public class CredentialsValidation {

    public enum UsernameFault {
        WHITESPACE
    }

    public static Set<UsernameFault> rateUsername(String username) {
        Set<UsernameFault> faults = new HashSet<>();

        for (Character c : username.toCharArray()) {
            if (isWhitespace(c)) {
                faults.add(UsernameFault.WHITESPACE);
            }
        }

        return faults;
    }

    public enum PasswordFault {
        NOT_8_CHARS,
        NO_NUMBER,
        NO_LETTER,
        WHITESPACE
    }

    public static Set<PasswordFault> ratePassword(String password) {
        Set<PasswordFault> faults = new HashSet<>();

        if (password.length() < 8) {
            faults.add(PasswordFault.NOT_8_CHARS);
        }

        boolean number = false;
        boolean letter = false;

        for (Character c : password.toCharArray()) {
            if (isWhitespace(c)) {
                faults.add(PasswordFault.WHITESPACE);
            }
            if (isDigit(c)) {
                number = true;
            } else if (isLetter(c)) {
                letter = true;
            }
        }

        if (!number) {
            faults.add(PasswordFault.NO_NUMBER);
        }
        if (!letter) {
            faults.add(PasswordFault.NO_LETTER);
        }

        return faults;
    }
}
