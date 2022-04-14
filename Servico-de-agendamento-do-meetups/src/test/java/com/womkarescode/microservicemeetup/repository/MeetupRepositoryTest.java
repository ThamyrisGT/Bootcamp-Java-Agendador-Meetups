package com.womkarescode.microservicemeetup.repository;

import com.womkarescode.microservicemeetup.model.entity.Meetup;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class MeetupRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    MeetupRepository meetupRepository;

    @Autowired
    RegistrationRepository registrationRepository;

    @Test
    @DisplayName("Should return true when exists a meetup")
    public void testReturnMeetupSuccess(){
        String registration = "123";

        Registration registrationAttribute = createNewRegistration(registration);
        registrationRepository.save(registrationAttribute);

        Meetup meetup = createNewMeetup(registrationAttribute);
        meetupRepository.save(meetup);

        Meetup exists = meetupRepository.getById(meetup.getId());
        assertThat(exists).isNotNull();
    }

    @Test
    @DisplayName("Should return false when doesn't exists a meetup")
    public void testNotReturnMeetup(){
        Long id = 11L;
        Boolean exists = meetupRepository.existsById(id);
        assertThat(exists).isFalse();
    }

    @DisplayName("should save a meetup")
    @Test
    public void testSaveMeetup(){
        String registration = "123";

        Registration registrationAttribute = createNewRegistration(registration);
        registrationRepository.save(registrationAttribute);

        Meetup meetup = createNewMeetup(registrationAttribute);
        Meetup savedMeetup = meetupRepository.save(meetup);

        assertThat(savedMeetup.getId()).isNotNull();
    }

//    @DisplayName("should return meetup by id")
//    @Test
//    public void testReturnMeetupById(){
//        String registration = "123";
//        Registration registrationAttribute = createNewRegistration(registration);
//        registrationRepository.save(registrationAttribute);
//        Meetup meetup = createNewMeetup(registrationAttribute);
//        meetupRepository.save(meetup);
//
//        Optional<Meetup> meetupFound = meetupRepository.findById(meetup.getId());
//
//        assertThat(meetupFound.isPresent()).isTrue();
//    }


    private Registration createNewRegistration(String registration) {
        return Registration.builder()
                .name("Thamyris")
                .dateOfRegistration("10/10/2021")
                .registration(registration)
                .build();
    }

    private Meetup createNewMeetup(Registration registration){
        return Meetup.builder()
                .id(11L)
                .registration(registration)
                .meetupDate("10/10/2021")
                .registered(true)
                .event("WomakersCode - Palestra").build();
    }
}
