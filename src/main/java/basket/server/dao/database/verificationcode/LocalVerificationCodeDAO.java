package basket.server.dao.database.verificationcode;

import basket.server.model.VerificationCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import static java.util.concurrent.TimeUnit.MINUTES;

@Repository("localVerificationCodeDAO")
public class LocalVerificationCodeDAO implements VerificationCodeDAO {

    private static final List<VerificationCode> localDB = new ArrayList<>();

    @Override
    public Optional<VerificationCode> get(String address) {
        return localDB.stream()
                .filter(verificationCode -> verificationCode.getAddress().equals(address))
                .findFirst();
    }

    @Override
    public VerificationCode submit(String address) {
        var newCode = new VerificationCode(address);
        newCode.setId(UUID.randomUUID().toString());

        get(address).ifPresent(localDB::remove);

        localDB.add(newCode);
        return newCode;
    }

    @Override
    public boolean verify(String address, String code) {
        Optional<VerificationCode> verificationCode = get(address);
        return verificationCode.map(value -> value.getCode().equals(code)).orElse(false);
    }

    /**
     * Mimic automatic database clean-up
     */
    @Scheduled(fixedRate = 15, timeUnit = MINUTES)
    void removeExpired() {
        var now = LocalDateTime.now();
        localDB.removeIf(verificationCode -> verificationCode.getCreatedAt().plusMinutes(15).isAfter(now));
    }
}
