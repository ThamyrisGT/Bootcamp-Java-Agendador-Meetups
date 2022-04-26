package com.womkarescode.microservicemeetup.controller;
import com.womkarescode.microservicemeetup.controller.resource.RegistrationController;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import com.womkarescode.microservicemeetup.exception.BusinessException;
import com.womkarescode.microservicemeetup.model.dto.RegistrationDTO;
import com.womkarescode.microservicemeetup.service.RegistrationService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {RegistrationController.class})
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    static String REGISTRATION_API = "/api/registration";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegistrationService registrationService;

    @Test
    @DisplayName("Should create a registration with success")
    public void testCreateRegistration() throws Exception {

        RegistrationDTO registrationDTOBuilder = createNewRegistration();
        Registration savedRegistration  = Registration.builder().id(101L)
                .name("Thamyris")
                .email("thammy@gmail.com")
                .password("1234")
                .dateOfRegistration(LocalDate.now())
                .registration("001").build();

        // simula camada do usuário, da parte da execução
        BDDMockito.given(registrationService.save(any(Registration.class))).willReturn(savedRegistration);


        String json  = new ObjectMapper().writeValueAsString(registrationDTOBuilder);


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(101L))
                .andExpect(jsonPath("name").value(registrationDTOBuilder.getName()))
                .andExpect( jsonPath("email").value(registrationDTOBuilder.getEmail()))
                .andExpect( jsonPath("password").value(registrationDTOBuilder.getPassword()))
                //.andExpect(jsonPath("dateOfRegistration").value(registrationDTOBuilder.getDateOfRegistration()))
                .andExpect(jsonPath("registration").value(registrationDTOBuilder.getRegistration()));
    }

    @Test
    @DisplayName("Should throw an exception when not have enough data for test")
    public void testCreateInvalidRegistration() throws  Exception{
        String json = new ObjectMapper().writeValueAsString(new RegistrationDTO());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get registration information")
    public void testGetRegistration() throws  Exception{
        Long id = 101L;

        Registration registration = Registration.builder()
                .id(createNewRegistration().getId())
                .name(createNewRegistration().getName())
                .email(createNewRegistration().getEmail())
                .password(createNewRegistration().getPassword())
                .dateOfRegistration(createNewRegistration().getDateOfRegistration())
                .registration(createNewRegistration().getRegistration())
                .build();

        BDDMockito.given(registrationService.getRegistrationById(id)).willReturn(Optional.of(registration));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/"+ id))
                .accept(MediaType.APPLICATION_JSON);


        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(101L))
                .andExpect(jsonPath("name").value(createNewRegistration().getName()))
                .andExpect( jsonPath("email").value(createNewRegistration().getEmail()))
                .andExpect( jsonPath("password").value(createNewRegistration().getPassword()))
                //.andExpect(jsonPath("dateOfRegistration").value(createNewRegistration().getDateOfRegistration()))
                .andExpect(jsonPath("registration").value(createNewRegistration().getRegistration()));
    }


    @Test
    @DisplayName("Should throw exception when try to create  a new registration with another  registration created")
    public void testCreateDuplicatedRegistration() throws  Exception{

        RegistrationDTO registrationDTO = createNewRegistration();
        String json = new ObjectMapper().writeValueAsString(registrationDTO);

        BDDMockito.given(registrationService.save(any(Registration.class))).willThrow(new BusinessException("Registration already exists"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors",hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Registration already exists"));
    }

    @Test
    @DisplayName("Should return NOT FOUND when the registration doesn't exists")
    public void testRegistrationNotFound() throws Exception {

        BDDMockito.given(registrationService.getRegistrationById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete the registration")
    public void testDeleteRegistration() throws Exception {

        BDDMockito.given(registrationService
                        .getRegistrationById(anyLong()))
                .willReturn(Optional.of(Registration.builder().id(11L).build()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return resource not found when no registration is found to delete")
    public void testDeleteNonExistentRegistration() throws Exception {

        BDDMockito.given(registrationService
                .getRegistrationById(anyLong())).willReturn(Optional.empty());


        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update registration")
    public void testUpdateRegistration() throws Exception {

        Long id = 11L;
        String json = new ObjectMapper().writeValueAsString(createNewRegistration());

        Registration updatingRegistration =
                Registration.builder()
                        .id(id)
                        .name("Thamyris")
                        .dateOfRegistration(LocalDate.now())
                        .registration("001")
                        .build();

        BDDMockito.given(registrationService.getRegistrationById(anyLong()))
                .willReturn(Optional.of(updatingRegistration));

        Registration updatedRegistration =
                Registration.builder()
                        .id(id)
                        .name("Thamyris")
                        .email("thammy@gmail.com")
                        .password("1234")
                        .dateOfRegistration(LocalDate.now())
                        .registration("001")
                        .build();

        BDDMockito.given(registrationService
                        .update(updatingRegistration))
                .willReturn(updatedRegistration);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(REGISTRATION_API.concat("/" + 1))
                .contentType(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(createNewRegistration().getName()))
                .andExpect(jsonPath("email").value(createNewRegistration().getEmail()))
                .andExpect(jsonPath("password").value(createNewRegistration().getPassword()))
                //.andExpect(jsonPath("dateOfRegistration").value(createNewRegistration().getDateOfRegistration()))
                .andExpect(jsonPath("registration").value("001"));
    }

    @Test
    @DisplayName("Should return 404 when try to update an registration no existent")
    public void testUpdateNonExistentRegistration() throws Exception {

        String json = new ObjectMapper().writeValueAsString(createNewRegistration());
        BDDMockito.given(registrationService.getRegistrationById(anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(REGISTRATION_API.concat("/" + 1))
                .contentType(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter registration")
    public void testFindRegistration() throws Exception {

        Registration registration = Registration.builder()
                .id(101l)
                .name("Thamyris")
                .email("thammy@gmail.com")
                .password("1234")
                .dateOfRegistration(LocalDate.now())
                .registration("001")
                .build();

        BDDMockito.given( registrationService.find(Mockito.any(Registration.class),
                        Mockito.any(Pageable.class)))
                .willReturn( new PageImpl<Registration>( Arrays.asList(registration),
                        PageRequest.of(0,100), 1 ));

        String queryString = String.format("?name=%s&email=%s&page=0&size=100",
                registration.getName(), registration.getEmail());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform( request )
                .andExpect( status().isOk() )
                .andExpect( jsonPath("content", Matchers.hasSize(1)))
                .andExpect( jsonPath("totalElements").value(1))
                .andExpect( jsonPath("pageable.pageSize").value(100))
                .andExpect( jsonPath("pageable.pageNumber").value(0));

    }

    private RegistrationDTO createNewRegistration() {
        return  RegistrationDTO.builder()
                .id(101l)
                .name("Thamyris")
                .email("thammy@gmail.com")
                .password("1234")
                .dateOfRegistration(LocalDate.now())
                .registration("001")
                .build();
    }
}
