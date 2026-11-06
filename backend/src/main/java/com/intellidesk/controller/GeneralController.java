package com.intellidesk.controller;
import com.intellidesk.entity.*;
import com.intellidesk.repository.*;
import com.intellidesk.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class GeneralController {
 private final TicketService tickets;
 private final UserRepository users;
 private final WorkerRepository workers;
 private final TicketHistoryRepository history;
 private final NotificationRepository notifications;

       public GeneralController(TicketService tickets,UserRepository users,WorkerRepository workers,
                                TicketHistoryRepository history,NotificationRepository notifications){
        this.tickets=tickets;this.users=users;this.workers=workers;
        this.history=history;this.notifications=notifications;
       }


 private AppUser me(Authentication a){
        return tickets.current(a.getName());
       }
 @GetMapping("/api/users/me") public Map<String,Object> profile(Authentication a){

        AppUser u=me(a);
        return Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail(),"role",u.getRole().name());

       }
 @GetMapping("/api/notifications") public List<Map<String,Object>> notifications(Authentication a){
        return notifications.findByUserIdOrderByCreatedAtDesc(me(a).getId()).stream()
                .map(n->Map.<String,Object>of("id",n.getId(),"message",n.getMessage()
                        ,"createdAt",n.getCreatedAt(),"read",n.isReadFlag())).toList();
       }


 @Transactional(readOnly = true)
 @GetMapping("/api/admin/workers")
 public ResponseEntity<?> workerList(Authentication a) {

  if (me(a).getRole() != Role.ADMIN) {
   return ResponseEntity.status(403)
           .body(Map.of("message", "Admin only"));
  }

  List<Map<String, Object>> result = new ArrayList<>();

  for (Worker w : workers.findAll()) {

   Map<String, Object> worker = new LinkedHashMap<>();

   worker.put("id", w.getUserId());
   worker.put("name", w.getUser().getName());
   worker.put("email", w.getUser().getEmail());
   worker.put("jobRole", w.getJobRole());
   worker.put("available", w.isAvailable());
   worker.put("maxWorkload", w.getMaxWorkload());
   worker.put("skills", new ArrayList<>(w.getSkills()));

   result.add(worker);
  }

  return ResponseEntity.ok(result);
 }


 @GetMapping("/api/admin/sla") public ResponseEntity<?> sla(Authentication a){
        if(me(a).getRole()!=Role.ADMIN)
         return ResponseEntity.status(403)
                 .body(Map.of("message","Admin only"));
        return ResponseEntity.ok(tickets.list(me(a)).stream()
                .map(t->Map.of("id",t.get("id"),
                        "title",t.get("title"),
                        "priority",t.get("priority"),
                        "status",t.get("status"),"slaDeadline",String.valueOf(t.get("slaDeadline")),
                        "slaBreached",t.get("slaBreached"))).toList());

       }


 @GetMapping("/api/admin/history")
 public ResponseEntity<?> history(Authentication a){
        if(me(a).getRole()!=Role.ADMIN)
         return ResponseEntity.status(403)
                 .body(Map.of("message","Admin only"));
        return ResponseEntity.ok(history.findAllByOrderByCreatedAtDesc()
                .stream().map(h->Map.of("id",h.getId(),
                        "ticketId",h.getTicket().getId(),"actor",h.getActor().getName(),
                        "action",h.getAction(),"details",String.valueOf(h.getDetails()),
                        "createdAt",h.getCreatedAt())).toList());
       }
}
