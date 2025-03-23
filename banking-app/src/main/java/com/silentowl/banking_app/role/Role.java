package com.silentowl.banking_app.role;

import com.silentowl.banking_app.common.AbstractEntity;
import com.silentowl.banking_app.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "roles")
public class Role extends AbstractEntity {

    private String name;

    @OneToMany(mappedBy = "role")
    private List<User> users;
}
