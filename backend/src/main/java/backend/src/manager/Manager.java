package backend.src.manager;

import backend.src.model.BaseEntity;
import backend.src.team.Team;
import backend.src.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Manager extends BaseEntity {
    @NotNull
    private String name;

    private String surname;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    @Column(unique = true)
    @Size(max = 9)
    private String phoneNumber;

    @OneToMany
    List<Team> teams;

    @OneToOne
    User user;
}
