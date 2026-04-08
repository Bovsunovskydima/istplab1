package stp.lab1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stp.lab1.model.entity.AdminAction;
import stp.lab1.model.entity.User;
import stp.lab1.repository.AdminActionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminActionService {

    private final AdminActionRepository adminActionRepository;

    @Transactional
    public void logAction(User admin, String description) {
        AdminAction action = AdminAction.builder()
                .admin(admin)
                .actionDescription(description)
                // actionDate проставиться автоматично завдяки твоєму @PrePersist
                .build();
        adminActionRepository.save(action);
    }

    @Transactional(readOnly = true)
    public List<AdminAction> getFilteredActions(String search) {
        String searchParam = (search == null || search.isBlank()) ? null : search;
        return adminActionRepository.findBySearch(searchParam);
    }
}