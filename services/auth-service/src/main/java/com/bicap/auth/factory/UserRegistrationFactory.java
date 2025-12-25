package com.bicap.auth.factory;

import java.util.HashSet;
import java.util.Set;
import com.bicap.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bicap.auth.dto.AuthRequest;
import com.bicap.auth.model.ERole;
import com.bicap.auth.model.Role;
import com.bicap.auth.model.User;
import com.bicap.auth.model.UserStatus;
import com.bicap.auth.repository.RoleRepository;

@Component
public class UserRegistrationFactory {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserRegistrationFactory(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(AuthRequest authRequest) {
        String requestedRole = authRequest.getRole().toUpperCase();

        if (requestedRole.equals("ADMIN") || requestedRole.equals("DELIVERY_DRIVER")) {
            throw new IllegalArgumentException("Cannot self-register for high-priviledge roles.");
        }

        User user = new User();

        Set<Role> roles = new HashSet<>();

        if (requestedRole.equals("FARM")) {
            roles.add(getRole(ERole.ROLE_FARM_MANAGER));
            user.setStatus(UserStatus.PENDING);
        }
        else if (requestedRole.equals("RETAILER")) {
            roles.add(getRole(ERole.ROLE_RETAILER));
            user.setStatus(UserStatus.ACTIVE);
        }
        else {
            roles.add(getRole(ERole.ROLE_GUEST));
            user.setStatus(UserStatus.ACTIVE);
        }
        user.setEmail(authRequest.getEmail());
        user.setUsername(authRequest.getUsername());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole(roles);
        return userRepository.save(user);
    }

    public Role getRole(ERole roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("System Error: Role " + roleName + " not found in the database."));
    }
}
