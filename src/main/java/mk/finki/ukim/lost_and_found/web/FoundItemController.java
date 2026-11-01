package mk.finki.ukim.lost_and_found.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.CreateOrUpdateLostItemRequestDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayLostItemResponseDto;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.exceptions.LostItemNotFoundException;
import mk.finki.ukim.lost_and_found.service.application.FoundItemApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@AllArgsConstructor
@Controller
@RequestMapping({"/found-items"})
public class FoundItemController {
    private final FoundItemApplicationService foundItemApplicationService;
    private final UserService userService;

    @GetMapping
    public String getFoundItemsPage(@RequestParam(required = false) String title,
                                   @RequestParam(required = false) String posterName,
                                   @RequestParam(required = false) ItemStatus status,
                                   @RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize,
                                   Principal principal,
                                   Model model){
        User user = userService.findByUsername(principal.getName());

        model.addAttribute("items", foundItemApplicationService.findPage(title, posterName, status, user, pageNum - 1, pageSize));
        model.addAttribute("user", user);
        model.addAttribute("isStudent", userService.isStudent(user));
        model.addAttribute("itemStatus", ItemStatus.values());

        return "foundItems/foundItems";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable String id,
                           Principal principal,
                           Model model){
        return "foundItems/foundItemDetails";
    }

    @GetMapping("/add")
    public String addFoundItem(){
        return "lostItems/foundItemForm";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute CreateOrUpdateLostItemRequestDto createOrUpdateLostItemRequestDto,
                         BindingResult bindingResult,
                         Principal principal,
                         RedirectAttributes redirectAttributes){
        return "";
    }

    @GetMapping("/edit/{id}")
    public String editFoundItem(@PathVariable String id,
                               Model model){
        return "";
    }

    @PostMapping("/{id}")
    public String edit(@PathVariable String id,
                       @Valid @ModelAttribute CreateOrUpdateLostItemRequestDto updateLostItemRequestDto,
                       BindingResult bindingResult,
                       Principal principal,
                       RedirectAttributes redirectAttributes){
        return "";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable String id,
                               @RequestParam ItemStatus itemStatus,
                               Principal principal,
                               RedirectAttributes redirectAttributes){
        return "";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id,
                         Principal principal,
                         RedirectAttributes redirectAttributes){
        return "";
    }
}
