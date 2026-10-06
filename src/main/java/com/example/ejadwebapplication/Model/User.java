package com.example.ejadwebapplication.Model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseAccount {


    // @OneToMany(mappedBy = "user")
    // @JsonIgnore
    // private Set<Report> reports;

    // @OneToMany(mappedBy = "user")
    // @JsonIgnore
    // private Set<Notification> notifications;
}
