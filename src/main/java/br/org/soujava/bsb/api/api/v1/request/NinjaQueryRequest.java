package br.org.soujava.bsb.api.api.v1.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.web.bind.annotation.BindParam;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NinjaQueryRequest(
    String nome,
    String vila,
    String cla,
    String rank,
    @BindParam("chakra_tipo")
    String chakraTipo,
    String especialidade,
    @BindParam("kekkei_genkai")
    String kekkeiGenkai,
    String status,
    @BindParam("nivel_forca")
    Integer nivelForca
) {
}
