package com.womkarescode.microservicemeetup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.womkarescode.microservicemeetup.controller.resource.CreateMeetupController;
import com.womkarescode.microservicemeetup.exception.BusinessException;
import com.womkarescode.microservicemeetup.model.dto.CreateMeetupDTO;
import com.womkarescode.microservicemeetup.model.entity.CreateMeetup;
import com.womkarescode.microservicemeetup.model.entity.Meetup;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import com.womkarescode.microservicemeetup.service.CreateMeetupService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = CreateMeetupController.class)
@AutoConfigureMockMvc
public class CreateMeetupControllerTest {

    static String MEETUP_API = "/api/create-meetups";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CreateMeetupService service;

    @Test
    @DisplayName("Should successfully register new event meetup")
    public void testCreateEvent() throws Exception {

        CreateMeetupDTO dto = newEventMeetupDTO();
        CreateMeetup savedEvent = CreateMeetup.builder()
                .id(1L)
                .event("Palestra - Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Neri")
                .build();

        BDDMockito.given(service.saveNewEventMeetup(Mockito.any(CreateMeetup.class)))
                .willReturn(savedEvent);
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(MEETUP_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect( status().isCreated())
                .andExpect( jsonPath("id").value(1L))
                .andExpect( jsonPath("event").value(dto.getEvent()))
                .andExpect( jsonPath("linkMeetup").value(dto.getLinkMeetup()))
                .andExpect( jsonPath("guestSpeaker").value(dto.getGuestSpeaker()))
                .andExpect( jsonPath("hostedBy").value(dto.getHostedBy()));

    }

    @Test
    @DisplayName("Should return Event Created")
    public void testGetEventMeetup() throws Exception{

        Long id = 1L;

        CreateMeetupDTO dto = newEventMeetupDTO();

        CreateMeetup event = CreateMeetup.builder().id(1L)
                .id(1L)
                .event("Palestra - Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Neri")
                .build();


        BDDMockito.given( service.getEventById(id)).willReturn(Optional.of(event));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect( jsonPath("id").value(1l))
                .andExpect( jsonPath("event").value(dto.getEvent()))
                .andExpect( jsonPath("linkMeetup").value(dto.getLinkMeetup()))
                .andExpect( jsonPath("guestSpeaker").value(dto.getGuestSpeaker()))
                .andExpect( jsonPath("hostedBy").value(dto.getHostedBy()));
    }

    @Test
    @DisplayName("Should return NOT FOUND when the event doesn't exists")
    public void testEventNotFound() throws Exception {

        BDDMockito.given(service.getEventById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Shouldn't create event with event duplicated")
    public void testCreateEventMeetupWithRegistrationDuplicated() throws Exception {

        CreateMeetupDTO dto = newEventMeetupDTO();
        String json = new ObjectMapper().writeValueAsString(dto);
        BDDMockito.given(service.saveNewEventMeetup(Mockito.any(CreateMeetup.class)))
                .willThrow(new BusinessException("Event already registered"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(MEETUP_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform( request )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Event already registered"));

    }

    @Test
    @DisplayName("Should not delete event")
    public void testDeleteNonExistentEvent() throws Exception {

        BDDMockito.given( service.getEventById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(MEETUP_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete the event")
    public void testDeleteEvent() throws Exception {

        BDDMockito.given(service
                        .getEventById(anyLong()))
                        .willReturn(Optional.of(CreateMeetup.builder().id(11L).build()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(MEETUP_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should update event meetup")
    public void testUpdateEventMeetup() throws Exception {

        Long id = 1L;
        CreateMeetupDTO dto = newEventMeetupDTO();
        String json = new ObjectMapper().writeValueAsString(dto);

        CreateMeetup updatingEvent = CreateMeetup.builder()
                .id(1L)
                .event("Palestra - Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Neri")
                .build();

        BDDMockito.given( service.getEventById(id) ).willReturn( Optional.of(updatingEvent) );
        CreateMeetup updatedRegistration = CreateMeetup.builder()
                .id(1L)
                .event("Palestra - Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Neri")
                .build();

        BDDMockito.given(service.updateEventMeetup(updatingEvent)).willReturn(updatedRegistration);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(MEETUP_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform( request )
                .andExpect(status().isOk())
                .andExpect( jsonPath("id").value(id) )
                .andExpect( jsonPath("event").value(dto.getEvent()) )
                .andExpect( jsonPath("linkMeetup").value(dto.getLinkMeetup()) )
                .andExpect( jsonPath("guestSpeaker").value(dto.getGuestSpeaker()) )
                .andExpect( jsonPath("hostedBy").value(dto.getHostedBy()) );
    }

    @Test
    @DisplayName("Shouldn't update event meetup Not exist.")
    public void testUpdateEventMeetupNotExist() throws Exception {

        CreateMeetupDTO dto = newEventMeetupDTO();
        String json = new ObjectMapper().writeValueAsString(dto);
        BDDMockito.given( service.getEventById(Mockito.anyLong()))
                .willReturn( Optional.empty() );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(MEETUP_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform( request )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return all Event")
    public void testFindAllEvent() throws Exception{

        Long id = 1l;

        CreateMeetup eventMeetup = CreateMeetup.builder()
                .id(1L)
                .event("Palestra - Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Neri")
                .build();

        BDDMockito.given( service.findAllEventMeetup(Mockito.any(CreateMeetup.class),
                        Mockito.any(Pageable.class)))
                .willReturn( new PageImpl<CreateMeetup>( Arrays.asList(eventMeetup),
                        PageRequest.of(0,100), 1 ));

        String queryString = String.format("?event=%s&speaker=%s&page=0&size=100",
                eventMeetup.getEvent(), eventMeetup.getGuestSpeaker());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(MEETUP_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform( request )
                .andExpect( status().isOk() )
                .andExpect( jsonPath("content", Matchers.hasSize(1)))
                .andExpect( jsonPath("totalElements").value(1) )
                .andExpect( jsonPath("pageable.pageSize").value(100) )
                .andExpect( jsonPath("pageable.pageNumber").value(0));
    }

    private CreateMeetupDTO newEventMeetupDTO(){
        return CreateMeetupDTO.builder()
                .id(1L)
                .event("Palestra - Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Neri")
                .build();
    }
}
