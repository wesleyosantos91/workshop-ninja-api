package br.org.soujava.bsb.api.api.v1.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import br.org.soujava.bsb.api.domain.service.NinjaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NinjaController.class)
@DisplayName("Ninja Controller")
class NinjaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NinjaService ninjaService;

    @Autowired
    private ObjectMapper objectMapper;

    private NinjaEntity ninjaEntity;
    private NinjaRequest ninjaRequest;

    @BeforeEach
    void setUp() {
        // Preparar dados de teste reutilizáveis
        ninjaEntity = new NinjaEntity();
        ninjaEntity.setId(1);
        ninjaEntity.setNome("Naruto Uzumaki");
        ninjaEntity.setVila("Konoha");
        ninjaEntity.setCla("Uzumaki");
        ninjaEntity.setRank("Kage"); // Mudando de "Hokage" para "Kage" (valor válido)
        ninjaEntity.setChakraTipo("Vento");
        ninjaEntity.setEspecialidade("Ninjutsu");
        ninjaEntity.setKekkeiGenkai("Kurama (Bijuu)");
        ninjaEntity.setStatus("Ativo");
        ninjaEntity.setNivelForca(98);
        ninjaEntity.setDataRegistro(LocalDate.of(2024, 1, 1));

        ninjaRequest = new NinjaRequest(
                "Naruto Uzumaki",
                "Konoha",
                "Uzumaki",
                "Kage", // Mudando de "Hokage" para "Kage" (valor válido)
                "Vento",
                "Ninjutsu",
                "Kurama (Bijuu)",
                "Ativo",
                98,
                LocalDate.of(2024, 1, 1)
        );
    }

    @Test
    @DisplayName("POST /v1/ninjas - Deve criar ninja com sucesso")
    void deveCrearNinjaComSucesso() throws Exception {
        // Given: service retornará ninja criado
        when(ninjaService.create(any(NinjaRequest.class))).thenReturn(ninjaEntity);

        // When/Then: fazer requisição POST e verificar resposta
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ninjaRequest)))
                .andDo(print()) // Imprime detalhes da requisição/resposta (útil para debug)
                .andExpect(status().isCreated()) // Status 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Naruto Uzumaki")))
                .andExpect(jsonPath("$.vila", is("Konoha")))
                .andExpect(jsonPath("$.rank", is("Kage"))) // Corrigindo de "Hokage" para "Kage"
                .andExpect(jsonPath("$.chakra_tipo", is("Vento"))) // snake_case por causa do @JsonNaming
                .andExpect(jsonPath("$.nivel_forca", is(98)));
    }


    @Test
    @DisplayName("GET /v1/ninjas/{id} - Deve buscar ninja por ID com sucesso")
    void deveBuscarNinjaPorIdComSucesso() throws Exception {
        // Given: service retornará ninja encontrado
        when(ninjaService.findById(1)).thenReturn(ninjaEntity);

        // When/Then: fazer requisição GET por ID
        mockMvc.perform(get("/v1/ninjas/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk()) // Status 200
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Naruto Uzumaki")))
                .andExpect(jsonPath("$.vila", is("Konoha")))
                .andExpect(jsonPath("$.rank", is("Kage")));
    }

    @Test
    @DisplayName("GET /v1/ninjas/{id} - Deve retornar erro 404 para ID inexistente")
    void deveRetornarErro404ParaIdInexistente() throws Exception {
        // Given: service lançará ResourceNotFoundException
        when(ninjaService.findById(999))
                .thenThrow(new ResourceNotFoundException("Not found registry with code 999"));

        // When/Then: fazer requisição com ID inexistente
        mockMvc.perform(get("/v1/ninjas/{id}", 999))
                .andDo(print())
                .andExpect(status().isNotFound()); // Status 404
    }

    @Test
    @DisplayName("GET /v1/ninjas - Deve fazer busca paginada com sucesso")
    void deveFazerBuscaPaginadaComSucesso() throws Exception {
        // Given: preparar dados paginados
        NinjaEntity sasuke = new NinjaEntity();
        sasuke.setId(2);
        sasuke.setNome("Sasuke Uchiha");
        sasuke.setVila("Konoha");
        sasuke.setRank("Jounin");

        List<NinjaEntity> ninjas = List.of(ninjaEntity, sasuke);
        Page<NinjaEntity> page = new PageImpl<>(ninjas, PageRequest.of(0, 10), 2);

        when(ninjaService.search(any(NinjaQueryRequest.class), any(Pageable.class))).thenReturn(page);

        // When/Then: fazer requisição GET com parâmetros de busca
        mockMvc.perform(get("/v1/ninjas")
                        .param("vila", "Konoha")
                        .param("status", "Ativo")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nome", is("Naruto Uzumaki")))
                .andExpect(jsonPath("$.content[1].nome", is("Sasuke Uchiha")))
                .andExpect(jsonPath("$.page.totalElements", is(2))) // Corrigido: $.page.totalElements
                .andExpect(jsonPath("$.page.size", is(10))); // Corrigido: $.page.size
    }

    @Test
    @DisplayName("GET /v1/ninjas - Deve retornar página vazia quando não há resultados")
    void deveRetornarPaginaVaziaQuandoNaoHaResultados() throws Exception {
        // Given: service retornará página vazia
        Page<NinjaEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(ninjaService.search(any(NinjaQueryRequest.class), any(Pageable.class))).thenReturn(emptyPage);

        // When/Then: fazer busca que não retorna resultados
        mockMvc.perform(get("/v1/ninjas")
                        .param("vila", "Vila Inexistente"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.totalElements", is(0))); // Corrigido: $.page.totalElements
    }

    @Test
    @DisplayName("PUT /v1/ninjas/{id} - Deve atualizar ninja com sucesso")
    void deveAtualizarNinjaComSucesso() throws Exception {
        // Given: ninja atualizado
        NinjaEntity ninjaAtualizado = new NinjaEntity();
        ninjaAtualizado.setId(1);
        ninjaAtualizado.setNome("Naruto Uzumaki - Atualizado");
        ninjaAtualizado.setVila("Konoha");
        ninjaAtualizado.setRank("Hokage");
        ninjaAtualizado.setNivelForca(99);

        when(ninjaService.update(eq(1), any(NinjaRequest.class))).thenReturn(ninjaAtualizado);

        NinjaRequest requestAtualizado = new NinjaRequest(
                "Naruto Uzumaki - Atualizado",
                "Konoha",
                "Uzumaki",
                "Hokage",
                "Vento",
                "Ninjutsu",
                "Kurama (Bijuu)",
                "Ativo",
                99, // nivel_forca aumentado
                LocalDate.of(2024, 1, 1)
        );

        // When/Then: fazer requisição PUT
        mockMvc.perform(put("/v1/ninjas/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestAtualizado)))
                .andDo(print())
                .andExpect(status().isOk()) // Status 200
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Naruto Uzumaki - Atualizado")))
                .andExpect(jsonPath("$.nivel_forca", is(99)));
    }

    @Test
    @DisplayName("PUT /v1/ninjas/{id} - Deve retornar erro 404 ao atualizar ninja inexistente")
    void deveRetornarErro404AoAtualizarNinjaInexistente() throws Exception {
        // Given: service lançará ResourceNotFoundException
        when(ninjaService.update(eq(999), any(NinjaRequest.class)))
                .thenThrow(new ResourceNotFoundException("Not found registry with code 999"));

        // When/Then: tentar atualizar ninja inexistente
        mockMvc.perform(put("/v1/ninjas/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ninjaRequest)))
                .andDo(print())
                .andExpect(status().isNotFound()); // Status 404
    }

    @Test
    @DisplayName("DELETE /v1/ninjas/{id} - Deve deletar ninja com sucesso")
    void deveDeletarNinjaComSucesso() throws Exception {
        // Given: service executará delete sem erro
        doNothing().when(ninjaService).delete(1);

        // When/Then: fazer requisição DELETE
        mockMvc.perform(delete("/v1/ninjas/{id}", 1))
                .andDo(print())
                .andExpect(status().isNoContent()); // Status 204
    }

    @Test
    @DisplayName("DELETE /v1/ninjas/{id} - Deve retornar erro 404 ao deletar ninja inexistente")
    void deveRetornarErro404AoDeletarNinjaInexistente() throws Exception {
        // Given: service lançará ResourceNotFoundException
        doThrow(new ResourceNotFoundException("Not found registry with code 999"))
                .when(ninjaService).delete(999);

        // When/Then: tentar deletar ninja inexistente
        mockMvc.perform(delete("/v1/ninjas/{id}", 999))
                .andDo(print())
                .andExpect(status().isNotFound()); // Status 404
    }

    @Test
    @DisplayName("Deve testar validação de Content-Type")
    void deveTestarValidacaoContentType() throws Exception {
        // When/Then: fazer requisição POST sem Content-Type correto
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.TEXT_PLAIN) // Content-Type incorreto
                        .content("dados inválidos"))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType()); // Status 415
    }

    @Test
    @DisplayName("Deve testar requisição com JSON malformado")
    void deveTestarRequisicaoComJsonMalformado() throws Exception {
        // When/Then: fazer requisição com JSON inválido
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"nome\": \"test\", \"malformed\": }")) // JSON inválido
                .andDo(print())
                .andExpect(status().isBadRequest()); // Status 400
    }

    @Test
    @DisplayName("Deve testar headers de resposta")
    void deveTestarHeadersDeResposta() throws Exception {
        // Given
        when(ninjaService.create(any(NinjaRequest.class))).thenReturn(ninjaEntity);

        // When/Then: verificar headers de resposta
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ninjaRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /v1/ninjas - Deve retornar erro 400 para JSON vazio devido à validação")
    void deveRetornarErro400ParaJsonVazioDevidoValidacao() throws Exception {
        // Given: request com JSON vazio - deve falhar na validação
        String jsonVazio = "{}"; // JSON vazio

        // When/Then: fazer requisição com JSON vazio e esperar erro de validação
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonVazio))
                .andDo(print())
                .andExpect(status().isBadRequest()) // Status 400 - falha na validação
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("POST /v1/ninjas - Deve retornar erro 400 para dados inválidos")
    void deveRetornarErro400ParaDadosInvalidos() throws Exception {
        // Given: request com dados que não passam na validação
        NinjaRequest requestInvalido = new NinjaRequest(
                "", // nome vazio - falha @NotBlank
                "", // vila vazia - falha @NotBlank
                "Uzumaki",
                "RankInvalido", // rank inválido - falha @Pattern
                "", // chakraTipo vazio - falha @NotBlank
                "Ninjutsu",
                "Kurama (Bijuu)",
                "StatusInvalido", // status inválido - falha @Pattern
                101, // nivelForca > 100 - falha @Max
                LocalDate.of(2025, 1, 1) // data futura - falha @PastOrPresent
        );

        // When/Then: fazer requisição com dados inválidos
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // Status 400 - falha na validação
                .andExpect(content().contentType("application/problem+json"));
    }

    @Test
    @DisplayName("Deve testar diferentes parâmetros de paginação")
    void deveTestarDiferentesParametrosPaginacao() throws Exception {
        // Given
        Page<NinjaEntity> page = new PageImpl<>(List.of(ninjaEntity), PageRequest.of(2, 5), 11);
        when(ninjaService.search(any(NinjaQueryRequest.class), any(Pageable.class))).thenReturn(page);

        // When/Then: testar com diferentes parâmetros de paginação
        mockMvc.perform(get("/v1/ninjas")
                        .param("page", "2")
                        .param("size", "5")
                        .param("sort", "nome,asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page.number", is(2))) // Corrigido: $.page.number
                .andExpect(jsonPath("$.page.size", is(5))) // Corrigido: $.page.size
                .andExpect(jsonPath("$.page.totalElements", is(11))); // Corrigido: $.page.totalElements
    }

    @Test
    @DisplayName("Deve testar todos os filtros de busca disponíveis")
    void deveTestarTodosFiltrosDeBuscaDisponiveis() throws Exception {
        // Given
        Page<NinjaEntity> page = new PageImpl<>(List.of(ninjaEntity), PageRequest.of(0, 10), 1);
        when(ninjaService.search(any(NinjaQueryRequest.class), any(Pageable.class))).thenReturn(page);

        // When/Then: testar com todos os filtros possíveis
        mockMvc.perform(get("/v1/ninjas")
                        .param("nome", "Naruto")
                        .param("vila", "Konoha")
                        .param("cla", "Uzumaki")
                        .param("rank", "Hokage")
                        .param("chakra_tipo", "Vento")
                        .param("especialidade", "Ninjutsu")
                        .param("kekkei_genkai", "Kurama")
                        .param("status", "Ativo")
                        .param("nivel_forca", "98"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @DisplayName("POST /v1/ninjas - Deve criar ninja com dados válidos completos")
    void deveCrearNinjaComDadosValidosCompletos() throws Exception {
        // Given: request com todos os dados válidos obrigatórios
        NinjaRequest requestCompleto = new NinjaRequest(
                "Sasuke Uchiha",
                "Konoha",
                "Uchiha",
                "Jounin", // rank válido
                "Fogo", // chakraTipo obrigatório
                "Ninjutsu",
                "Sharingan",
                "Ativo", // status válido
                95,
                LocalDate.of(2024, 1, 1)
        );

        NinjaEntity sasuke = new NinjaEntity();
        sasuke.setId(2);
        sasuke.setNome("Sasuke Uchiha");
        sasuke.setVila("Konoha");
        sasuke.setCla("Uchiha");
        sasuke.setRank("Jounin");
        sasuke.setChakraTipo("Fogo");
        sasuke.setEspecialidade("Ninjutsu");
        sasuke.setKekkeiGenkai("Sharingan");
        sasuke.setStatus("Ativo");
        sasuke.setNivelForca(95);
        sasuke.setDataRegistro(LocalDate.of(2024, 1, 1));

        when(ninjaService.create(any(NinjaRequest.class))).thenReturn(sasuke);

        // When/Then: fazer requisição com dados válidos completos
        mockMvc.perform(post("/v1/ninjas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCompleto)))
                .andDo(print())
                .andExpect(status().isCreated()) // Status 201 - dados válidos
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.nome", is("Sasuke Uchiha")))
                .andExpect(jsonPath("$.rank", is("Jounin")));
    }
}
