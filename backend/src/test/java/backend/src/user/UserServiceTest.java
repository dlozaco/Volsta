package backend.src.user;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import backend.src.exceptions.ResourceNotFoundException;

@SpringBootTest
class UserServiceTest {

    private final UserService userService;
    private final AuthoritiesService authoritiesService;

    @Autowired
    public UserServiceTest (UserService userService, AuthoritiesService authoritiesService){
        this.userService = userService;
        this.authoritiesService = authoritiesService;
    }

    @Test
    void shouldFindAllUsers(){
        assertEquals(4, userService.findAll().size());
    }

    @Test
    void shouldFindUserByName_RightName() {
        String name = "manager1";
        assertEquals(name, userService.findUserByName(name).getUsername());
    }

    @Test
    void shouldFindUserByName_WrongName_ReturnsResourceNotFound() {
        String wrongName = "BadName";
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findUserByName(wrongName));
    }

    @Test
    void shouldFindUserById_RightId() {
        Integer id = 1;
        assertEquals(1, userService.findUserById(id).getId());
    }

    @Test
    void shouldFindUserById_WrongId_ReturnsResourceNotFound() {
        Integer wrongId = -123;
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findUserById(wrongId));
    }

    @Test
    void shouldFindAllUsersByAuthority(){
        String adminAuth = "ADMIN";
        List<User> admins = userService.findAllUsersByAuthority(adminAuth);
        assertEquals(2, admins.size());

        String managerAuth = "MANAGER";
        List<User> managers = userService.findAllUsersByAuthority(managerAuth);
        assertEquals(2, managers.size());
    }

    @Test
    void shouldExistsUser_ValidName_ReturnsTrue(){
        String rightName = "manager1";
        assertEquals(true, userService.existsUser(rightName));
    }

    @Test
    void shouldExistsUser_NotExistingName_ReturnsFalse(){
        String notExistingName = "UUUssserrr";
        assertEquals(false, userService.existsUser(notExistingName));
    }

    @Test
    @Transactional
    void shouldCreateUser_ReturnsOk() {
        int count = userService.findAll().size();

        User user = new User();
        user.setUsername("John");
        user.setPassword("password");
        user.setAuthority(authoritiesService.findByAuthoritiy("MANAGER"));

        userService.createUser(user);
        assertNotEquals(0, user.getId().longValue());
        assertNotNull(user.getId());

        int finalCount = userService.findAll().size();
        assertEquals(count+1, finalCount);
    }

    @Test
    @Transactional
    void shouldUpdateUser_RightId_ReturnsOk() {
        int userId = 1;
        User user = userService.findUserById(userId);
        user.setUsername("John");
        userService.updateUser(user, userId);
        user = userService.findUserById(userId);
        assertEquals("John", user.getUsername());
    }

    @Test
    void shouldDeleteUser_ReturnsVoid() {
        Integer firstCount = userService.findAll().size();
        User user = new User();
        user.setUsername("Sam");
        user.setPassword("password");
        Authorities auth = authoritiesService.findByAuthoritiy("MANAGER");
        user.setAuthority(auth);
        this.userService.createUser(user);

        Integer secondCount = userService.findAll().size();
        assertEquals(firstCount + 1, secondCount);
        userService.deleteUser(user.getId());
        Integer lastCount = userService.findAll().size();
        assertEquals(firstCount, lastCount);
    }
}