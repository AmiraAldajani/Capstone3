package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.Entity.MatchStatus;
import com.example.ejadwebapplication.Entity.ReportMatch;
import com.example.ejadwebapplication.Repository.ReportMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportMatchService {

    private final ReportMatchRepository reportMatchRepository;

    public List<ReportMatch> getAllMatches() {
        return reportMatchRepository.findAll();
    }

    public ReportMatch getMatchById(Integer id) {
        ReportMatch match = reportMatchRepository.findReportMatchById(id);
        if (match == null) {
            throw new ApiException("Match not found with ID: " + id);
        }
        return match;
    }

    public List<ReportMatch> getMatchesByStatus(MatchStatus status) {
        return reportMatchRepository.findAllByStatus(status);
    }

    public void addMatch(ReportMatch match) {
        // بعد Report: نتحقق إن lostReport نوعه LOST و foundReport نوعه FOUND وإن الزوج ما تكرر
        reportMatchRepository.save(match);
    }

    @Transactional
    public void confirmMatch(Integer id) {
        ReportMatch match = getMatchById(id);
        checkIsSuggested(match);

        match.setStatus(MatchStatus.CONFIRMED);
        // بعد Report: نحوّل البلاغين لـ MATCHED ونرسل إشعار MATCH_CONFIRMED
        reportMatchRepository.save(match);
    }

    public void rejectMatch(Integer id) {
        ReportMatch match = getMatchById(id);
        checkIsSuggested(match);

        match.setStatus(MatchStatus.REJECTED);
        reportMatchRepository.save(match);
    }

    public void deleteMatch(Integer id) {
        ReportMatch match = getMatchById(id);
        reportMatchRepository.delete(match);
    }

    private void checkIsSuggested(ReportMatch match) {
        if (match.getStatus() != MatchStatus.SUGGESTED) {
            throw new ApiException("Match is already " + match.getStatus());
        }
    }
}