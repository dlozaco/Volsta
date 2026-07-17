package backend.src.manager;

import backend.src.model.BaseEntity;
import backend.src.team.Team;
import backend.src.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Manager extends BaseEntity {

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    @Column(unique = true)
    @Size(min = 7, max = 15)
    @Pattern(
            regexp = "^\\+?[0-9\\s\\-()]{7,15}$",
            message = "Phone number must have between 7 and 15 numbers"
    )
    private String phoneNumber;

    @OneToMany
    List<Team> teams;

    @OneToOne
    User user;
}
