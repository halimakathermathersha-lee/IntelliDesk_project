package com.intellidesk.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="ticket_updates")
public class TicketUpdate {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="ticket_id")
 private Ticket ticket;
 @ManyToOne(optional=false) @JoinColumn(name="author_id")
 private AppUser author;
 @Column(nullable=false,columnDefinition="TEXT")
 private String message;
 @Column(nullable=false)
 private LocalDateTime createdAt=LocalDateTime.now();




 public TicketUpdate(){}
 public TicketUpdate(Ticket t,AppUser a,String m){ticket=t;author=a;message=m;}



 public Long getId(){return id;} public Ticket getTicket(){return ticket;} public AppUser getAuthor(){return author;} public String getMessage(){return message;} public LocalDateTime getCreatedAt(){return createdAt;}
}
