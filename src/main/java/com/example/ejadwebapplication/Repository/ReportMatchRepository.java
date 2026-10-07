package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Enums.MatchStatus;
import com.example.ejadwebapplication.Model.Location;
import com.example.ejadwebapplication.Model.Report;
import com.example.ejadwebapplication.Model.ReportMatch;
import com.example.ejadwebapplication.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportMatchRepository extends JpaRepository<ReportMatch, Integer> {

    ReportMatch findReportMatchById(Integer id);

    List<ReportMatch> findAllByStatus(MatchStatus status);

    Boolean existsByLostReportAndFoundReport(Report lostReport, Report foundReport);

    List<ReportMatch> findAllByLostReportOrFoundReport(Report lostReport, Report foundReport);

    // ================= Extra endpoints =================

    // كل التطابقات اللي أحد بلاغاتها يخص هذا المستخدم، الأعلى نسبة أول
    @Query("select m from ReportMatch m where m.lostReport.user = ?1 or m.foundReport.user = ?1 " +
            "order by m.similarityScore desc")
    List<ReportMatch> findAllByReportOwner(User user);

    Integer countByStatus(MatchStatus status);

    // ================= Extra endpoints 2 =================

    List<ReportMatch> findAllBySimilarityScoreGreaterThanEqualOrderBySimilarityScoreDesc(Double minScore);

    // التطابقات اللي الغرض الموجود (FOUND) فيها في هذا المكان
    @Query("select m from ReportMatch m join m.foundReport f join f.locations l " +
            "where l = ?1 order by m.similarityScore desc")
    List<ReportMatch> findAllByFoundReportLocation(Location location);
}