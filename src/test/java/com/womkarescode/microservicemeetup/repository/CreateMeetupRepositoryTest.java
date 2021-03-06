package com.womkarescode.microservicemeetup.repository;

import com.womkarescode.microservicemeetup.model.entity.CreateMeetup;
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
public class CreateMeetupRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CreateMeetupRepository repository;


    @Test
    @DisplayName("Should return event by id")
    public void testFindEventById(){

        CreateMeetup eventMeetup = createNewEvent("Palestra - Microservice");
        entityManager.persist(eventMeetup);

        Optional<CreateMeetup> foundEvent = repository.findById(eventMeetup.getId());

        assertThat(foundEvent.isPresent()).isTrue();
    }
    @Test
    @DisplayName("Should return event by event name")
    public void testFindyByEvent(){
        CreateMeetup eventMeetup = createNewEvent("Palestra - Microservice");
        entityManager.persist(eventMeetup);

        Optional<CreateMeetup> foundEvent = repository.findByEvent(eventMeetup.getEvent());

        assertThat(foundEvent.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should save event")
    public void testSaveEventMeetup() {

        CreateMeetup newEvent = createNewEvent("Palestra - Microservice");

        CreateMeetup savedRegistration = repository.save(newEvent);

        assertThat(savedRegistration.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should update event by id")
    public void testUpdateEventById(){

        CreateMeetup eventMeetup = createNewEvent("Palestra - Microservice");
        entityManager.persist(eventMeetup);

        Optional<CreateMeetup> foundEvent = repository.findById(eventMeetup.getId());
        CreateMeetup savedRegistration = repository.save(eventMeetup);

        assertThat(savedRegistration.getId()).isNotNull();
        assertThat(foundEvent.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should delete event and registration from the base")
    public void testDeleteEventMeetup() {

        CreateMeetup event = createNewEvent("Palestra - Microservice");
        entityManager.persist(event);

        CreateMeetup foundEvent = entityManager
                .find(CreateMeetup.class, event.getId());
        repository.delete(foundEvent);

        Registration deleteRegistration = entityManager
                .find(Registration.class, event.getId());

        CreateMeetup deletedMeetup = entityManager
                .find(CreateMeetup.class, event.getId());

        assertThat(deleteRegistration).isNull();
        assertThat(deletedMeetup).isNull();
    }

    @Test
    @DisplayName("Should return false when doesn't exists Event by id")
    public void testReturnFalseWhenEventDoesntExistsById(){
        Long id = 11L;

        boolean exists = repository.existsById(id);

        assertThat(exists).isFalse();
    }
    @Test
    @DisplayName("Shouldn't return event when doesn't exists Event by event name")
    public void testReturnFalseWhenEventDoesntExistsByEventName(){
        String eventName = "Palestra - Microservice";

        Optional<CreateMeetup> nonCreateMeetup = repository.findByEvent(eventName);

        assertThat(nonCreateMeetup.isPresent()).isFalse();
    }


    public static CreateMeetup createNewEvent(String event) {
        return CreateMeetup.builder()
                .event(event)
                .guestSpeaker("Anna Neri")
                .eventDate(LocalDate.now())
                .build();
    }
}
