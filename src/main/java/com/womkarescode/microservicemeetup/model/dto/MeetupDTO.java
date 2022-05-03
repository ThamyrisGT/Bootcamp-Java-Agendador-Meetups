package com.womkarescode.microservicemeetup.model.dto;

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

    private CreateMeetupDTO eventDetails;

    private RegistrationDTO registration;
}
