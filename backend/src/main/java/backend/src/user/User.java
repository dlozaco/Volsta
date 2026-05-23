package backend.src.user;

import backend.src.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User extends BaseEntity {

    @Column(unique = true)
    private String username;

    private String password;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "authority")
    Authorities authority;

    public Boolean hasAuthority(String auth){
        return authority.getAuthority().equals(auth);
    }

    public Boolean hasAnyAuthority(String... authorities) {
        Boolean cond = false;
        for (String auth : authorities) {
            if (auth.equals(authority.getAuthority()))
                cond = true;
        }
        return cond;
    }

}
