package com.friendgithub.api.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.*;
import jakarta.persistence.Id;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {
    @Id
    private String name;

    private String description;

    @ManyToMany
    Set<Permission> permissions;
}
