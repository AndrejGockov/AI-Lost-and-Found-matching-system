package mk.finki.ukim.lost_and_found.model.dto;

import mk.finki.ukim.lost_and_found.model.domain.User;

public record DisplayUserResponseDto(String id, String name, String alias) {
    public static DisplayUserResponseDto from(User u) {
        return new DisplayUserResponseDto(u.getId(), u.getName(), u.getAlias());
    }
}