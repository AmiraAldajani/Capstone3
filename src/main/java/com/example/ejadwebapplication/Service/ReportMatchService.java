package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTO.MatchResultDTO;
import com.example.ejadwebapplication.DTOIN.ReportMatchDTOIn;
import com.example.ejadwebapplication.DTOOUT.ReportMatchDTOOut;
import com.example.ejadwebapplication.Enums.MatchStatus;
import com.example.ejadwebapplication.Enums.NotificationType;
import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Enums.ReportType;
import com.example.ejadwebapplication.Model.Report;
import com.example.ejadwebapplication.Model.ReportMatch;
import com.example.ejadwebapplication.Repository.ReportMatchRepository;
import com.example.ejadwebapplication.Repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportMatchService {

    // أقل نسبة تشابه عشان نحفظ التطابق
    private static final double MATCH_THRESHOLD = 70.0;

    private final ReportMatchRepository reportMatchRepository;
    private final ReportRepository reportRepository;
    private final NotificationService notificationService;
    private final AiService aiService;

    public List<ReportMatchDTOOut> getAllMatches() {
        return convertListToDTO(reportMatchRepository.findAll());
    }

    public ReportMatchDTOOut getMatchById(Integer id) {
        return convertToDTO(findMatch(id));
    }

    public List<ReportMatchDTOOut> getMatchesByStatus(MatchStatus status) {
        return convertListToDTO(reportMatchRepository.findAllByStatus(status));
    }

    public List<ReportMatchDTOOut> getMatchesByReport(Integer reportId) {
        Report report = findReport(reportId);
        return convertListToDTO(reportMatchRepository.findAllByLostReportOrFoundReport(report, report));
    }

    // إضافة يدوية (مثلاً الأدمن أو للتجربة بدون AI)
    @Transactional
    public void addMatch(ReportMatchDTOIn dto) {
        Report lostReport = findReport(dto.getLostReportId());
        Report foundReport = findReport(dto.getFoundReportId());

        if (lostReport.getType() != ReportType.LOST) {
            throw new ApiException("lostReportId must belong to a LOST report");
        }
        if (foundReport.getType() != ReportType.FOUND) {
            throw new ApiException("foundReportId must belong to a FOUND report");
        }
        if (lostReport.getStatus() != ReportStatus.OPEN || foundReport.getStatus() != ReportStatus.OPEN) {
            throw new ApiException("Both reports must be open");
        }
        if (reportMatchRepository.existsByLostReportAndFoundReport(lostReport, foundReport)) {
            throw new ApiException("This match already exists");
        }

        saveMatch(lostReport, foundReport, dto.getSimilarityScore(), dto.getAiReason());
    }

    // يُستدعى من ReportService بعد حفظ أي بلاغ جديد
    // فشل الـ AI ما يفشّل حفظ البلاغ، نسجّل الخطأ ونكمل
    public void findMatchesForReport(Report report) {
        ReportType oppositeType = report.getType() == ReportType.LOST ? ReportType.FOUND : ReportType.LOST;

        List<Report> candidates = reportRepository.findMatchCandidates(
                oppositeType, report.getCategory(), ReportStatus.OPEN, report.getLocations());
        if (candidates.isEmpty()) {
            return;
        }

        List<MatchResultDTO> results;
        try {
            results = aiService.compareReports(report, candidates);
        } catch (Exception e) {
            log.warn("AI matching failed for report {}: {}", report.getId(), e.getMessage());
            return;
        }

        for (MatchResultDTO result : results) {
            if (result.getScore() == null || result.getScore() < MATCH_THRESHOLD) {
                continue;
            }
            // نتأكد إن الـ id اللي رجّعه الـ AI فعلاً من المرشحين
            Report other = findInList(candidates, result.getReportId());
            if (other == null) {
                continue;
            }

            Report lostReport = report.getType() == ReportType.LOST ? report : other;
            Report foundReport = report.getType() == ReportType.FOUND ? report : other;
            if (reportMatchRepository.existsByLostReportAndFoundReport(lostReport, foundReport)) {
                continue;
            }

            saveMatch(lostReport, foundReport, Math.min(result.getScore(), 100.0), result.getReason());
        }
    }

    // تأكيد التطابق: الـ match يصير CONFIRMED والبلاغين MATCHED، كلها مع بعض أو ولا شي
    @Transactional
    public void confirmMatch(Integer id) {
        ReportMatch match = findMatch(id);
        checkIsSuggested(match);

        Report lostReport = match.getLostReport();
        Report foundReport = match.getFoundReport();
        if (lostReport.getStatus() != ReportStatus.OPEN || foundReport.getStatus() != ReportStatus.OPEN) {
            throw new ApiException("Both reports must be open to confirm the match");
        }

        match.setStatus(MatchStatus.CONFIRMED);
        lostReport.setStatus(ReportStatus.MATCHED);
        foundReport.setStatus(ReportStatus.MATCHED);
        reportMatchRepository.save(match);
        reportRepository.save(lostReport);
        reportRepository.save(foundReport);

        // باقي الاقتراحات لنفس البلاغين ما عاد لها داعي
        rejectOtherSuggestions(lostReport, match);
        rejectOtherSuggestions(foundReport, match);

        notificationService.notifyReportOwner(lostReport, NotificationType.MATCH_CONFIRMED,
                "Match confirmed for your report: " + lostReport.getTitle());
        notificationService.notifyReportOwner(foundReport, NotificationType.MATCH_CONFIRMED,
                "Match confirmed for your report: " + foundReport.getTitle());
    }

    public void rejectMatch(Integer id) {
        ReportMatch match = findMatch(id);
        checkIsSuggested(match);

        match.setStatus(MatchStatus.REJECTED);
        reportMatchRepository.save(match);
    }

    public void deleteMatch(Integer id) {
        ReportMatch match = findMatch(id);
        // لو انحذف وهو مؤكد، البلاغين يبقون MATCHED بدون سبب
        if (match.getStatus() == MatchStatus.CONFIRMED) {
            throw new ApiException("Cannot delete a confirmed match");
        }
        reportMatchRepository.delete(match);
    }

    // ================= Helpers =================

    private void saveMatch(Report lostReport, Report foundReport, Double score, String reason) {
        ReportMatch match = new ReportMatch();
        match.setLostReport(lostReport);
        match.setFoundReport(foundReport);
        match.setSimilarityScore(score);
        if (reason != null && reason.length() > 500) {
            reason = reason.substring(0, 500);
        }
        match.setAiReason(reason);
        reportMatchRepository.save(match);

        notificationService.notifyReportOwner(lostReport, NotificationType.MATCH_FOUND,
                "Possible match found for your report: " + foundReport.getTitle());
        notificationService.notifyReportOwner(foundReport, NotificationType.MATCH_FOUND,
                "Possible match found for your report: " + lostReport.getTitle());
    }

    private void rejectOtherSuggestions(Report report, ReportMatch confirmed) {
        for (ReportMatch match : reportMatchRepository.findAllByLostReportOrFoundReport(report, report)) {
            if (!match.getId().equals(confirmed.getId()) && match.getStatus() == MatchStatus.SUGGESTED) {
                match.setStatus(MatchStatus.REJECTED);
                reportMatchRepository.save(match);
            }
        }
    }

    private Report findInList(List<Report> reports, Integer id) {
        for (Report report : reports) {
            if (report.getId().equals(id)) {
                return report;
            }
        }
        return null;
    }

    private Report findReport(Integer id) {
        Report report = reportRepository.findReportById(id);
        if (report == null) {
            throw new ApiException("Report not found with ID: " + id);
        }
        return report;
    }

    private ReportMatch findMatch(Integer id) {
        ReportMatch match = reportMatchRepository.findReportMatchById(id);
        if (match == null) {
            throw new ApiException("Match not found with ID: " + id);
        }
        return match;
    }

    private void checkIsSuggested(ReportMatch match) {
        if (match.getStatus() != MatchStatus.SUGGESTED) {
            throw new ApiException("Match is already " + match.getStatus());
        }
    }

    private List<ReportMatchDTOOut> convertListToDTO(List<ReportMatch> matches) {
        List<ReportMatchDTOOut> result = new ArrayList<>();
        for (ReportMatch match : matches) {
            result.add(convertToDTO(match));
        }
        return result;
    }

    private ReportMatchDTOOut convertToDTO(ReportMatch match) {
        return new ReportMatchDTOOut(match.getId(), match.getSimilarityScore(), match.getAiReason(),
                match.getStatus().name(), match.getCreatedAt(),
                match.getLostReport().getId(), match.getLostReport().getTitle(),
                match.getFoundReport().getId(), match.getFoundReport().getTitle());
    }
}