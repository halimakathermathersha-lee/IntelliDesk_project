package com.intellidesk.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    @JoinColumn(name="user_id")
    private AppUser createdBy;
    @ManyToOne @JoinColumn(name="worker_id")
    private Worker assignedWorker;
    @Column(nullable=false,length=500)
    private String title;
    @Column(nullable=false,columnDefinition="TEXT")
    private String description;
    @Column(nullable=false)
    private String category;
    @Column(nullable=false)
    private String priority;
    @Column(nullable=false)
    private String severity;
    @Column(nullable=false)
    private String requiredRole;
    @Column(nullable=false)
    private String requiredSkills;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private TicketStatus status=TicketStatus.OPEN;
    @Column(nullable=false)
    private LocalDateTime createdAt=LocalDateTime.now();
    private LocalDateTime slaDeadline;
    private boolean slaBreached=false;
    private boolean adminOverridden=false;
    private String resolution;

    public Ticket() {}

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public AppUser getCreatedBy(){return createdBy;} public void setCreatedBy(AppUser v){createdBy=v;}
    public Worker getAssignedWorker(){return assignedWorker;} public void setAssignedWorker(Worker v){assignedWorker=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public String getPriority(){return priority;} public void setPriority(String v){priority=v;}
    public String getSeverity(){return severity;} public void setSeverity(String v){severity=v;}
    public String getRequiredRole(){return requiredRole;} public void setRequiredRole(String v){requiredRole=v;}
    public String getRequiredSkills(){return requiredSkills;} public void setRequiredSkills(String v){requiredSkills=v;}
    public TicketStatus getStatus(){return status;} public void setStatus(TicketStatus v){status=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
    public LocalDateTime getSlaDeadline(){return slaDeadline;} public void setSlaDeadline(LocalDateTime v){slaDeadline=v;}
    public boolean isSlaBreached(){return slaBreached;} public void setSlaBreached(boolean v){slaBreached=v;}
    public boolean isAdminOverridden(){return adminOverridden;} public void setAdminOverridden(boolean v){adminOverridden=v;}
    public String getResolution(){return resolution;} public void setResolution(String v){resolution=v;}
}
