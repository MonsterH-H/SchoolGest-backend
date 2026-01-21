package com.example.schoolgestapp.security;

import com.example.schoolgestapp.entity.User;
import com.example.schoolgestapp.repository.IUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final IUser userRepository;

    public CustomUserDetailsService(IUser userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Utilisateur introuvable : " + username)
                );

        // Rôle par défaut si null
        String roleName = (user.getRole() == null)
                ? "ETUDIANT"
                : user.getRole().name();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),   // compte activé
                true,              // account non expiré
                true,              // credentials non expirés
                true,              // compte non verrouillé
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + roleName)
                )
        );
    }
}
