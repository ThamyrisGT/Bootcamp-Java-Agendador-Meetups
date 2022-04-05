package com.womkarescode.microservicemeetup.service;

import com.womkarescode.microservicemeetup.exception.BusinessException;
import com.womkarescode.microservicemeetup.model.Registration;
import com.womkarescode.microservicemeetup.repository.RegistrationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {

    RegistrationService service;

    @MockBean
    RegistrationRepository repository;


    @BeforeEach
    public void setUp(){
        this.service = new RegistrationServiceImpl(repository);
    }


    @Test
    @DisplayName("Should have an registration")
    public void testSaveStudent(){

        Registration registration = createValidRegistration();

        Mockito.when(repository.existsByRegistration((Mockito.anyString()))).thenReturn(false);
        Mockito.when(repository.save(registration)).thenReturn(createValidRegistration());

        Registration savedRegistration = service.save(registration);

        assertThat(savedRegistration.getId()).isEqualTo(101L);
        assertThat(savedRegistration.getName()).isEqualTo("Thamyris");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo("01/04/2022");
        assertThat(savedRegistration.getRegistration()).isEqualTo("001");

    }

    @Test
    @DisplayName("Should throw Business Exception error when try to safe a new registration with a duplicated registration")
    public void testShouldNotSafeAsRegistrationDuplicated(){
        Registration registration = createValidRegistration();
        Mockito.when(repository.existsByRegistration(Mockito.any())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(registration));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Registration already created");

        Mockito.verify(repository,Mockito.never()).save(registration);

    }

    @Test
    @DisplayName("Should get an registration by id")
    public void testGetByRegistrationId(){
        Long id = 101L;
        Registration registration = createValidRegistration();
        registration.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(registration));

        Optional<Registration> foundRegistration = service.getRegistrationById(id);

        assertThat(foundRegistration.isPresent()).isTrue();
        assertThat(foundRegistration.get().getName()).isEqualTo(registration.getName());
        assertThat(foundRegistration.get().getDateOfRegistration()).isEqualTo(registration.getDateOfRegistration());
        assertThat(foundRegistration.get().getRegistration()).isEqualTo(registration.getRegistration());
    }

    @Test
    @DisplayName("Should return empty when get an registration by id that doesn't exist")
    public void testRegistrationNotFoundById(){
        Long id = 11L;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Registration> registration = service.getRegistrationById(id);

        assertThat(registration.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should delete an student ")
    public void testDeleteRegistrationTest(){
        Registration registration = Registration.builder().id(11L).build();

        assertDoesNotThrow(()-> service.delete(registration));

        Mockito.verify(repository,Mockito.times(1)).delete(registration);
    }
    @Test
    @DisplayName("Should update an registration")
    public void testUpdateRegistration() {

        Long id = 11L;
        Registration updatingRegistration = Registration.builder().id(11L).build();

        Registration updatedRegistration = createValidRegistration();
        updatedRegistration.setId(id);

        Mockito.when(repository.save(updatingRegistration)).thenReturn(updatedRegistration);
        Registration registration = service.update(updatingRegistration);

        assertThat(registration.getId()).isEqualTo(updatedRegistration.getId());
        assertThat(registration.getName()).isEqualTo(updatedRegistration.getName());
        assertThat(registration.getDateOfRegistration()).isEqualTo(updatedRegistration.getDateOfRegistration());
        assertThat(registration.getRegistration()).isEqualTo(updatedRegistration.getRegistration());

    }

    @Test
    @DisplayName("Should filter registration must by properties")
    public void testFindRegistration(){
        Registration registration = createValidRegistration();

        PageRequest pageRequest = PageRequest.of(0,10);

        List<Registration> registrationList = Arrays.asList(registration);
        Page<Registration> page = new PageImpl<Registration>(Arrays.asList(registration),
               PageRequest.of(0,10),1);

        Mockito.when(repository.findAll(Mockito.any(Example.class),Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Registration>  result = service.find(registration, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(registrationList);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should get an Registration model by registration attribute")
    public void getRegistrationByRegistration(){
        String registrationAttribute = "1234";

        Mockito.when(repository.findByRegistration(registrationAttribute))
                .thenReturn(Optional.of(Registration.builder().id(11L).registration(registrationAttribute).build()));

        Optional<Registration> registration = service.getRegistrationByRegistrationAttribute(registrationAttribute);

        assertThat(registration.isPresent()).isTrue();
        assertThat(registration.get().getId()).isEqualTo(11L);
        assertThat(registration.get().getRegistration()).isEqualTo(registrationAttribute);

        Mockito.verify(repository,Mockito.times(1)).findByRegistration(registrationAttribute);
    }

    private Registration createValidRegistration() {
        return Registration.builder()
                .id(101L)
                .name("Thamyris")
                .dateOfRegistration("01/04/2022")
                .registration("001")
                .build();
    }
}
