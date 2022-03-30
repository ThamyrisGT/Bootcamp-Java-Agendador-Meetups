package com.womkarescode.microservicemeetup.repository;

import com.womkarescode.microservicemeetup.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByRegistration(String registration);
}
