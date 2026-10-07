package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Enums.ReportStatus;
import com.example.ejadwebapplication.Enums.ReportType;
import com.example.ejadwebapplication.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // ================= Extra endpoints =================

    // البحث بكلمة في العنوان أو الوصف
    List<Report> findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    List<Report> findAllByCategory(Category category);

    List<Report> findAllByItemDateBetween(LocalDate from, LocalDate to);

    List<Report> findAllByLocationsContainingAndStatus(Location location, ReportStatus status);

    // للإحصائيات
    Integer countByStatus(ReportStatus status);

    Integer countByCategory(Category category);

    Integer countByCategoryAndStatus(Category category, ReportStatus status);

    Integer countByLocationsContainingAndStatus(Location location, ReportStatus status);

    // ================= Extra endpoints 2 =================

    List<Report> findAllByUserAndStatus(User user, ReportStatus status);

    List<Report> findAllByColorContainingIgnoreCase(String color);

    List<Report> findAllByBrandContainingIgnoreCase(String brand);

    List<Report> findAllByCreatedAtAfter(LocalDateTime date);

    // البلاغات القديمة (مثلاً مفتوحة من أكثر من 30 يوم)
    List<Report> findAllByStatusAndCreatedAtBefore(ReportStatus status, LocalDateTime date);

    // المدينة موجودة في الـ Location، فنسوي join
    @Query("select distinct r from Report r join r.locations l where lower(l.city) = lower(?1)")
    List<Report> findAllByCity(String city);

    Boolean existsByStaffAndStatus(Staff staff, ReportStatus status);

    Integer countByUser(User user);

    Integer countByUserAndStatus(User user, ReportStatus status);

    Integer countByType(ReportType type);

    Integer countByLocationsContaining(Location location);

    // البلاغات المرشحة للمطابقة: النوع المعاكس + نفس التصنيف + مفتوحة + تشترك بمكان واحد على الأقل
    // distinct لأن البلاغ لو يشترك بأكثر من مكان بيتكرر في النتيجة
    @Query("select distinct r from Report r join r.locations l " +
            "where r.type = ?1 and r.category = ?2 and r.status = ?3 and l in ?4")
    List<Report> findMatchCandidates(ReportType type, Category category, ReportStatus status, Set<Location> locations);
}