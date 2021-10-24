package basket.server.security;

public class PasswordRater {

    public enum Rating {
        GOOD,
        NOT_8_CHARS,
        NO_NUMBER
    }

    public static Rating rate(String password) {
        if (password.length() < 8) {
            return Rating.NOT_8_CHARS;
        }
        for (Character c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return Rating.GOOD;
            }
        }
        return Rating.NO_NUMBER;
    }
}
