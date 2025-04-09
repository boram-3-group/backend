package com.boram.look.global.security.password;

import com.boram.look.domain.user.entity.User;
import com.boram.look.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User loginUser = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException());
        return new PrincipalDetails(loginUser);
    }
}
