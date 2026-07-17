package backend.src.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.src.exceptions.ResourceNotFoundException;

@Service
public class AuthoritiesService {


    private final AuthoritiesRepository authoritiesRepository;

    public AuthoritiesService(AuthoritiesRepository authoritiesRepository){
        this.authoritiesRepository = authoritiesRepository;
    }

    @Transactional(readOnly = true)
    public List<Authorities> findAllAuthorities(){
        return authoritiesRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Authorities findByAuthoritiy(String authority){
        return authoritiesRepository.findByAuthority(authority)
                .orElseThrow(() -> new ResourceNotFoundException("Authority", "Name", authority));
    }

}
