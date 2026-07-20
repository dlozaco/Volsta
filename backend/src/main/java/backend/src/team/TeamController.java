package backend.src.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController (TeamService teamService){
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<Page<Team>> getAllTeams (@PageableDefault(page = 0, size = 10)Pageable pageable){
        return ResponseEntity.ok(teamService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Integer id){
        Team team = teamService.findById(id);
        if(team == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(team);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Team> getTeamByName(@PathVariable String name){
        Team team = teamService.findByName(name);
        if(team == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(team);
    }

}
