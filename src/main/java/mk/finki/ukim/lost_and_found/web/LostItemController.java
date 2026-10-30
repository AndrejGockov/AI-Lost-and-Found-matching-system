package mk.finki.ukim.lost_and_found.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.LostItem;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.CreateOrUpdateLostItemRequestDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayLostItemResponseDto;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.exceptions.LostItemNotFoundException;
import mk.finki.ukim.lost_and_found.service.application.LostItemApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.LostItemService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Controller
@RequestMapping({"/lost-items"})
public class LostItemController {
    private final LostItemApplicationService lostItemApplicationService;
    private final LostItemService lostItemService;
    private final UserService userService;

    @GetMapping
    public String getLostItemsPage(@RequestParam(required = false) String title,
                                   @RequestParam(required = false) String posterName,
                                   @RequestParam(required = false) ItemStatus status,
                                   @RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize,
                                   Principal principal,
                                   Model model){
        User user = userService.findByUsername(principal.getName());

        model.addAttribute("items", lostItemApplicationService.findPage(title, posterName, status, user, pageNum - 1, pageSize));
        model.addAttribute("user", user);
        model.addAttribute("isStudent", userService.isStudent(user));
        model.addAttribute("itemStatus", ItemStatus.values());

        return "lostItems/lostItems";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable String id,
                           Principal principal,
                           Model model){
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("item", lostItemApplicationService.findById(id).orElseThrow(() -> new LostItemNotFoundException(id)));
        model.addAttribute("user", user);
        model.addAttribute("itemStatus", ItemStatus.values());
        return "lostItems/lostItemDetails";
    }

    @GetMapping("/add")
    public String addLostItem(){
        return "lostItems/lostItemForm";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute CreateOrUpdateLostItemRequestDto createOrUpdateLostItemRequestDto,
                         BindingResult bindingResult,
                         Principal principal,
                         RedirectAttributes redirectAttributes){
        User user = userService.findByUsername(principal.getName());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/lost-items/add";
        }

        try {
            DisplayLostItemResponseDto item = lostItemApplicationService.create(createOrUpdateLostItemRequestDto, user);
            return "redirect:/lost-items/" + item.id();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/lost-items/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String editLostItem(@PathVariable String id,
                               Model model){
        DisplayLostItemResponseDto itemDto = lostItemApplicationService.findById(id)
                .orElseThrow(() -> new LostItemNotFoundException(id));
        model.addAttribute("item", itemDto);

        LostItem item = lostItemService.findById(id).orElseThrow(() -> new LostItemNotFoundException(id));
        if (item.getDateLost() != null) {
            String formattedDate = LocalDateTime.ofInstant(item.getDateLost(), ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            model.addAttribute("dateLostValue", formattedDate);
        }

        return "lostItems/lostItemForm";
    }

    @PostMapping("/{id}")
    public String edit(@PathVariable String id,
                       @Valid @ModelAttribute CreateOrUpdateLostItemRequestDto updateLostItemRequestDto,
                       BindingResult bindingResult,
                       Principal principal,
                       RedirectAttributes redirectAttributes){
        User user = userService.findByUsername(principal.getName());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/lost-items/edit/" + id;
        }

        try {
            lostItemApplicationService.update(id, updateLostItemRequestDto, user);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/lost-items/edit/" + id;
        }
        return "redirect:/lost-items/{id}";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable String id,
                               @RequestParam ItemStatus itemStatus,
                               Principal principal,
                               RedirectAttributes redirectAttributes){
        User user = userService.findByUsername(principal.getName());
        try {
            lostItemApplicationService.updateStatus(id, itemStatus, user);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/lost-items/{id}";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id,
                         Principal principal,
                         RedirectAttributes redirectAttributes){
        User user = userService.findByUsername(principal.getName());
        try {
            lostItemApplicationService.deleteById(id, user);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/lost-items/{id}";
        }
        return "redirect:/lost-items";
    }
}