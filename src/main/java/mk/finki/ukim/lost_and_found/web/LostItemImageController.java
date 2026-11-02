package mk.finki.ukim.lost_and_found.web;

import lombok.AllArgsConstructor;
import mk.finki.ukim.lost_and_found.model.domain.LostItemImage;
import mk.finki.ukim.lost_and_found.repository.LostItemImageRepository;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

@Controller
@AllArgsConstructor
@RequestMapping("/lost-items/images")
public class LostItemImageController {

    private final LostItemImageRepository lostItemImageRepository;

    @GetMapping("/{imageId}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable String imageId) {
        System.out.println("Serving image " + imageId);

        return lostItemImageRepository.findById(imageId)
                .map(this::toResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<byte[]> toResponse(LostItemImage image) {
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