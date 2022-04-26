package com.womkarescode.microservicemeetup.service;

import com.womkarescode.microservicemeetup.model.entity.CreateMeetup;
import com.womkarescode.microservicemeetup.model.entity.Meetup;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import com.womkarescode.microservicemeetup.repository.MeetupRepository;
import com.womkarescode.microservicemeetup.repository.RegistrationRepository;
import com.womkarescode.microservicemeetup.service.impl.MeetupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MeetupServiceTest {

    MeetupService meetupService;

    @MockBean
    RegistrationRepository registrationRepository;

    @MockBean
    MeetupRepository meetupRepository;

    @BeforeEach
    public void setUp(){
        this.meetupService = new MeetupServiceImpl(meetupRepository);
    }

    @Test
    @DisplayName("Should create a meetup")
    public void testSaveMeetup(){
        Meetup meetupSaved = createValidMeetup();

        when( meetupRepository.save(createValidMeetup()) ).thenReturn( meetupSaved );

        Meetup meetup = meetupService.save(meetupSaved);

        assertThat(meetup.getId()).isEqualTo(meetupSaved.getId());
        assertThat(meetup.getMeetupDateRegistration()).isEqualTo(meetupSaved.getMeetupDateRegistration());
        assertThat(meetup.getRegistration()).isEqualTo(meetupSaved.getRegistration());
        assertThat(meetup.getEventDetails()).isEqualTo(meetupSaved.getEventDetails());

    }

    @Test
    @DisplayName("Should update schedule a meetup held by the user.")
    public void updateMeetupTest(){
        Registration registration = Registration.builder()
                .id(11L)
                .name("Thamyris")
                .email("thammy@gmail.com")
                .password("1234")
                .dateOfRegistration(LocalDate.now())
                .registration("Thamyris")
                .build();

        CreateMeetup createMeetup = CreateMeetup.builder()
                .id(11L)
                .event("Palestra Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Neri")
                .build();

        Meetup updatingMeetup = createValidMeetup();
        updatingMeetup.setId(11l);

        Meetup updatedMeetup = Meetup.builder()
                .meetupDateRegistration(LocalDate.now())
                .registration(registration)
                .eventDetails(createMeetup)
                .build();

        when( meetupRepository.save(updatedMeetup) ).thenReturn( updatedMeetup );

        Meetup meetup = meetupService.update(updatedMeetup);

        assertThat(meetup.getId()).isEqualTo(updatedMeetup.getId());
        assertThat(meetup.getMeetupDateRegistration()).isEqualTo(updatedMeetup.getMeetupDateRegistration());
        assertThat(meetup.getRegistration()).isEqualTo(updatedMeetup.getRegistration());
        assertThat(meetup.getEventDetails()).isEqualTo(updatedMeetup.getEventDetails());
    }

    @Test
    @DisplayName("Should return a meetup when searched by id")
    public void getMeetupByIdTest(){

        Long id = 1L;

        Meetup meetup = createValidMeetup();

        meetup.setId(id);

        Mockito.when( meetupRepository.findById(id)).thenReturn(Optional.of(meetup));

        Optional<Meetup> result = meetupService.getById(id);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getMeetupDateRegistration()).isEqualTo(meetup.getMeetupDateRegistration());
        assertThat(result.get().getRegistration()).isEqualTo(meetup.getRegistration());
        assertThat(result.get().getEventDetails()).isEqualTo(meetup.getEventDetails());

        verify( meetupRepository ).findById(id);

    }


    private Meetup createValidMeetup(){
        Meetup meetup = Meetup.builder().id(11l).build();
        Registration registration = Registration.builder()
                .id(11L)
                .name("Thamyris")
                .email("thammy@gmail.com")
                .password("1234")
                .dateOfRegistration(LocalDate.now())
                .registration("Thamyris")
                .build();

        CreateMeetup createMeetup = CreateMeetup.builder()
                .id(11L)
                .event("Palestra Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Neri")
                .build();

        return Meetup.builder()
                .meetupDateRegistration(LocalDate.now())
                .registration(registration)
                .eventDetails(createMeetup)
                .build();
    }

}
