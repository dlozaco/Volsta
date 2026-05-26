package backend.src.user;

import backend.src.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthoritiesServiceTest {

    private final AuthoritiesService authoritiesService;

    @Autowired
    public AuthoritiesServiceTest(AuthoritiesService authoritiesService){
        this.authoritiesService = authoritiesService;
    }

    @Test
    void shouldFindAllAuthorities(){
        assertEquals(2, authoritiesService.findAllAuthorities().size());
    }

    @Test
    void shouldFindAuhorityByName_NotExistingName_ReturnsResourceNotFound(){
        String name = "Test";
        assertThrows(ResourceNotFoundException.class, () -> authoritiesService.findByAuthoritiy(name));
    }
}