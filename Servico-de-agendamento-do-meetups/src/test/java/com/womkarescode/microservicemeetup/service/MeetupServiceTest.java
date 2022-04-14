package com.womkarescode.microservicemeetup.service;

import com.womkarescode.microservicemeetup.controller.dto.MeetupDTO;
import com.womkarescode.microservicemeetup.exception.BusinessException;
import com.womkarescode.microservicemeetup.model.entity.Meetup;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import com.womkarescode.microservicemeetup.repository.MeetupRepository;
import com.womkarescode.microservicemeetup.repository.RegistrationRepository;
import com.womkarescode.microservicemeetup.service.impl.MeetupServiceImpl;
import com.womkarescode.microservicemeetup.service.impl.RegistrationServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MeetupServiceTest {

    RegistrationService registrationService;

    MeetupService meetupService;

    @MockBean
    RegistrationRepository registrationRepository;

    @MockBean
    MeetupRepository meetupRepository;

    @BeforeEach
    public void setUp(){
        this.registrationService = new RegistrationServiceImpl(registrationRepository);
        this.meetupService = new MeetupServiceImpl(meetupRepository);
    }

    @Test
    @DisplayName("Should create a meetup")
    public void testSaveMeetup(){
        Meetup meetup = createValidMeetup();
        Registration registration = createValidRegistration();
        Mockito.when(registrationRepository.existsByRegistration(Mockito.any())).thenReturn(true);
        Mockito.when(meetupRepository.save(meetup)).thenReturn(createValidMeetup());

        Meetup savedMeetup = meetupService.save(meetup);

        assertThat(savedMeetup.getId()).isEqualTo(11L);
        assertThat(savedMeetup.getRegistration()).isEqualTo(registration);
        assertThat(savedMeetup.getEvent()).isEqualTo("WomakersCode - Palestra");

    }

    @DisplayName("should not save a new meetup when try to register a registration already registered on a meetup")
    @Test
    public void testErrorOnCreateMeetup(){
        Meetup meetup = createValidMeetup();

        Mockito.when(registrationRepository.existsByRegistration(Mockito.any())).thenReturn(true);
        Mockito.when(meetupRepository.save(meetup)).thenThrow(new BusinessException("Meetup already enrolled"));

        Mockito.verify(meetupRepository,Mockito.never()).save(meetup);
    }

    @DisplayName("should not save a new meetup when try to register a registration nonexistent")
    @Test
    public void testErrorOnCreateMeetupWithInvalidRegistration(){
        Meetup meetup = createValidMeetup();
        Long id = 11L;
        Mockito.when(registrationRepository.findById(id)).thenReturn(Optional.empty());
        Optional<Registration> registration = registrationService.getRegistrationById(id);

        assertThat(registration.isPresent()).isFalse();
        Mockito.verify(meetupRepository,Mockito.never()).save(meetup);

    }


    private Registration createValidRegistration() {
        return Registration.builder()
                .id(101L)
                .name("Thamyris")
                .dateOfRegistration("01/04/2022")
                .registration("001")
                .build();
    }

    private Meetup createValidMeetup(){
        return Meetup.builder()
                .id(11L)
                .registration(createValidRegistration())
                .event("WomakersCode - Palestra").build();
    }

}
