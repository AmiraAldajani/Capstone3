package com.example.ejadwebapplication.Model;


import com.example.ejadwebapplication.Enums.LocationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(100) not null")
    private String name;

    @Column(columnDefinition = "varchar(200)")
    private String description;

    @Column(columnDefinition = "varchar(30) not null")
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20) not null")
    private LocationType type;

    @OneToMany(mappedBy = "location")
    @JsonIgnore
    private Set<Staff> staff;

    // TODO: علاقة ManyToMany مع Report — المالك Report (العضو 2) بـ @JoinTable، وهذا الطرف mappedBy
    // @ManyToMany(mappedBy = "locations")
    // @JsonIgnore
    // private Set<Report> reports;
}
