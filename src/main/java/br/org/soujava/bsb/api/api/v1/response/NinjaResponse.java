package br.org.soujava.bsb.api.api.v1.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NinjaResponse(
    Integer id,
    String nome,
    String vila,
    String cla,
    String rank,
    String chakraTipo,
    String especialidade,
    String kekkeiGenkai,
    String status,
    Integer nivelForca,
    LocalDate dataRegistro
) {
}
