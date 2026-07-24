package backend.src.manager;

import java.util.List;

public record ManagerProfile(String name,
                             String surname,
                             String email,
                             String phoneNumber,
                             List<String> teams) {
}
