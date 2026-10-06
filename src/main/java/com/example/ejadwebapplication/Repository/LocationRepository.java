package com.example.ejadwebapplication.Repository;

import com.example.ejadwebapplication.Enums.LocationType;
import com.example.ejadwebapplication.Model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    Location findLocationById(Integer id);

    List<Location> findAllByCityIgnoreCase(String city);

    List<Location> findAllByType(LocationType type);
}
