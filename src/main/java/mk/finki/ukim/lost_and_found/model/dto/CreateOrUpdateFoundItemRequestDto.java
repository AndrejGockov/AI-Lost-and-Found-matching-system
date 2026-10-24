package mk.finki.ukim.lost_and_found.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.List;
import mk.finki.ukim.lost_and_found.model.domain.FoundItem;
import mk.finki.ukim.lost_and_found.model.domain.FoundItemImage;
import mk.finki.ukim.lost_and_found.model.domain.User;
import mk.finki.ukim.lost_and_found.model.exceptions.CouldNotReadImageException;
import org.springframework.web.multipart.MultipartFile;

public record CreateOrUpdateFoundItemRequestDto(
        @NotBlank
        String holderId,
        @NotBlank
        String title,
        @NotBlank
        String description,
        @PastOrPresent
        Instant dateFound,
        String locationFound,
        boolean identifiable,
        List<MultipartFile> images
) {
    // Create: builds a new item (status defaults to UNRESOLVED, embedding is filled in later by the AI service).
    public FoundItem toFoundItem(User poster, User holder) {
        FoundItem item = new FoundItem();
        item.setPoster(poster);
        applyTo(item, holder);
        return item;
    }

    // Update: copies the editable fields onto an existing item
    public void applyTo(FoundItem item, User holder) {
        item.setHolder(holder);
        item.setTitle(title);
        item.setDescription(description);
        item.setDateFound(dateFound);
        item.setLocationFound(locationFound);
        item.setIdentifiable(identifiable);

        if (images != null) {
            images.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> toImage(file, item))
                    .forEach(item.getImages()::add);
        }
    }

    private static FoundItemImage toImage(MultipartFile file, FoundItem item) {
        try {
            FoundItemImage image = new FoundItemImage();
            image.setItem(item);
            image.setImgBytes(file.getBytes());
            image.setSizeBytes((int) file.getSize());
            return image;
        } catch (Exception e) {
            throw new CouldNotReadImageException();
        }
    }
}