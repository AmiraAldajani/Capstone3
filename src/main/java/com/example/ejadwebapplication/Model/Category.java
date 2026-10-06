package com.example.ejadwebapplication.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // unique لأن الـ AI يرجع اسم التصنيف ونطابقه بالاسم
    @Column(columnDefinition = "varchar(30) not null unique")
    private String name;

    @Column(columnDefinition = "varchar(200) not null")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private Set<Report> reports;
}