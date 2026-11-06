package mk.finki.ukim.lost_and_found.model.dto;

import mk.finki.ukim.lost_and_found.model.domain.FoundItemImage;
import mk.finki.ukim.lost_and_found.model.domain.LostItemImage;

import java.util.Base64;

public record DisplayItemImageResponseDto(
        String id,
        int sizeBytes
) {
    public static DisplayItemImageResponseDto from(LostItemImage image) {
        return new DisplayItemImageResponseDto(
                image.getId(),
                image.getSizeBytes()
        );
    }

    public static DisplayItemImageResponseDto from(FoundItemImage image) {
        return new DisplayItemImageResponseDto(
                image.getId(),
                image.getSizeBytes()
        );
    }
}