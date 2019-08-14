package search.engine.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DocumentRequestDto {

    @NotBlank(message = "Key is mandatory")
    private String key;
    @NotBlank(message = "Document is mandatory")
    private String document;
}
