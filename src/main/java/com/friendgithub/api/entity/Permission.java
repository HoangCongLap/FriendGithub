package com.friendgithub.api.entity;

import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.Id;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Permission {
    @Id
    private String name;

    private String description;
}
