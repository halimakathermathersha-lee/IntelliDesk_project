package com.intellidesk.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="ai_predictions")
public class AiPrediction {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="ticket_id") private Ticket ticket;
 @Column(nullable=false) private String provider;
 @Column(nullable=false) private String category;
 @Column(nullable=false) private String priority;
 @Column(nullable=false) private String severity;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 public AiPrediction(){}
 public AiPrediction(Ticket t,String provider,String category,String priority,String severity){ticket=t;this.provider=provider;this.category=category;this.priority=priority;this.severity=severity;}
}
