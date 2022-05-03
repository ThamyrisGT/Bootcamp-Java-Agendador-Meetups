package com.womkarescode.microservicemeetup.controller;

import com.womkarescode.microservicemeetup.model.dto.MeetupDTO;
import com.womkarescode.microservicemeetup.controller.resource.MeetupController;
import com.womkarescode.microservicemeetup.exception.BusinessException;
import com.womkarescode.microservicemeetup.model.dto.MeetupFilterDTO;
import com.womkarescode.microservicemeetup.model.entity.CreateMeetup;
import com.womkarescode.microservicemeetup.model.entity.Meetup;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import com.womkarescode.microservicemeetup.service.CreateMeetupService;
import com.womkarescode.microservicemeetup.service.MeetupService;
import com.womkarescode.microservicemeetup.service.RegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {MeetupController.class})
@AutoConfigureMockMvc
public class MeetupControllerTest {

    static final String MEETUP_API = "/api/meetups";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private CreateMeetupService createMeetupService;

    @MockBean
    private MeetupService meetupService;


    @Test
    @DisplayName("Should create a meetup")
    public void testCreateMeetup() throws Exception{
        MeetupFilterDTO dto = MeetupFilterDTO.builder()
                .event("Palestra Microservice")
                .registration("Thamyris")
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        Registration registration = Registration.builder()
                .id(11L)
                .name("Thamyris")
                .email("thammy@gmail.com")
                .password("1234")
                .dateOfRegistration(LocalDate.now())
                .registration("Thamyris")
                .build();

        CreateMeetup eventMeetup = CreateMeetup.builder()
                .id(11L)
                .event("Palestra Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Neri")
                .build();

        BDDMockito.given(registrationService.getByRegistration(registration.getRegistration())).
                willReturn(Optional.of(registration));

        BDDMockito.given(createMeetupService.findByEvent(eventMeetup.getEvent())).
                willReturn(Optional.of(eventMeetup));

        Meetup meetup = Meetup.builder()
                .id(11L)
                .registration(registration)
                .eventDetails(eventMeetup)
                .build();

        BDDMockito.given(meetupService.save(Mockito.any(Meetup.class))).willReturn(meetup);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(MEETUP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated());

    }
    @Test
    @DisplayName("Should return error when try to register a meetup nonexistent")
    public void testInvalidRegistrationCreateMeetup () throws Exception{
        MeetupFilterDTO dto = MeetupFilterDTO.builder().event("Palestra Microservice").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Registration registration = Registration.builder()
                .id(11L)
                .name("Thamyris")
                .email("thammy@gmail.com")
                .password("1234")
                .dateOfRegistration(LocalDate.now())
                .registration("Thamyris")
                .build();

        CreateMeetup eventMeetup = CreateMeetup.builder()
                .id(11L)
                .event("Palestra Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Neri")
                .build();

        BDDMockito.given(registrationService.getByRegistration(registration.getRegistration())).
                willReturn(Optional.of(registration));

        BDDMockito.given(createMeetupService.findByEvent(eventMeetup.getEvent())).
                willReturn(Optional.of(eventMeetup));

        Meetup meetup = Meetup.builder().id(1L).registration(registration).eventDetails(eventMeetup).build();

        BDDMockito.given(meetupService.save(Mockito.any(Meetup.class))).willReturn(meetup);


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(MEETUP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return error when try to register a registration already register on a meetup")
    public void testRegistrationErrorOnCreateMeetup () throws Exception{
        MeetupFilterDTO dto = MeetupFilterDTO.builder().event("Palestra Microservice").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Registration registration = Registration.builder()
                .id(11L)
                .name("Thamyris")
                .email("thammy@gmail.com")
                .password("1234")
                .dateOfRegistration(LocalDate.now())
                .registration("Thamyris")
                .build();

        CreateMeetup eventMeetup = CreateMeetup.builder()
                .id(11L)
                .event("Palestra Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Neri")
                .build();

        BDDMockito.given(registrationService.getByRegistration(registration.getRegistration())).
                willReturn(Optional.of(registration));

        BDDMockito.given(createMeetupService.findByEvent(eventMeetup.getEvent())).
                willReturn(Optional.of(eventMeetup));

        Meetup meetup = Meetup.builder()
                .id(11L)
                .registration(registration)
                .eventDetails(eventMeetup)
                .build();

        BDDMockito.given(meetupService.save(Mockito.any(Meetup.class)))
                .willReturn(meetup);

        BDDMockito.given(meetupService.save(Mockito.any(Meetup.class)))
                .willThrow(new BusinessException("Meetup already enrolled"));


        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(MEETUP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
}
