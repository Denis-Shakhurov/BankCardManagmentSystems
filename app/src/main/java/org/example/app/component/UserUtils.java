package org.example.app.component;

import lombok.RequiredArgsConstructor;
import org.example.app.model.User;
import org.example.app.repository.UserRepository;
import org.example.app.service.CustomUserDetailsService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtils implements ApplicationRunner {
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        String email = auth.getName();
        return userRepository.findByEmail(email).get();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = new User();
        user.setEmail("admin@admin.com");
        user.setPassword("admin");
        userDetailsService.createUser(user);
    }
}
