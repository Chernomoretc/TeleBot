package ru.chernomoretc.telegrambot.entity;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

//
@Entity(name = "location_user")
@Data
@Table(name = "locations_users")


public class LocationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

//    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;


    @Column(name = "date_loc")
    private Date dateLoc;

    @Column(name = "latitude")
    private Double latitude;


    @Column(name = "longitude")
    private Double longitude;

}
