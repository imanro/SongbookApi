package songbook.domain.user.port.out;

public interface PasswordEndoderPort {
    String encode(String password);
}
