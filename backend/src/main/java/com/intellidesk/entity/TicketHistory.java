package com.intellidesk.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="ticket_history")
public class TicketHistory {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="ticket_id")
 private Ticket ticket;
 @ManyToOne(optional=false) @JoinColumn(name="actor_id")
 private AppUser actor;
 @Column(nullable=false)
 private String action;
 @Column(columnDefinition="TEXT")
 private String details;
 @Column(nullable=false)
 private LocalDateTime createdAt=LocalDateTime.now();




 public TicketHistory(){}
 public TicketHistory(Ticket t,AppUser a,String action,
                      String details){
  ticket=t;actor=a;this.action=action;this.details=details;}




 public Long getId(){return id;} public Ticket getTicket(){return ticket;} public AppUser getActor(){return actor;} public String getAction(){return action;} public String getDetails(){return details;} public LocalDateTime getCreatedAt(){return createdAt;}
}
