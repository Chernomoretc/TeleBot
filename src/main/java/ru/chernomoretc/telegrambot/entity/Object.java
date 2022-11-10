package ru.chernomoretc.telegrambot.entity;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "object")
@Table(name = "objects")
@Data
public class Object {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "object_name")
    private String objectName;
}
