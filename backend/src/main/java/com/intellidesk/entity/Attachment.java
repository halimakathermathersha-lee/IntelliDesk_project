package com.intellidesk.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="attachments")
public class Attachment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="ticket_id") private Ticket ticket;
 @Column(nullable=false) private String originalName;
 @Column(nullable=false) private String contentType;
 @Column(nullable=false) private long sizeBytes;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 public Attachment(){}
 public Attachment(Ticket t,String n,String c,long s){ticket=t;originalName=n;contentType=c;sizeBytes=s;}
}
