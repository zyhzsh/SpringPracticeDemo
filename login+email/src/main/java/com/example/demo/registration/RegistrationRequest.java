package com.example.demo.registration;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class RegistrationRequest {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
}
