package com.womkarescode.microservicemeetup.repository;

import com.womkarescode.microservicemeetup.model.entity.CreateMeetup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreateMeetupRepository  extends JpaRepository<CreateMeetup, Long> {
    Optional<CreateMeetup> findByEvent(String event);
}
