package com.intellidesk.repository;
import com.intellidesk.entity.Ticket;
import com.intellidesk.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TicketRepository extends JpaRepository<Ticket,Long>{
 List<Ticket> findByCreatedByIdOrderByCreatedAtDesc(Long userId);
 List<Ticket> findByAssignedWorkerUserIdOrderByCreatedAtDesc(Long workerId);
 List<Ticket> findAllByOrderByCreatedAtDesc();
 List<Ticket> findByStatusNot(TicketStatus status);
 long countByAssignedWorkerUserIdAndStatusIn(Long workerId,List<TicketStatus> statuses);
}
