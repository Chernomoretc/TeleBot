package ru.chernomoretc.telegrambot.entity;


import lombok.Data;
import org.hibernate.annotations.Type;
import ru.chernomoretc.telegrambot.enumShift.Role;

import javax.persistence.*;

@Entity(name = "user")
@Table(name = "users")
@Data

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "full_Name")
    private String fullName;

    @Column(name = "chat_id")
    private Long chatId;

    @Enumerated(EnumType.STRING)
    @Type(type = "role")
    @Column(columnDefinition = "role")
    private Role role;
}
