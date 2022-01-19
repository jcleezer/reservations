package com.github.jcleezer.reservation.controller;

import com.github.jcleezer.reservation.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@RestController
public class VacancyRestController {

    private final VacancyService vacancyService;

    @Autowired
    public VacancyRestController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @GetMapping(path = "vacancy")
    public Flux<LocalDate> getVacancies(@RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        if (startDate == null){
            startDate = LocalDate.now();
        }
        if (endDate == null){
            endDate = startDate.plusMonths(1);
        }

        return vacancyService.getVacancies(startDate,endDate);
    }

}
