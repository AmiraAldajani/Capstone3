package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.Api.ApiResponse;
import com.example.ejadwebapplication.Entity.MatchStatus;
import com.example.ejadwebapplication.Entity.ReportMatch;
import com.example.ejadwebapplication.Service.ReportMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/match")
@RequiredArgsConstructor
public class ReportMatchController {

    private final ReportMatchService reportMatchService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllMatches() {
        return ResponseEntity.status(200).body(reportMatchService.getAllMatches());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMatchById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(reportMatchService.getMatchById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getMatchesByStatus(@PathVariable MatchStatus status) {
        return ResponseEntity.status(200).body(reportMatchService.getMatchesByStatus(status));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMatch(@RequestBody @Valid ReportMatch match) {
        reportMatchService.addMatch(match);
        return ResponseEntity.status(200).body(new ApiResponse("Match added successfully"));
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<?> confirmMatch(@PathVariable Integer id) {
        reportMatchService.confirmMatch(id);
        return ResponseEntity.status(200).body(new ApiResponse("Match confirmed successfully"));
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectMatch(@PathVariable Integer id) {
        reportMatchService.rejectMatch(id);
        return ResponseEntity.status(200).body(new ApiResponse("Match rejected successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMatch(@PathVariable Integer id) {
        reportMatchService.deleteMatch(id);
        return ResponseEntity.status(200).body(new ApiResponse("Match deleted successfully"));
    }
}