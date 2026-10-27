package mk.finki.ukim.lost_and_found.web;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.enums.ItemStatus;
import mk.finki.ukim.lost_and_found.service.application.LostItemApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping({"/lost-items"})
public class LostItemController {
    private final LostItemApplicationService lostItemApplicationService;

    @GetMapping
    public String getLostItemsPage(){
        return "lostItems/lostItems";
    }

//    @GetMapping
//    public String getRestrictedLostItemsPage(){
//        return "";
//    }

    @GetMapping("/{id}")
    public String findById(@PathVariable String id){
        return "";
    }

    @GetMapping("/add")
    public String addLostItem(){
        return "lostItems/lostItemForm";
    }

    @PostMapping
    public String create(){
        return "";
    }

    @GetMapping("/edit/{id}")
    public String editLostItem(@PathVariable String id){
        return "";
    }

    @PostMapping("/{id}")
    public String edit(@PathVariable String id){
        return "";
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable String id, ItemStatus itemStatus){
        return "";
    }
}
