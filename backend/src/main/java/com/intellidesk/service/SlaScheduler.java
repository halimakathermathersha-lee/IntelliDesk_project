package com.intellidesk.service;
import com.intellidesk.entity.Ticket;
import com.intellidesk.entity.TicketStatus;
import com.intellidesk.repository.TicketRepository;
import com.intellidesk.repository.TicketHistoryRepository;
import com.intellidesk.entity.TicketHistory;
import com.intellidesk.repository.UserRepository;
import com.intellidesk.entity.Role;
import com.intellidesk.repository.NotificationRepository;
import com.intellidesk.entity.Notification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class SlaScheduler {
 private final TicketRepository tickets; private final TicketHistoryRepository history; private final UserRepository users; private final NotificationRepository notifications;
 public SlaScheduler(TicketRepository tickets,TicketHistoryRepository history,UserRepository users,NotificationRepository notifications){this.tickets=tickets;this.history=history;this.users=users;this.notifications=notifications;}
 @Scheduled(fixedDelay=60000)
 public void checkSla(){
  for(Ticket t:tickets.findByStatusNot(TicketStatus.CLOSED)){
   if(!t.isSlaBreached()&&t.getSlaDeadline()!=null&&t.getSlaDeadline().isBefore(LocalDateTime.now())&&t.getStatus()!=TicketStatus.RESOLVED){
    t.setSlaBreached(true);tickets.save(t);
    users.findAll().stream().filter(u->u.getRole()==Role.ADMIN).forEach(u->notifications.save(new Notification(u,"SLA breached for ticket #"+t.getId())));
   }
  }
 }
}
