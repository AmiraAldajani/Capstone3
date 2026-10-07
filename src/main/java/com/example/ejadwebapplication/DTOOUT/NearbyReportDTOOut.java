package com.example.ejadwebapplication.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;

// بلاغ موجود + بعده عن أقرب مكان ضياع بالكيلو
@Data
@AllArgsConstructor
public class NearbyReportDTOOut {
    private Double distanceKm;
    private ReportDTOOut report;
}
