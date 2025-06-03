package at.pcgamingfreaks.mapper;

import at.pcgamingfreaks.model.UserDTO;
import at.pcgamingfreaks.model.anilist.AniListUser;
import lombok.Getter;
import lombok.Setter;


public class UserDtoMapper {
    public static UserDTO map(AniListUser user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getName());
        return dto;
    }
}
