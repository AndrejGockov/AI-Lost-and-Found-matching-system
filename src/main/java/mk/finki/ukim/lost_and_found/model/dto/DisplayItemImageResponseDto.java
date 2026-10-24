package mk.finki.ukim.lost_and_found.model.dto;

import mk.finki.ukim.lost_and_found.model.domain.FoundItemImage;
import mk.finki.ukim.lost_and_found.model.domain.LostItemImage;

public record DisplayItemImageResponseDto(
        String id,
        int sizeBytes,
        byte[] imgBytes
) {
    public static DisplayItemImageResponseDto from(LostItemImage image) {
        return new DisplayItemImageResponseDto(
                image.getId(),
                image.getSizeBytes(),
                image.getImgBytes()
        );
    }

    public static DisplayItemImageResponseDto from(FoundItemImage image) {
        return new DisplayItemImageResponseDto(
                image.getId(),
                image.getSizeBytes(),
                image.getImgBytes()
        );
    }
}