package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Enums.ReportType;
import com.example.ejadwebapplication.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    Report findReportById(Integer id);

    List<Report> findAllByUser(User user);

    List<Report> findAllByStaff(Staff staff);

    List<Report> findAllByStatus(ReportStatus status);

    List<Report> findAllByType(ReportType type);

    // locations عبارة عن Set، فنستخدم Containing
    List<Report> findAllByLocationsContaining(Location location);

    // للتحقق قبل الحذف
    Boolean existsByUser(User user);

    Boolean existsByStaff(Staff staff);

    Boolean existsByCategory(Category category);

    Boolean existsByLocationsContaining(Location location);

    // البلاغات المرشحة للمطابقة: النوع المعاكس + نفس التصنيف + مفتوحة + تشترك بمكان واحد على الأقل
    // distinct لأن البلاغ لو يشترك بأكثر من مكان بيتكرر في النتيجة
    @Query("select distinct r from Report r join r.locations l " +
            "where r.type = ?1 and r.category = ?2 and r.status = ?3 and l in ?4")
    List<Report> findMatchCandidates(ReportType type, Category category, ReportStatus status, Set<Location> locations);

    List<Report> findAllByTypeAndCategoryAndStatus(ReportType type, Category category, ReportStatus status);
}