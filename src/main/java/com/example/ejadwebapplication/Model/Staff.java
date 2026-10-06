package com.example.ejadwebapplication.Model;

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
public class Staff extends BaseAccount {

    @Column(columnDefinition = "boolean not null default false")
    private Boolean isVerified = false;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    // TODO: فعّلها لما يرفع العضو 2 كلاس Report و Notification
     @OneToMany(mappedBy = "staff")
     @JsonIgnore
     private Set<Report> reports;

    // @OneToMany(mappedBy = "staff")
    // @JsonIgnore
    // private Set<Notification> notifications;
}
