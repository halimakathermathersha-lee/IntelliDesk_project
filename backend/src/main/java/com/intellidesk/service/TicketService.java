package com.intellidesk.service;

import com.intellidesk.ai.ClassificationService;
import com.intellidesk.ai.ClassificationService.Prediction;
import com.intellidesk.entity.*;
import com.intellidesk.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TicketService {
 private final TicketRepository tickets;
 private final UserRepository users;
 private final WorkerRepository workers;
 private final TicketUpdateRepository updates;
 private final TicketHistoryRepository history;
 private final AiPredictionRepository predictions;
 private final AttachmentRepository attachments;
 private final NotificationRepository notifications;
 private final ClassificationService classifier;
 private final AssignmentService assignment;

 public TicketService(TicketRepository tickets,UserRepository users,WorkerRepository workers,TicketUpdateRepository updates,TicketHistoryRepository history,AiPredictionRepository predictions,AttachmentRepository attachments,NotificationRepository notifications,ClassificationService classifier,AssignmentService assignment){
  this.tickets=tickets;this.users=users;this.workers=workers;this.updates=updates;this.history=history;this.predictions=predictions;this.attachments=attachments;this.notifications=notifications;this.classifier=classifier;this.assignment=assignment;
 }

 public AppUser current(String email){
  return users.findByEmail(email).orElseThrow(()->new IllegalArgumentException("User not found"));
 }

 public List<Map<String,Object>> list(AppUser user){
  List<Ticket> list=user.getRole()==Role.ADMIN?tickets.findAllByOrderByCreatedAtDesc():user.getRole()==Role.WORKER?tickets.findByAssignedWorkerUserIdOrderByCreatedAtDesc(user.getId()):tickets.findByCreatedByIdOrderByCreatedAtDesc(user.getId());
  return list.stream().map(this::view).toList();
 }

 public Map<String,Object> detail(Long id,AppUser user){
  return view(allowedTicket(id,user));
 }
 @Transactional
 public Map<String,Object> create(String description,String fileName,String contentType,long size,AppUser user){

  if(description==null||description.trim().length()<10)throw new IllegalArgumentException("Please provide at least 10 characters describing the issue");
  if(description.length()>10000)throw new IllegalArgumentException("Description is too long");
  Prediction p=classifier.classify(description);


  // Validate classifier output against known safe values before persistence.

  if(!Set.of("GENERAL","APPLICATION","HARDWARE","ACCESS","NETWORK","EMAIL").contains(p.category()))
   throw new IllegalArgumentException("Invalid predicted category");
  if(!Set.of("LOW","MEDIUM","HIGH","CRITICAL").contains(p.priority()))
   throw new IllegalArgumentException("Invalid predicted priority");

  Ticket t=new Ticket();
  t.setCreatedBy(user);
  t.setTitle(p.title());
  t.setDescription(description.trim());
  t.setCategory(p.category());
  t.setPriority(p.priority());
  t.setSeverity(p.severity());
  t.setRequiredRole(p.requiredRole());
  t.setRequiredSkills(p.requiredSkills());

  int hours=switch(p.priority()){case "CRITICAL"->2;case "HIGH"->4;case "MEDIUM"->8;default->24;};
  t.setSlaDeadline(LocalDateTime.now().plusHours(hours));

  Worker worker=assignment.chooseWorker(p.requiredRole(),p.requiredSkills());

  if(worker!=null){
   t.setAssignedWorker(worker);
   t.setStatus(TicketStatus.ASSIGNED);
  }
  else{
   t.setStatus(TicketStatus.OPEN);
  }

  t=tickets.save(t);

  predictions.save(new AiPrediction(t,p.provider(),p.category(),p.priority(),p.severity()));

  if(fileName!=null&&!fileName.isBlank())
   attachments.save(new Attachment(t,fileName,contentType==null?"application/octet-stream":contentType,size));

  history.save(new TicketHistory(t,user,"CREATED","Ticket created using "+p.provider()));

  if(worker==null){
   for(AppUser admin:users.findAll())
    if(admin.getRole()==Role.ADMIN)
     notifications.save(new Notification(admin,"Ticket #"+t.getId()+" has no suitable available worker and needs assignment."));
  }
  else
   notifications.save(new Notification(worker.getUser(),"Ticket #"+t.getId()+" was assigned to you."));
  return view(t);
 }
 public Map<String,Object> addUpdate(Long id,String message,AppUser user){
  Ticket t=allowedTicket(id,user);
  if(user.getRole()==Role.USER)
   throw new IllegalArgumentException("Only workers or admins can add progress updates");

  if(message==null||message.isBlank())
   throw new IllegalArgumentException("Update message is required");
  updates.save(new TicketUpdate(t,user,message.trim()));

  if(t.getStatus()==TicketStatus.ASSIGNED||t.getStatus()==TicketStatus.OPEN)
   t.setStatus(TicketStatus.IN_PROGRESS);

  tickets.save(t);history.save(new TicketHistory(t,user,"PROGRESS_UPDATE",message.trim()));
  return view(t);
 }

 public Map<String,Object> resolve(Long id,String resolution,AppUser user){
  Ticket t=allowedTicket(id,user);if(user.getRole()==Role.USER)throw new IllegalArgumentException("Only workers or admins can resolve tickets");
  if(t.getStatus()==TicketStatus.CLOSED)throw new IllegalArgumentException("Closed tickets cannot be resolved again");
  if(resolution==null||resolution.isBlank())throw new IllegalArgumentException("Resolution details are required");
  t.setResolution(resolution.trim());t.setStatus(TicketStatus.RESOLVED);tickets.save(t);history.save(new TicketHistory(t,user,"RESOLVED",resolution.trim()));
  notifications.save(new Notification(t.getCreatedBy(),"Ticket #"+t.getId()+" has been marked resolved. Please confirm or reopen it."));
  return view(t);
 }
 public Map<String,Object> confirm(Long id,AppUser user){Ticket t=allowedTicket(id,user);if(user.getRole()!=Role.USER)throw new IllegalArgumentException("Only the employee can confirm resolution");if(t.getStatus()!=TicketStatus.RESOLVED)throw new IllegalArgumentException("Only resolved tickets can be closed");t.setStatus(TicketStatus.CLOSED);tickets.save(t);history.save(new TicketHistory(t,user,"CLOSED","Employee confirmed resolution"));return view(t);}
 public Map<String,Object> reopen(Long id,AppUser user){Ticket t=allowedTicket(id,user);if(user.getRole()!=Role.USER)throw new IllegalArgumentException("Only the employee can reopen a ticket");if(t.getStatus()!=TicketStatus.RESOLVED&&t.getStatus()!=TicketStatus.CLOSED)throw new IllegalArgumentException("Only resolved or closed tickets can be reopened");t.setStatus(t.getAssignedWorker()==null?TicketStatus.OPEN:TicketStatus.ASSIGNED);t.setResolution(null);tickets.save(t);history.save(new TicketHistory(t,user,"REOPENED","Employee reopened issue"));return view(t);}
 public Map<String,Object> escalate(Long id,AppUser user){if(user.getRole()!=Role.ADMIN)throw new IllegalArgumentException("Admin only");Ticket t=tickets.findById(id).orElseThrow(()->new IllegalArgumentException("Ticket not found"));t.setStatus(TicketStatus.ESCALATED);if(!"CRITICAL".equals(t.getPriority()))t.setPriority("CRITICAL");t.setSlaDeadline(LocalDateTime.now().plusHours(2));tickets.save(t);history.save(new TicketHistory(t,user,"ESCALATED","Admin escalated ticket"));return view(t);}

 @Transactional
 public Map<String, Object> assignWorker(
         Long ticketId,
         Long workerId,
         AppUser admin) {

  // Only admin can assign workers
  if (admin.getRole() != Role.ADMIN) {
   throw new IllegalArgumentException("Admin only");
  }

  // Find the ticket
  Ticket ticket = tickets.findById(ticketId)
          .orElseThrow(() ->
                  new IllegalArgumentException("Ticket not found"));

  // Closed tickets cannot be assigned
  if (ticket.getStatus() == TicketStatus.CLOSED ||
          ticket.getStatus() == TicketStatus.RESOLVED) {
   throw new IllegalArgumentException(
           "Cannot assign a closed or resolved ticket");
  }

  // Find the selected worker
  Worker worker = workers.findById(workerId)
          .orElseThrow(() ->
                  new IllegalArgumentException("Worker not found"));

  // Check worker availability
  if (!worker.isAvailable()) {
   throw new IllegalArgumentException(
           "Selected worker is not available");
  }

  // Check worker workload
  long activeTickets =
          tickets.countByAssignedWorkerUserIdAndStatusIn(
                  worker.getUserId(),
                  List.of(
                          TicketStatus.ASSIGNED,
                          TicketStatus.IN_PROGRESS,
                          TicketStatus.OPEN,
                          TicketStatus.ESCALATED
                  )
          );

  if (activeTickets >= worker.getMaxWorkload()) {
   throw new IllegalArgumentException(
           "Selected worker has reached maximum workload");
  }

  // Assign worker to ticket
  ticket.setAssignedWorker(worker);
  ticket.setStatus(TicketStatus.ASSIGNED);

  tickets.save(ticket);

  // Save assignment history
  history.save(new TicketHistory(
          ticket,
          admin,
          "ASSIGNED",
          "Admin assigned ticket to " + worker.getUser().getName()
  ));

  // Notify the selected worker
  notifications.save(new Notification(
          worker.getUser(),
          "Ticket #" + ticket.getId() + " was assigned to you."
  ));

  return view(ticket);
 }


 public Ticket allowedTicket(Long id,AppUser user){
  Ticket t=tickets.findById(id).orElseThrow(()->new IllegalArgumentException("Ticket not found"));
  boolean allowed=user.getRole()==Role.ADMIN||(user.getRole()==Role.USER&&t.getCreatedBy().getId().equals(user.getId()))||(user.getRole()==Role.WORKER&&t.getAssignedWorker()!=null&&t.getAssignedWorker().getUserId().equals(user.getId()));
  if(!allowed)throw new org.springframework.security.access.AccessDeniedException("You cannot access this ticket");
  return t;
 }
 public Map<String,Object> view(Ticket t){
  Map<String,Object> m=new LinkedHashMap<>();m.put("id",t.getId());m.put("title",t.getTitle());m.put("description",t.getDescription());m.put("category",t.getCategory());m.put("priority",t.getPriority());m.put("severity",t.getSeverity());m.put("requiredRole",t.getRequiredRole());m.put("requiredSkills",t.getRequiredSkills());m.put("status",t.getStatus().name());m.put("createdAt",t.getCreatedAt());m.put("slaDeadline",t.getSlaDeadline());m.put("slaBreached",t.isSlaBreached());m.put("adminOverridden",t.isAdminOverridden());m.put("resolution",t.getResolution());m.put("createdByName",t.getCreatedBy().getName());m.put("assignedWorkerName",t.getAssignedWorker()==null?null:t.getAssignedWorker().getUser().getName());
  m.put("updates",updates.findByTicketIdOrderByCreatedAtAsc(t.getId()).stream().map(u->Map.of("id",u.getId(),"message",u.getMessage(),"authorName",u.getAuthor().getName(),"createdAt",u.getCreatedAt())).toList());return m;
 }
}
