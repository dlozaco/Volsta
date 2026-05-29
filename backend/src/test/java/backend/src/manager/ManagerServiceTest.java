package backend.src.manager;

import backend.src.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ManagerServiceTest {

    private final ManagerService managerService;

    @Autowired
    public ManagerServiceTest(ManagerService managerService) {
        this.managerService = managerService;
    }

    @Test
    void shouldFindAllManagers() {
        assertEquals(2, managerService.findAll().size());
    }

    @Test
    void shouldFindManagerById_RightId() {
        Manager manager = managerService.findById(1);
        assertEquals("Manager1", manager.getName());
    }

    @Test
    void shouldFindManagerById_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> managerService.findById(-1));
    }

    @Test
    void shouldFindManagerByEmail_RightEmail() {
        Manager manager = managerService.findByEmail("manager@prueba.com");
        assertEquals(1, manager.getId());
    }

    @Test
    void shouldFindManagerByEmail_WrongEmail_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> managerService.findByEmail("nobody@test.com"));
    }

    @Test
    void shouldFindManagerByPhoneNumber_RightPhone() {
        Manager manager = managerService.findByPhoneNumber("111111111");
        assertEquals("Manager1", manager.getName());
    }

    @Test
    void shouldFindManagerByPhoneNumber_WrongPhone_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> managerService.findByPhoneNumber("000000000"));
    }

    @Test
    void shouldFindManagerByUserId() {
        Manager manager = managerService.findByUserId(3);
        assertEquals("Manager1", manager.getName());
    }

    @Test
    void shouldFindManagerByUserId_WrongId_ReturnsResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> managerService.findByUserId(-1));
    }

    @Test
    @Transactional
    void shouldCreateManager_ReturnsOk() {
        int count = managerService.findAll().size();

        Manager manager = new Manager();
        manager.setName("NewManager");
        manager.setSurname("Test");
        manager.setEmail("new.manager@test.com");
        manager.setPhoneNumber("333333333");
        managerService.create(manager);

        assertNotNull(manager.getId());
        assertEquals(count + 1, managerService.findAll().size());
    }

    @Test
    @Transactional
    void shouldUpdateManager_RightId_ReturnsOk() {
        Manager manager = managerService.findById(1);
        manager.setName("UpdatedName");
        managerService.update(manager, 1);

        Manager updated = managerService.findById(1);
        assertEquals("UpdatedName", updated.getName());
    }

    @Test
    @Transactional
    void shouldDeleteManager_ReturnsVoid() {
        int count = managerService.findAll().size();

        Manager manager = new Manager();
        manager.setName("Temp");
        manager.setSurname("Mgr");
        manager.setEmail("temp.manager@test.com");
        manager.setPhoneNumber("444444444");
        managerService.create(manager);

        assertEquals(count + 1, managerService.findAll().size());
        managerService.delete(manager.getId());
        assertEquals(count, managerService.findAll().size());
    }
}
