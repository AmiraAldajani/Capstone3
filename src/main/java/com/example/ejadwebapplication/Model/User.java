package com.example.ejadwebapplication.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseAccount {


    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Report> reports;

     @OneToMany(mappedBy = "user")
     @JsonIgnore
     private Set<Notification> notifications;
}
