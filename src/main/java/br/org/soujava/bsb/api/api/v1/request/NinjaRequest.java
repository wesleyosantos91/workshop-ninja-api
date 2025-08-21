package br.org.soujava.bsb.api.api.v1.request;

import br.org.soujava.bsb.api.core.validation.Groups;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NinjaRequest(

    @NotBlank(message = "Nome é obrigatório", groups = Groups.Create.class)
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres", groups = Groups.Create.class)
    String nome,

    @NotBlank(message = "Vila é obrigatória", groups = Groups.Create.class)
    @Size(max = 50, message = "Vila deve ter no máximo 50 caracteres", groups = Groups.Create.class)
    String vila,

    @Size(max = 50, message = "Clã deve ter no máximo 50 caracteres", groups = Groups.Create.class)
    String cla,

    @NotBlank(message = "Rank é obrigatório", groups = Groups.Create.class)
    @Size(max = 20, message = "Rank deve ter no máximo 20 caracteres", groups = Groups.Create.class)
    @Pattern(regexp = "^(Genin|Chunin|Jounin|Kage)$",
             message = "Rank deve ser: Genin, Chunin, Jounin ou Kage",
             groups = Groups.Create.class)
    String rank,

    @NotBlank(message = "Tipo de chakra é obrigatório", groups = Groups.Create.class)
    @Size(max = 30, message = "Tipo de chakra deve ter no máximo 30 caracteres", groups = Groups.Create.class)
    String chakraTipo,

    @Size(max = 50, message = "Especialidade deve ter no máximo 50 caracteres", groups = Groups.Create.class)
    String especialidade,

    @Size(max = 50, message = "Kekkei Genkai deve ter no máximo 50 caracteres", groups = Groups.Create.class)
    String kekkeiGenkai,

    @Size(max = 20, message = "Status deve ter no máximo 20 caracteres", groups = Groups.Create.class)
    @Pattern(regexp = "^(Ativo|Desaparecido|Renegado)$",
             message = "Status deve ser: Ativo, Desaparecido ou Renegado",
             groups = Groups.Create.class)
    String status,

    @Min(value = 1, message = "Nível de força deve ser no mínimo 1", groups = Groups.Create.class)
    @Max(value = 100, message = "Nível de força deve ser no máximo 100", groups = Groups.Create.class)
    Integer nivelForca,

    @PastOrPresent(message = "Data de registro deve ser hoje ou no passado", groups = Groups.Create.class)
    LocalDate dataRegistro
) {
}
