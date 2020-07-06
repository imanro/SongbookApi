package songbook.user.rest;

import org.springframework.web.bind.annotation.*;
import songbook.domain.user.entity.User;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/sign-up")
    public void signUp(@RequestBody User user) {

        // userRegistration usecase
    }
}
