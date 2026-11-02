package mk.finki.ukim.lost_and_found.web;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.model.domain.FoundItemImage;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.repository.FoundItemImageRepository;
import mk.finki.ukim.lost_and_found.service.domain.UserService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.security.Principal;
import java.util.concurrent.TimeUnit;

@Controller
@AllArgsConstructor
@RequestMapping("/found-items/images")
public class FoundItemImageController {

    private final FoundItemImageRepository foundItemImageRepository;
    private final UserService userService;

    @GetMapping("/{imageId}")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> getImage(@PathVariable String imageId, Principal principal) {
        User user = userService.findByUsername(principal.getName());

        return foundItemImageRepository.findById(imageId)
                .filter(image -> canView(image, user))
                .map(this::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private boolean canView(FoundItemImage image, User user) {
        FoundItem item = image.getItem();
        return item.isIdentifiable() || item.getHolder().getId().equals(user.getId());
    }

    private ResponseEntity<byte[]> toResponse(FoundItemImage image) {
        byte[] bytes = image.getImgBytes();
        String guessed = null;
        try {
            guessed = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));
        } catch (Exception ignored) {
        }
        MediaType type = guessed != null ? MediaType.parseMediaType(guessed) : MediaType.IMAGE_JPEG;

        return ResponseEntity.ok()
                .contentType(type)
                // image ids never change content, so they can be cached
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePrivate())
                .body(bytes);
    }
}