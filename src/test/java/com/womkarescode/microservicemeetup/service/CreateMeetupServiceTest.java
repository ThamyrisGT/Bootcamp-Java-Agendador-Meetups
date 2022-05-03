package com.womkarescode.microservicemeetup.service;

import com.womkarescode.microservicemeetup.exception.BusinessException;
import com.womkarescode.microservicemeetup.model.entity.CreateMeetup;
import com.womkarescode.microservicemeetup.repository.CreateMeetupRepository;
import com.womkarescode.microservicemeetup.service.impl.CreateMeetupServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class CreateMeetupServiceTest {

    CreateMeetupService service;

    @MockBean
    CreateMeetupRepository repository;

    @BeforeEach
    public void setup(){
        this.service = new CreateMeetupServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save an event meetup")
    public void testSaveEventMeetup(){

        CreateMeetup eventMeetup = createEventMeetupValid();

        when(repository.save(eventMeetup)).thenReturn(createEventMeetupValid());

        CreateMeetup savedEvent = service.saveNewEventMeetup(eventMeetup);

        assertDoesNotThrow(()-> service.saveNewEventMeetup(eventMeetup));
        assertThat(savedEvent.getId()).isEqualTo(11L);
        assertThat(savedEvent.getEvent()).isEqualTo("Paletra - Microservice");
        assertThat(savedEvent.getHostedBy()).isEqualTo("Thamyris");
        assertThat(savedEvent.getEventDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Should not save an duplicated event meetup")
    public void testNotSaveDuplicatedEventMeetup(){

        CreateMeetup eventMeetup = createEventMeetupValid();
        when(repository.findByEvent(eventMeetup.getEvent())).thenReturn(Optional.of(eventMeetup));

        Throwable exception = Assertions.catchThrowable(() -> service.saveNewEventMeetup(eventMeetup));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Event already created");

        Mockito.verify(repository, Mockito.never()).save(eventMeetup);
    }

    @Test
    @DisplayName("Should get event meetup by id.")
    public void testGetEventMeetupById(){

        CreateMeetup eventMeetup = createEventMeetupValid();

        when(repository.findById(eventMeetup.getId())).thenReturn(Optional.of(eventMeetup));

        Optional<CreateMeetup> foundEvent = service.getEventById(eventMeetup.getId());

        assertThat(foundEvent.isPresent()).isTrue();
        assertThat(foundEvent.get().getId()).isEqualTo(11L);
        assertThat(foundEvent.get().getEvent()).isEqualTo("Paletra - Microservice");
        assertThat(foundEvent.get().getHostedBy()).isEqualTo("Thamyris");
        assertThat(foundEvent.get().getEventDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("When a event meetup does not exist, it should return empty.")
    public void testEventMeetupByIdNotFound(){
        Long id = 101L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<CreateMeetup> eventMeetup = service.getEventById(id);

        assertThat(eventMeetup.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should delete event")
    public void testDeleteEventMeetup(){
        CreateMeetup eventMeetup = createEventMeetupValid();
        when(repository.findByEvent(eventMeetup.getEvent())).thenReturn(Optional.of(eventMeetup));

        assertDoesNotThrow(()-> service.deleteEventMeetup(eventMeetup));

        Mockito.verify(repository, Mockito.times(1)).delete(eventMeetup);
    }

    @Test
    @DisplayName("Should not delete a non-existent event")
    public void testDeleteEventMeetupNotFound(){

        CreateMeetup eventMeetup = createEventMeetupValid();

        Throwable exception = Assertions.catchThrowable(() -> service.deleteEventMeetup(eventMeetup));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event id must exist");
        //assertThrows(IllegalArgumentException.class, () -> service.deleteEventMeetup(eventMeetup));

        Mockito.verify(repository, Mockito.never()).delete(eventMeetup);
    }
    @Test
    @DisplayName("Should update event meetup")
    public void testUpdateEventMeetup(){

        long id = 11L;

        CreateMeetup updatingEventMeetup = CreateMeetup.builder().id(id).build();

        CreateMeetup updatedEventMeetup = createEventMeetupValid();
        updatedEventMeetup.setId(id);
        when(repository.save(updatingEventMeetup)).thenReturn(updatedEventMeetup);

        CreateMeetup eventMeetup = service.updateEventMeetup(updatingEventMeetup);

        assertDoesNotThrow(()-> service.updateEventMeetup(updatingEventMeetup));
        assertThat(eventMeetup.getId()).isEqualTo(updatedEventMeetup.getId());
        assertThat(eventMeetup.getEvent()).isEqualTo(updatedEventMeetup.getEvent());
        assertThat(eventMeetup.getHostedBy()).isEqualTo(updatedEventMeetup.getHostedBy());
        assertThat(eventMeetup.getEventDate()).isEqualTo(updatedEventMeetup.getEventDate());

    }

    @Test
    @DisplayName("Should throw Exception when trying to update a non-existent event")
    public void testUpdateEventMeetupNotFound(){
        CreateMeetup eventMeetup = new CreateMeetup();

        Throwable exception = Assertions.catchThrowable(() -> service.updateEventMeetup(eventMeetup));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event id must not be null");

        Mockito.verify(repository, Mockito.never()).save(eventMeetup);
    }

    @Test
    @DisplayName("Should find all meetup events")
    public void testFindAllEventMeetup(){

        CreateMeetup eventMeetup = createEventMeetupValid();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<CreateMeetup> listEvents = Arrays.asList(eventMeetup);
        Page<CreateMeetup> page = new PageImpl<CreateMeetup>(listEvents, pageRequest, 1);
        when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<CreateMeetup> result = service.findAllEventMeetup(eventMeetup, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(listEvents);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private CreateMeetup createEventMeetupValid(){
        return CreateMeetup.builder()
                .id(11L)
                .event("Paletra - Microservice")
                .linkMeetup("https://www.zoom.com/")
                .eventDate(LocalDate.now())
                .hostedBy("Thamyris")
                .guestSpeaker("Anna Nery")
                .build();
    }
}
