package stp.lab1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import stp.lab1.model.entity.BorrowRecord;
import stp.lab1.model.entity.User;
import stp.lab1.model.enums.UserRole;
import stp.lab1.repository.BorrowRecordRepository;
import stp.lab1.repository.UserRepository;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final AdminActionService adminActionService;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void changeRole(Long id, UserRole role, Principal principal) {
        User targetUser = findById(id);
        User currentAdmin = findByEmail(principal.getName());

        if (targetUser.getRole() == UserRole.Admin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Зміна ролі адміністратора заборонена!");
        }
        targetUser.setRole(role);
        userRepository.save(targetUser);

        adminActionService.logAction(currentAdmin,
                "Змінено роль користувачу " + targetUser.getEmail() + " на " + role.name());
    }

    @Transactional(readOnly = true)
    public List<BorrowRecord> getBorrowHistory(Long userId) {
        return borrowRecordRepository.findByUserId(userId);
    }
}
