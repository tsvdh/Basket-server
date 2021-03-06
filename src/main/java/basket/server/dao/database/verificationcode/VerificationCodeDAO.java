package basket.server.dao.database.verificationcode;

import basket.server.model.expiring.VerificationCode;
import java.util.Optional;

public interface VerificationCodeDAO {

    Optional<VerificationCode> get(String address);

    VerificationCode submit(String address);

    boolean verify(String address, String code);
}
