package mk.finki.ukim.lost_and_found.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.List;
import mk.finki.ukim.lost_and_found.model.domain.LostItem;
import mk.finki.ukim.lost_and_found.model.domain.LostItemImage;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.exceptions.CouldNotReadImageException;
import org.springframework.web.multipart.MultipartFile;

public record CreateOrUpdateLostItemRequestDto(
        @NotBlank
        String title,
        @NotBlank
        String description,
        @PastOrPresent
        Instant dateLost,
        String locationLost,
        List<MultipartFile> images
) {
    // Create: builds a new item
    public LostItem toLostItem(User poster) {
        LostItem item = new LostItem();
        item.setPoster(poster);
        applyTo(item);
        return item;
    }

    // Update: copies the editable fields onto an existing item. New images are appended.
    public void applyTo(LostItem item) {
        item.setTitle(title);
        item.setDescription(description);
        item.setDateLost(dateLost);
        item.setLocationLost(locationLost);

        if (images != null) {
            images.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> toImage(file, item))
                    .forEach(item.getImages()::add);
        }
    }

    private static LostItemImage toImage(MultipartFile file, LostItem item) {
        try {
            LostItemImage image = new LostItemImage();
            image.setItem(item);
            image.setImgBytes(file.getBytes());
            image.setSizeBytes((int) file.getSize());
            return image;
        } catch (Exception e) {
            throw new CouldNotReadImageException();
        }
    }
}