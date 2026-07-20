package backend.src.manager;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import backend.src.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;

@Service
@Validated
public class ManagerService {

    private final ManagerRepository managerRepository;

    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Transactional(readOnly = true)
    public List<Manager> findAll() {
        return managerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Manager findById(Integer id) {
        return managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "id", id));
    }

    @Transactional(readOnly = true)
    public Manager findByEmail(String email) {
        return managerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "email", email));
    }

    @Transactional(readOnly = true)
    public Manager findByPhoneNumber(String phoneNumber) {
        return managerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "phoneNumber", phoneNumber));
    }

    @Transactional(readOnly = true)
    public Manager findByUserId(Integer userId) {
        return managerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "userId", userId));
    }

    @Transactional
    public Manager create(Manager manager) {
        return managerRepository.save(manager);
    }

    @Transactional
    public Manager update(@Valid Manager manager, Integer id) {
        // TODO Se tiene que comprobar si el que lo updatea es él mismo
        Manager existing = findById(id);
        BeanUtils.copyProperties(manager, existing, "id");
        return managerRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        // TODO Se tiene que comprobar si el que lo borra es él mismo
        Manager manager = findById(id);
        managerRepository.delete(manager);
    }
}
