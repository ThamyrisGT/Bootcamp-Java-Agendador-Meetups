package com.womkarescode.microservicemeetup.controller.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationForm {

    @NotEmpty
    private String name;

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

    @NotEmpty
    private String registration;
}
