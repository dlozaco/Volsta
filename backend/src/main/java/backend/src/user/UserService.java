package backend.src.user;

import backend.src.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAll(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findUserById(Integer id){
        return userRepository.findUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional(readOnly = true)
    public User findUserByName(String name){
        return userRepository.findUserByUsername(name)
                .orElseThrow(() -> new ResourceNotFoundException("User", "name", name));
    }

    public Boolean existsUser(String username){
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsersByAuthority(String auth){
        return userRepository.findUsersByAuthority(auth);
    }

    @Transactional
    public User createUser(User user) throws DataAccessException {
        userRepository.save(user);
        return user;
    }

    @Transactional
    public User updateUser(@Valid User user, Integer idToUpdate){
        User toUpdate = findUserById(idToUpdate);
        BeanUtils.copyProperties(user, toUpdate, "id");
        userRepository.save(toUpdate);
        return toUpdate;
    }

    @Transactional
    public void deleteUser(Integer id) {
        User toDelete = findUserById(id);
        this.userRepository.delete(toDelete);
    }
}
