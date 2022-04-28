package com.womkarescode.microservicemeetup.controller.resource;

import com.womkarescode.microservicemeetup.controller.form.RegistrationForm;
import com.womkarescode.microservicemeetup.model.dto.RegistrationDTO;
import com.womkarescode.microservicemeetup.model.entity.Registration;
import com.womkarescode.microservicemeetup.service.RegistrationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
    public RegistrationDTO create (@RequestBody @Valid RegistrationForm registrationForm){

        Registration registration = modelMapper.map(registrationForm, Registration.class);
        registration = service.save(registration);
        return modelMapper.map(registration, RegistrationDTO.class);
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
    public RegistrationDTO update(@PathVariable Long id, RegistrationForm registrationForm) {

        return service.getRegistrationById(id).map(registration -> {
            registration.setName(registrationForm.getName());
            registration.setEmail(registrationForm.getEmail());
            registration = service.update(registration);

            return modelMapper.map(registration, RegistrationDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @GetMapping
    public Page<RegistrationDTO> find(RegistrationDTO registrationDTO , Pageable pageble){
        Registration filter = modelMapper.map(registrationDTO,Registration.class);
        Page<Registration> result = service.find(filter,pageble);

        List<RegistrationDTO> list = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity,RegistrationDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<RegistrationDTO>(list,pageble,result.getTotalElements());
    }

}
