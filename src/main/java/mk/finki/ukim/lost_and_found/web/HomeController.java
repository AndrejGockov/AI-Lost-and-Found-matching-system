package mk.finki.ukim.lost_and_found.web;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Controller("/")
public class HomeController {
    @GetMapping("/error")
    public String errorPage(Model model) {
        model.addAttribute("bodyContent", "access-denied");
        return "error";
    }
}
