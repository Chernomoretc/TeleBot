package ru.chernomoretc.telegrambot.entity;

import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import ru.chernomoretc.telegrambot.enumShift.AdminStatusShift;

import javax.persistence.*;
import java.util.Date;


@Entity(name = "shift")
@Table(name = "shifts")
@Data

public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "open")
    private Date open;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "close")
    private Date close;

    @Column(name = "current_open")
    private Date currentOpen;

    @Column(name = "current_close")
    private Date currentClose;
    @Column(name = "work_hours")
    private double workHours = 0;
    @Enumerated(EnumType.STRING)
    @Type(type = "status_shift")
    @Column(columnDefinition = "status")
    private AdminStatusShift status;

    @Column (name = "comment")
    private String comment;


    public void setOpen(Date open) {
        if (this.open==null)
        {
            this.open = open;
            this.currentOpen = open;
            if (open.getHours() > 9 & open.getMinutes() > 10) {
                status = AdminStatusShift.BEING_LATE;
            } else {
                status = AdminStatusShift.OK;
            }
        }else {
            if (this.currentOpen==null)
            {
                this.currentOpen = open;
            }

        }

    }

    public void setClose(Date close) {
        if (this.close==null){
            this.close = close;
            this.currentClose = close;
            try {
                    workHours = ((currentClose.getTime() - currentOpen.getTime()) / 3600000.0);

            } catch (NullPointerException e) {
                workHours = 0.0;
            }
            this.currentOpen = null;
            this.currentClose = null;
        }else
        {
            if (this.close.getTime()<close.getTime())
            {
                this.close = close;
            }
            this.currentClose = close;
            workHours = workHours + ((currentClose.getTime() - currentOpen.getTime()) / 3600000.0);
            this.currentOpen = null;
            this.currentClose = null;
        }
    }

    public void setSickLeave(Boolean sickLeave) {
        status = AdminStatusShift.SICK_LEAVE;

    }

    public void setVacation(Boolean vacation) {
        status = AdminStatusShift.VACATION;
    }


}
