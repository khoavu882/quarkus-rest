package com.github.kaivu.services.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class CreateEntityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(max = 500)
    @NotBlank
    private String name;

    @Size(max = 2000)
    @NotEmpty
    private String description;
}
