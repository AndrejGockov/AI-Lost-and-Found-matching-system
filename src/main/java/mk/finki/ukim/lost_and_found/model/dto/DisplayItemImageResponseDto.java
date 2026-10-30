package mk.finki.ukim.lost_and_found.model.dto;

import mk.finki.ukim.lost_and_found.model.domain.FoundItemImage;
import mk.finki.ukim.lost_and_found.model.domain.LostItemImage;

import java.util.Base64;

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

    public String getBase64Data() {
        if (imgBytes == null || imgBytes.length == 0) {
            return "";
        }
        return Base64.getEncoder().encodeToString(imgBytes);
    }
}