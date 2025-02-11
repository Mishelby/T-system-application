package org.example.logisticapplication.controllers.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.User.CreateUserDto;
import org.example.logisticapplication.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserRegistrationViewController {
    private final UserService userService;

    @GetMapping("/registration")
    public String registration() {
        return "create-user";
    }

    @PostMapping("/registration")
    public String createUser(
            @ModelAttribute CreateUserDto newUser,
            RedirectAttributes redirectAttributes
    ) {
        var savedUser = userService.createNewUser(newUser);
        var userId = userService.getUserIdByEmail(savedUser.email());

        log.info("USER ID FOR REDIRECT: {}", userId);
        redirectAttributes.addFlashAttribute("message", "Пользователь успешно создан!");

        return "redirect:/users/view/profile/" + userId;
    }

    @GetMapping("/profile/{id}")
    public String getUserPage(
            @PathVariable("id") Long id,
            Model model
    ) {
        var userInfo = userService.getUserInfo(id);
        model.addAttribute("user", userInfo);
        return "user-profile";
    }
}