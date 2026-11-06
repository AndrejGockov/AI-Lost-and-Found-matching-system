package mk.finki.ukim.lost_and_found.web;

import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.dto.CreateOrUpdateFoundItemRequestDto;
import mk.finki.ukim.lost_and_found.model.dto.DisplayFoundItemResponseDto;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.model.exceptions.FoundItemNotFoundException;
import mk.finki.ukim.lost_and_found.model.exceptions.InvalidUsernameException;
import mk.finki.ukim.lost_and_found.service.application.FoundItemApplicationService;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                    Model model) {
        User user = userService.findByUsername(principal.getName());
        boolean isStudent = userService.isStudent(user);

        // Two lists, split by identifiable: fullItems (identifiable = true)
        // and restrictedItems (identifiable = false). isStudent only limits
        // which extra filters (posterName/status) apply to fullItems.
        model.addAttribute("fullItems", foundItemApplicationService.findPage(title, posterName, status, user, pageNum - 1, pageSize));
        model.addAttribute("restrictedItems", foundItemApplicationService.findPageRestricted(title, pageNum - 1, pageSize));

        model.addAttribute("user", user);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("itemStatus", ItemStatus.values());

        return "foundItems/foundItems";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable String id,
                           Principal principal,
                           Model model) {
        User user = userService.findByUsername(principal.getName());

        DisplayFoundItemResponseDto fullItem = foundItemApplicationService.findById(id)
                .orElseThrow(() -> new FoundItemNotFoundException(id));

        // The current holder always sees full detail. Otherwise: identifiable
        // = true shows full detail to anyone; identifiable = false and not
        // the holder shows the restricted view.
        boolean full = fullItem.holderId().equals(user.getId())
                || fullItem.identifiable();

        if (full) {
            model.addAttribute("item", fullItem);
        } else {
            model.addAttribute("item", foundItemApplicationService.findByIdRestricted(id)
                    .orElseThrow(() -> new FoundItemNotFoundException(id)));
        }

        model.addAttribute("full", full);
        model.addAttribute("user", user);
        model.addAttribute("itemStatus", ItemStatus.values());

        return "foundItems/foundItemDetails";
    }

    @GetMapping("/{id}/holder-history")
    public String getFoundItemTransferHistory(@PathVariable String id,
                                              Principal principal,
                                              Model model) {
        return "";
    }

    @GetMapping("/add")
    public String addFoundItem(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        return "foundItems/foundItemForm";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute CreateOrUpdateFoundItemRequestDto createOrUpdateFoundItemRequestDto,
                         BindingResult bindingResult,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the errors and try again.");
            return "redirect:/found-items/add";
        }

        User poster = userService.findByUsername(principal.getName());

        try {
            DisplayFoundItemResponseDto created = foundItemApplicationService.create(createOrUpdateFoundItemRequestDto, poster);
            return "redirect:/found-items/" + created.id();
        } catch (InvalidUsernameException e) {
            redirectAttributes.addFlashAttribute("error", "No user found with that holder username.");
            return "redirect:/found-items/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String editFoundItem(@PathVariable String id,
                                Principal principal,
                                Model model) {
        DisplayFoundItemResponseDto itemDto = foundItemApplicationService.findById(id)
                .orElseThrow(() -> new FoundItemNotFoundException(id));
        model.addAttribute("item", itemDto);

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        if (itemDto.dateFound() != null) {
            String formattedDate = LocalDateTime.ofInstant(itemDto.dateFound(), ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            model.addAttribute("dateFoundValue", formattedDate);
        }

        return "foundItems/foundItemForm";
    }

    @PostMapping("/{id}")
    public String edit(@PathVariable String id,
                       @Valid @ModelAttribute CreateOrUpdateFoundItemRequestDto updateFoundItemRequestDto,
                       BindingResult bindingResult,
                       Principal principal,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fix the errors and try again.");
            return "redirect:/found-items/edit/" + id;
        }

        User user = userService.findByUsername(principal.getName());

        try {
            foundItemApplicationService.update(id, updateFoundItemRequestDto, user);
            return "redirect:/found-items/" + id;
        } catch (InvalidUsernameException e) {
            redirectAttributes.addFlashAttribute("error", "No user found with that holder username.");
            return "redirect:/found-items/edit/" + id;
        }
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable String id,
                               @RequestParam ItemStatus itemStatus,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        foundItemApplicationService.updateStatus(id, itemStatus, user);

        return "redirect:/found-items/" + id;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        foundItemApplicationService.deleteById(id, user);

        return "redirect:/found-items";
    }

    @GetMapping("/{id}/create-transfer-offer")
    public String createTransfer(@PathVariable String id,
                                 Principal principal,
                                 Model model) {
        return "";
    }

    @PostMapping("/{id}/create-transfer-offer")
    public String addTransfer(@PathVariable String id,
                                 Principal principal,
                                 Model model) {
        return "redirect:/found-items/" + id;
    }

    @PostMapping("/{id}/retract-transfer-offer")
    public String retractTransfer(@PathVariable String id,
                                  Principal principal,
                                  Model model) {
        return "redirect:/found-items/" + id;
    }

    @PostMapping("/{id}/accept-transfer")
    public String acceptTransfer(@PathVariable String id,
                                 Principal principal,
                                 Model model) {
        return "redirect:/found-items/" + id;
    }

    @PostMapping("/{id}/reject-transfer")
    public String rejectTransfer(@PathVariable String id,
                                 Principal principal,
                                 Model model) {
        return "redirect:/found-items/" + id;
    }
}