package ru.dageev.schedule;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.dageev.schedule.models.Role;
import ru.dageev.schedule.repository.RoleRepository;

import static ru.dageev.schedule.models.ERole.ROLE_ADMIN;
import static ru.dageev.schedule.models.ERole.ROLE_STAFF;

@Component
public class DataLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void run(ApplicationArguments args) {
        roleRepository.save(new Role(ROLE_ADMIN));
        roleRepository.save(new Role(ROLE_STAFF));
    }
}
