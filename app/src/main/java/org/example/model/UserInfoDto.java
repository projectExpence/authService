package org.example.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.example.entities.UserInfo;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfoDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String profilePictureUrl;
}
