package mk.finki.ukim.lost_and_found.web;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.enums.ItemType;
import mk.finki.ukim.lost_and_found.service.application.MyItemsApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@AllArgsConstructor
@Controller("/")
public class HomeController {
    private final MyItemsApplicationService myItemsApplicationService;
    private final UserService userService;

    @GetMapping("/my-reports")
    public String myReportsPage(@RequestParam(required = false) String title,
                                @RequestParam(required = false) ItemStatus status,
                                @RequestParam(required = false) ItemType type,
                                @RequestParam(defaultValue = "1") int pageNum,
                                @RequestParam(defaultValue = "9") int pageSize,
                                Principal principal,
                                Model model) {
        User user = userService.findByUsername(principal.getName());

        model.addAttribute("items", myItemsApplicationService.findPage(user, title, status, type, pageNum - 1, pageSize));
        model.addAttribute("user", user);
        model.addAttribute("itemStatus", ItemStatus.values());

        return "myReports/myReports";
    }

    @GetMapping("/error")
    public String errorPage(Model model) {
        model.addAttribute("bodyContent", "access-denied");
        return "error";
    }
}