package com.friendgithub.api.dto.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreationRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;
}
