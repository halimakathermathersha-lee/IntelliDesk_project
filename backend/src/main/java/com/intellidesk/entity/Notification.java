package com.intellidesk.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="notifications")
public class Notification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="user_id") private AppUser user;
 @Column(nullable=false,columnDefinition="TEXT") private String message;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 @Column(nullable=false) private boolean readFlag=false;
 public Notification(){}
 public Notification(AppUser u,String m){user=u;message=m;}
 public Long getId(){return id;} public AppUser getUser(){return user;} public String getMessage(){return message;} public LocalDateTime getCreatedAt(){return createdAt;} public boolean isReadFlag(){return readFlag;}
}
