package app.mereb.mereb_backend.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequestDTO {
    @NotBlank(message = "Content must not be blank")
    @Size(max = 300, message = "Content must not exceed 300 characters")
    private String content;
}
