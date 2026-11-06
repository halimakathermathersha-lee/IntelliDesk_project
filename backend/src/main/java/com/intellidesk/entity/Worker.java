package com.intellidesk.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="workers")
public class Worker {
    @Id private Long userId;
    @OneToOne @MapsId @JoinColumn(name="user_id")
    private AppUser user;

    @Column(nullable=false)
    private String jobRole = "General Support";

    @Column(nullable=false) private boolean available = true;
    @Column(nullable=false) private int maxWorkload = 5;
    @ElementCollection
    @CollectionTable(name="worker_skills", joinColumns=@JoinColumn(name="worker_id"))
    @Column(name="skill", nullable=false)
    private List<String> skills = new ArrayList<>();
    public Worker() {}
    public Worker(AppUser user){
        this.user=user;
        this.jobRole="General Support";
        this.available=true;
        this.maxWorkload=5;
    }


    public Long getUserId(){return userId;} public void setUserId(Long userId){this.userId=userId;}
    public AppUser getUser(){return user;} public void setUser(AppUser user){this.user=user;}

    public String getJobRole(){return jobRole;}
    public void setJobRole(String jobRole){this.jobRole=jobRole;}

    public boolean isAvailable(){return available;} public void setAvailable(boolean available){this.available=available;}
    public int getMaxWorkload(){return maxWorkload;} public void setMaxWorkload(int maxWorkload){this.maxWorkload=maxWorkload;}
    public List<String> getSkills(){return skills;} public void setSkills(List<String> skills){this.skills=skills;}
}
