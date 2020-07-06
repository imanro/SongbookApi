package songbook.user.rest;

import org.springframework.web.bind.annotation.*;
import songbook.domain.user.entity.User;
import songbook.domain.user.port.in.AddUserCommand;

@RestController
@RequestMapping("/user")
public class UserController {

    private final AddUserCommand addUserCommand;

    public UserController(AddUserCommand addUserCommand) {
        this.addUserCommand = addUserCommand;
    }

    @PostMapping("/sign-up")
    public User signUp(@RequestBody User user) {
        return addUserCommand.addUser(user);
    }
}
