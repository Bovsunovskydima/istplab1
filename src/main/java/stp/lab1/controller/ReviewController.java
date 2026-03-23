package stp.lab1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import stp.lab1.model.entity.User;
import stp.lab1.service.ReviewService;
import stp.lab1.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping
    public String add(@RequestParam Long bookId,
                      @RequestParam int rating,
                      @RequestParam(required = false) String comment,
                      Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        reviewService.addReview(bookId, currentUser.getId(), rating, comment);
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @RequestParam Long bookId) {
        reviewService.delete(id);
        return "redirect:/books/" + bookId;
    }
}

