package stp.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stp.lab1.model.entity.AdminAction;
import java.util.List;

public interface AdminActionRepository extends JpaRepository<AdminAction, Long> {

    @Query("SELECT a FROM AdminAction a WHERE " +
            "(:search IS NULL OR :search = '' " +
            "OR LOWER(a.actionDescription) LIKE LOWER(CONCAT('%', CAST(:search AS String), '%')) " +
            "OR LOWER(a.admin.email) LIKE LOWER(CONCAT('%', CAST(:search AS String), '%')) " +
            "OR LOWER(a.admin.fullName) LIKE LOWER(CONCAT('%', CAST(:search AS String), '%'))) " +
            "ORDER BY a.actionDate DESC")
    List<AdminAction> findBySearch(@Param("search") String search);
}