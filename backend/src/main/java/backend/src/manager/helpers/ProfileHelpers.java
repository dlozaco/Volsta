package backend.src.manager.helpers;

import backend.src.manager.Manager;
import backend.src.manager.ManagerProfile;
import backend.src.team.Team;

import java.util.List;

public class ProfileHelpers {

    public static ManagerProfile managerToProfile(Manager manager){
        List<String> teams = manager.getTeams().stream().map(Team::getName).toList();
        return (new ManagerProfile(
                manager.getName(),
                manager.getSurname(),
                manager.getEmail(),
                manager.getPhoneNumber(),
                manager.getPhotoUrl(),
                teams
        ));
    }

}
