package com.womkarescode.microservicemeetup.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateMeetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String event;

    @Column
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate eventDate;

    @Column
    private String hostedBy;

    @Column
    private String guestSpeaker;

    @Column
    private String linkMeetup;

    @OneToMany(mappedBy = "eventDetails")
    private List<Meetup> meetups;

}
