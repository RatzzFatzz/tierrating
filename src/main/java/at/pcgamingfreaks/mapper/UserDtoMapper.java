package at.pcgamingfreaks.mapper;

import at.pcgamingfreaks.model.dto.UserDTO;
import at.pcgamingfreaks.model.anilist.AniListUser;


public class UserDtoMapper {
    public static UserDTO map(AniListUser user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getName());
        return dto;
    }
}
