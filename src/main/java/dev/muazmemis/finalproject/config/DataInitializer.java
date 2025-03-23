package dev.muazmemis.finalproject.config;

import dev.muazmemis.finalproject.dto.user.UserRequest;
import dev.muazmemis.finalproject.model.enums.Role;
import dev.muazmemis.finalproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) {
        if (!userService.findAllUsers().isEmpty())
            return;

        UserRequest projectManager = new UserRequest("adminFirst", "adminLast", "admin",
                "admin", Role.PROJECT_MANAGER);

        UserRequest teamLeader = new UserRequest("leadFirst", "leadLast", "lead",
                "lead", Role.TEAM_LEADER);

        UserRequest teamMember = new UserRequest("memberFirst", "memberLast", "member",
                "member", Role.TEAM_MEMBER);

        userService.saveUser(projectManager);
        userService.saveUser(teamLeader);
        userService.saveUser(teamMember);
    }
}
