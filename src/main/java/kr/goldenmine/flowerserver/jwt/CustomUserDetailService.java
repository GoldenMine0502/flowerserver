package kr.goldenmine.flowerserver.jwt;

import kr.goldenmine.flowerserver.profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

//    private final UserRepository userRepository;

    private final ProfileService service;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return service.getProfile(userName)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
}