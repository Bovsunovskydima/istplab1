package stp.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stp.lab1.model.entity.AdminAction;

import java.util.List;

public interface AdminActionRepository extends JpaRepository<AdminAction, Long> {
    List<AdminAction> findByAdminIdOrderByActionDateDesc(Long adminId);
}
