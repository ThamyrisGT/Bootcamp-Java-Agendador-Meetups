package com.womkarescode.microservicemeetup.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetupDTO {

    private Long id;

    private String registrationAttribute;

    private String event;

    private RegistrationDTO registration;
}
