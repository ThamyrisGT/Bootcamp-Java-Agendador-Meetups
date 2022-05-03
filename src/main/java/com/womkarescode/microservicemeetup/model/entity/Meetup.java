package com.womkarescode.microservicemeetup.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Meetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String event;

    @JoinColumn(name = "id_registration")
    @ManyToOne
    private Registration registration;

    @Column
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate meetupDateRegistration = LocalDate.now();

    @JoinColumn(name = "author_meetup")
    @ManyToOne
    private CreateMeetup eventDetails;

}
