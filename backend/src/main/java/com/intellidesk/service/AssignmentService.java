package com.intellidesk.service;
import com.intellidesk.entity.*;
import com.intellidesk.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AssignmentService {
 private final WorkerRepository workers;
 private final TicketRepository tickets;

 public AssignmentService(WorkerRepository workers,TicketRepository tickets){this.workers=workers;this.tickets=tickets;}

 public Worker chooseWorker(String requiredRole,String requiredSkills){
  List<String> needed=Arrays.stream(requiredSkills.split(",")).map(String::trim).map(s->s.toLowerCase(Locale.ROOT)).toList();
  Worker best=null;
  double bestScore=Double.MAX_VALUE;

  for(Worker w:workers.findAll()){
   long active=tickets
           .countByAssignedWorkerUserIdAndStatusIn(w.getUserId(),
                   List.of(TicketStatus.ASSIGNED,TicketStatus.IN_PROGRESS,TicketStatus.OPEN,TicketStatus.ESCALATED));

   if(!w.isAvailable()||active>=w.getMaxWorkload())continue;

   String workerRole = w.getJobRole()
           .trim()
           .toLowerCase(Locale.ROOT);

   String ticketRole = requiredRole
           .trim()
           .toLowerCase(Locale.ROOT);

   boolean roleMatch =
           workerRole.equals(ticketRole)
                   || workerRole.equals("general support")
                   || ticketRole.contains(workerRole);
   System.out.println("Required Role: " + requiredRole);
   System.out.println("Worker Role: " + w.getJobRole());
   System.out.println("Worker Available: " + w.isAvailable());
   System.out.println("Worker Skills: " + w.getSkills());
   System.out.println("Required Skills: " + requiredSkills);
   if(!roleMatch)continue;

   int matches=0;
   for(String skill:w.getSkills())
    for(String need:needed)
     if(skill.equalsIgnoreCase(need)||skill.toLowerCase(Locale.ROOT).contains(need)||need.contains(skill.toLowerCase(Locale.ROOT)))
      matches++;

   // Lower score is better: skill gaps dominate, then workload ratio.
   double score=(needed.size()-Math.min(matches,needed.size()))*10.0+((double)active/w.getMaxWorkload());
   if(score<bestScore){bestScore=score;best=w;}
  }
  return best;
 }
}
