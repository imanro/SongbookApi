package songbook.adapter.user.out;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import songbook.domain.user.port.out.PasswordEndoderPort;

@Component
public class PasswordEncoderPort implements PasswordEndoderPort {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public PasswordEncoderPort(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String encode(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}
