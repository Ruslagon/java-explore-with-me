package ru.practicum.dto.UserDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NewUserRequest {

    @Size(min = 6, max = 254)
    @NotBlank
    @Email
    private String email;

    @Size(min = 2, max = 250)
    @NotBlank
    private String name;

}
