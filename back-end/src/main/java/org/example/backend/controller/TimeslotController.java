package org.example.backend.controller;

import org.example.backend.dto.TimeslotDTO;
import org.example.backend.service.TimeslotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
public class TimeslotController {

    private final TimeslotService timeslotService;

    public TimeslotController(TimeslotService timeslotService) {
        this.timeslotService = timeslotService;
    }

    @GetMapping("/provider/all")
    public List<TimeslotDTO> getProviderTimeslots(Authentication auth) {
        String providerEmail = auth.getName();
        return timeslotService.getTimeslotsByProviderEmail(providerEmail);
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<TimeslotDTO>> getTimeslotsByActivityId(@PathVariable Long activityId) {
        try {
            List<TimeslotDTO> timeslots = timeslotService.getTimeslotsByActivityId(activityId);
            if (timeslots.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(timeslots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PostMapping("/activity/{activityId}")
    public ResponseEntity<TimeslotDTO> create(@PathVariable Long activityId, @RequestBody TimeslotDTO dto) {
        return ResponseEntity.ok(timeslotService.createTimeslot(activityId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeslotDTO> update(@PathVariable Long id, @RequestBody TimeslotDTO dto) {
        return ResponseEntity.ok(timeslotService.updateTimeslot(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        timeslotService.deleteTimeslot(id);
        return ResponseEntity.noContent().build();
    }
}