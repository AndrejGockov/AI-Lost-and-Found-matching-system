package mk.finki.ukim.lost_and_found.web;

import java.security.Principal;
import java.util.List;
import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.DisplayNotificationResponseDto;
import mk.finki.ukim.lost_and_found.service.application.NotificationApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@AllArgsConstructor
@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationApplicationService notificationApplicationService;
    private final UserService userService;

    @GetMapping
    public String getNotificationsPage(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        List<DisplayNotificationResponseDto> notifications = notificationApplicationService.findAllByUser(user);

        model.addAttribute("notifications", notifications);
        model.addAttribute("user", user);

        return "notifications/notifications";
    }

    // Rest mappings
    @GetMapping("/{id}")
    @ResponseBody
    public DisplayNotificationResponseDto getNotification(@PathVariable String id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return notificationApplicationService.findById(id, user);
    }

    @PostMapping("/{id}/mark-read")
    @ResponseBody
    public DisplayNotificationResponseDto markRead(@PathVariable String id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return notificationApplicationService.markAsRead(id, user);
    }

    @PostMapping("/mark-all-read")
    @ResponseBody
    public List<DisplayNotificationResponseDto> markAllRead(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return notificationApplicationService.markAllAsRead(user);
    }
}