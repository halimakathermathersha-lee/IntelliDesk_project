package com.intellidesk.repository;
import com.intellidesk.entity.TicketUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TicketUpdateRepository extends JpaRepository<TicketUpdate,Long>{ List<TicketUpdate> findByTicketIdOrderByCreatedAtAsc(Long ticketId); }
