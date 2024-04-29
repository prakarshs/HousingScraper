package com.crick.apis.controllers;

import com.crick.apis.services.HousingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/housing")
@CrossOrigin("*")
public class HousingController {

    private HousingService housingService;

    public HousingController(HousingService housingService) {
        this.housingService = housingService;
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllProperies() throws InterruptedException {
        System.out.println("getting properties");
        return new ResponseEntity<>(this.housingService.getProperties(), HttpStatus.OK);
    }
}
