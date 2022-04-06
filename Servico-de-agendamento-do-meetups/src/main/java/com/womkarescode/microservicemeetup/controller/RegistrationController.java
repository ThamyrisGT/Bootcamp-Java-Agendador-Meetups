package com.womkarescode.microservicemeetup.controller;

import com.womkarescode.microservicemeetup.model.Registration;
import com.womkarescode.microservicemeetup.model.RegistrationDTO;
import com.womkarescode.microservicemeetup.service.RegistrationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService service;

    @Autowired
    private ModelMapper modelMapper;

    public RegistrationController(RegistrationService registrationService, ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.service = registrationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationDTO create (@RequestBody @Valid RegistrationDTO registrationDTO){

        Registration entity = modelMapper.map(registrationDTO, Registration.class);
        entity = service.save(entity);

        return modelMapper.map(entity,RegistrationDTO.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RegistrationDTO getRegistration(@PathVariable Long id){
        return service.getRegistrationById(id)
                .map(registration -> modelMapper.map(registration,RegistrationDTO.class))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByRegistrationId(@PathVariable Long id) {
        Registration registration = service.getRegistrationById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(registration);
    }

    @PutMapping("{id}")
    public RegistrationDTO update(@PathVariable Long id, RegistrationDTO registrationDTO) {

        return service.getRegistrationById(id).map(registration -> {
            registration.setName(registrationDTO.getName());
            registration.setDateOfRegistration(registrationDTO.getDateOfRegistration());
            registration = service.update(registration);

            return modelMapper.map(registration, RegistrationDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

}
