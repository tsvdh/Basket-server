package basket.server.service;

import basket.server.dao.verificationcode.VerificationCodeDAO;
import basket.server.model.VerificationCode;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VerificationCodeService {

    private final VerificationCodeDAO verificationCodeDAO;

    @Autowired
    public VerificationCodeService(@Qualifier("localVerificationCodeDAO")
                                               VerificationCodeDAO verificationCodeDAO) {
        this.verificationCodeDAO = verificationCodeDAO;
    }

    public Optional<VerificationCode> get(String address) {
        log.info("Getting verification code of address {}", address);
        return verificationCodeDAO.get(address);
    }

    public boolean add(VerificationCode verificationCode) {
        log.info("Adding new verification code of address {}", verificationCode.getAddress());
        return verificationCodeDAO.add(verificationCode);
    }

    public boolean verify(String address, String code) {
        log.info("Verifying code of address {}", address);
        return verificationCodeDAO.verify(address, code);
    }
}
