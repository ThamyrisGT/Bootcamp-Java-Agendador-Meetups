package com.womkarescode.microservicemeetup.repository;

import com.womkarescode.microservicemeetup.model.entity.Meetup;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetupRepository extends JpaRepository<Meetup, Long> {

    @Query( value = " select meet from Meetup as meet join meet.registration as regis_meet where regis_meet.registration = :registration or meet.event =:event ")
    Page<Meetup> findByRegistrationOnMeetup(
            @Param("registration") String registration,
            @Param("event") String event,
            Pageable pageable
    );

    Page<Meetup> findByRegistration(Registration registration, Pageable pageable );

}
