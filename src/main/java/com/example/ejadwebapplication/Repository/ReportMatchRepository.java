package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Entity.MatchStatus;
import com.example.ejadwebapplication.Entity.ReportMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportMatchRepository extends JpaRepository<ReportMatch, Integer> {

    ReportMatch findReportMatchById(Integer id);

    List<ReportMatch> findAllByStatus(MatchStatus status);

    // بعد Report:
    //Boolean existsByLostReportAndFoundReport(Report lostReport, Report foundReport);
    //List<ReportMatch> findAllByLostReportOrFoundReport(Report lostReport, Report foundReport);
}