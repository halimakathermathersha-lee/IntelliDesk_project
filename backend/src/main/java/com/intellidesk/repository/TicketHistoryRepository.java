package com.intellidesk.repository;
import com.intellidesk.entity.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TicketHistoryRepository extends JpaRepository<TicketHistory,Long>{ List<TicketHistory> findAllByOrderByCreatedAtDesc(); }
