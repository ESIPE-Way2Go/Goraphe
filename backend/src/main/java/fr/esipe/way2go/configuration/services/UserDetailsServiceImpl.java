package fr.esipe.way2go.configuration.services;

import fr.esipe.way2go.dao.UserEntity;
import fr.esipe.way2go.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String code) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(code)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with this code: " + code));

        return UserDetailsImpl.build(user);
    }

}