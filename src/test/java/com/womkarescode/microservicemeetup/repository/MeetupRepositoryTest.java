package com.womkarescode.microservicemeetup.repository;

import com.womkarescode.microservicemeetup.model.entity.CreateMeetup;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class MeetupRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    MeetupRepository repository;


    @Test
    @DisplayName("Should return meetup by id")
    public void testFindMeetupById (){
        Meetup meetup = createNewMeetup();

        entityManager.persist(meetup);

        Optional<Meetup> foundMeetup = repository.findById(meetup.getId());
        assertThat(foundMeetup.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should save meetup")
    public void testSaveMeetup() {

        Meetup meetup = createNewMeetup();

        Meetup savedMeetup = repository.save(meetup);

        assertThat(savedMeetup.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should update meetup by id")
    public void testUpdateMeetupById (){
        Meetup meetup = createNewMeetup();

        entityManager.persist(meetup);

        Optional<Meetup> foundMeetup = repository.findById(meetup.getId());
        Meetup savedMeetup = repository.save(meetup);

        assertThat(savedMeetup.getId()).isNotNull();
        assertThat(foundMeetup.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should delete meetup,event and registration from the base")
    public void testDeleteEventMeetup() {

        Meetup meetup = createNewMeetup();
        entityManager.persist(meetup);

        Meetup foundMeetup = entityManager
                .find(Meetup.class, meetup.getId());
        repository.delete(foundMeetup);

        Meetup deletedMeetup = entityManager
                .find(Meetup.class, meetup.getId());

        Registration deleteRegistration = entityManager
                .find(Registration.class, meetup.getId());

        CreateMeetup deletedEvent = entityManager
                .find(CreateMeetup.class, meetup.getId());

        assertThat(deleteRegistration).isNull();
        assertThat(deletedEvent).isNull();
        assertThat(deletedMeetup).isNull();
    }
    @Test
    @DisplayName("Should return false when doesn't exists Meetup")
    public void testReturnFalseWhenMeetupDoesntExists(){
        Long id = 11L;

        boolean exists = repository.existsById(id);

        assertThat(exists).isFalse();
    }

    private Meetup createNewMeetup() {
        Meetup meetup = Meetup.builder().id(11l).build();
        return Meetup.builder()
                .meetupDateRegistration(LocalDate.now())
                .build();
    }
}
