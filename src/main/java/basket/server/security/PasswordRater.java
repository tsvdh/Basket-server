package basket.server.security;

import static java.lang.Character.isDigit;
import static java.lang.Character.isSpaceChar;

public class PasswordRater {

    public enum Rating {
        GOOD,
        NOT_8_CHARS,
        NO_NUMBER,
        WHITESPACE
    }

    public static Rating rate(String password) {
        if (password.length() < 8) {
            return Rating.NOT_8_CHARS;
        }
        for (Character c : password.toCharArray()) {
            if (isSpaceChar(c)) {
                return Rating.WHITESPACE;
            }
        }
        for (Character c : password.toCharArray()) {
            if (isDigit(c)) {
                return Rating.GOOD;
            }
        }

        return Rating.NO_NUMBER;
    }
}
