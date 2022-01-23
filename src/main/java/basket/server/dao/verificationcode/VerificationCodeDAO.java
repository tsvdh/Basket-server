package basket.server.dao.verificationcode;

import basket.server.model.VerificationCode;
import java.util.Optional;

public interface VerificationCodeDAO {

    Optional<VerificationCode> get(String address);

    boolean submit(VerificationCode newCode);

    boolean verify(String address, String code);
}
