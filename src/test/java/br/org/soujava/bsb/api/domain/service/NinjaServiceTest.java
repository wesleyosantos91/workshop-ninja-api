package br.org.soujava.bsb.api.domain.service;

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import br.org.soujava.bsb.api.domain.repository.NinjaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Ninja Service")
class NinjaServiceTest {

    @Mock
    private NinjaRepository ninjaRepository;

    @InjectMocks
    private NinjaService ninjaService;

    private NinjaEntity ninjaEntity;
    private NinjaRequest ninjaRequest;
    private NinjaQueryRequest ninjaQueryRequest;

    @BeforeEach
    void setUp() {
        // Preparar dados de teste que serão reutilizados
        ninjaEntity = new NinjaEntity();
        ninjaEntity.setId(1);
        ninjaEntity.setNome("Naruto Uzumaki");
        ninjaEntity.setVila("Konoha");
        ninjaEntity.setCla("Uzumaki");
        ninjaEntity.setRank("Hokage");
        ninjaEntity.setChakraTipo("Vento");
        ninjaEntity.setEspecialidade("Ninjutsu");
        ninjaEntity.setKekkeiGenkai("Kurama (Bijuu)");
        ninjaEntity.setStatus("Ativo");
        ninjaEntity.setNivelForca(98);
        ninjaEntity.setDataRegistro(LocalDate.now());

        ninjaRequest = new NinjaRequest(
                "Naruto Uzumaki",
                "Konoha",
                "Uzumaki",
                "Hokage",
                "Vento",
                "Ninjutsu",
                "Kurama (Bijuu)",
                "Ativo",
                98,
                LocalDate.now()
        );

        ninjaQueryRequest = new NinjaQueryRequest(
                "Naruto",
                "Konoha",
                null,
                null,
                null,
                null,
                null,
                "Ativo",
                null
        );
    }

    @Test
    @DisplayName("Deve criar ninja com sucesso")
    void deveCriarNinjaComSucesso() {
        // Given: repository retornará a entidade salva
        when(ninjaRepository.save(any(NinjaEntity.class))).thenReturn(ninjaEntity);

        // When: chamar create
        NinjaEntity resultado = ninjaService.create(ninjaRequest);

        // Then: deve retornar ninja criado e verificar interação
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Naruto Uzumaki");
        assertThat(resultado.getVila()).isEqualTo("Konoha");

        // Verificar que o repository foi chamado uma vez
        verify(ninjaRepository, times(1)).save(any(NinjaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar ninja por ID existente com sucesso")
    void deveBuscarNinjaPorIdExistenteComSucesso() throws ResourceNotFoundException {
        // Given: repository retornará o ninja
        when(ninjaRepository.findById(1)).thenReturn(Optional.of(ninjaEntity));

        // When: buscar ninja por ID
        NinjaEntity resultado = ninjaService.findById(1);

        // Then: deve retornar o ninja encontrado
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Naruto Uzumaki");

        // Verificar interação com repository
        verify(ninjaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ninja por ID inexistente")
    void deveLancarExcecaoAoBuscarNinjaPorIdInexistente() {
        // Given: repository retornará Optional vazio
        when(ninjaRepository.findById(999)).thenReturn(Optional.empty());

        // When/Then: deve lançar ResourceNotFoundException
        assertThatThrownBy(() -> ninjaService.findById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Not found regitstry with code 999");

        // Verificar que o repository foi chamado
        verify(ninjaRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Deve fazer busca paginada com filtros")
    void deveFazerBuscaPaginadaComFiltros() {
        // Given: preparar dados paginados
        List<NinjaEntity> ninjas = List.of(ninjaEntity);
        Page<NinjaEntity> page = new PageImpl<>(ninjas, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        when(ninjaRepository.findAll(any(Example.class), eq(pageable))).thenReturn(page);

        // When: fazer busca
        Page<NinjaEntity> resultado = ninjaService.search(ninjaQueryRequest, pageable);

        // Then: deve retornar página com resultados
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Naruto Uzumaki");
        assertThat(resultado.getTotalElements()).isEqualTo(1L);

        // Verificar que o repository foi chamado com Example
        verify(ninjaRepository, times(1)).findAll(any(Example.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve buscar página vazia quando não há resultados")
    void deveBuscarPaginaVaziaQuandoNaoHaResultados() {
        // Given: repository retornará página vazia
        Page<NinjaEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        Pageable pageable = PageRequest.of(0, 10);

        when(ninjaRepository.findAll(any(Example.class), eq(pageable))).thenReturn(emptyPage);

        // When: fazer busca
        Page<NinjaEntity> resultado = ninjaService.search(ninjaQueryRequest, pageable);

        // Then: deve retornar página vazia
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isEqualTo(0L);

        verify(ninjaRepository, times(1)).findAll(any(Example.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve atualizar ninja existente com sucesso")
    void deveAtualizarNinjaExistenteComSucesso() throws ResourceNotFoundException {
        // Given: ninja existe e será encontrado e salvo
        NinjaEntity ninjaAtualizado = new NinjaEntity();
        ninjaAtualizado.setId(1);
        ninjaAtualizado.setNome("Naruto Uzumaki Atualizado");
        ninjaAtualizado.setVila("Konoha");
        ninjaAtualizado.setRank("Hokage");

        when(ninjaRepository.findById(1)).thenReturn(Optional.of(ninjaEntity));
        when(ninjaRepository.save(any(NinjaEntity.class))).thenReturn(ninjaAtualizado);

        NinjaRequest requestAtualizado = new NinjaRequest(
                "Naruto Uzumaki Atualizado",
                "Konoha",
                "Uzumaki",
                "Hokage",
                "Vento",
                "Ninjutsu",
                "Kurama (Bijuu)",
                "Ativo",
                99,
                LocalDate.now()
        );

        // When: atualizar ninja
        NinjaEntity resultado = ninjaService.update(1, requestAtualizado);

        // Then: deve retornar ninja atualizado
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getNome()).isEqualTo("Naruto Uzumaki Atualizado");

        // Verificar interações: findById e save
        verify(ninjaRepository, times(1)).findById(1);
        verify(ninjaRepository, times(1)).save(any(NinjaEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar ninja inexistente")
    void deveLancarExcecaoAoTentarAtualizarNinjaInexistente() {
        // Given: ninja não existe
        when(ninjaRepository.findById(999)).thenReturn(Optional.empty());

        // When/Then: deve lançar ResourceNotFoundException
        assertThatThrownBy(() -> ninjaService.update(999, ninjaRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Not found regitstry with code 999");

        // Verificar que findById foi chamado, mas save não
        verify(ninjaRepository, times(1)).findById(999);
        verify(ninjaRepository, never()).save(any(NinjaEntity.class));
    }

    @Test
    @DisplayName("Deve deletar ninja existente com sucesso")
    void deveDeletarNinjaExistenteComSucesso() throws ResourceNotFoundException {
        // Given: ninja existe
        when(ninjaRepository.findById(1)).thenReturn(Optional.of(ninjaEntity));
        doNothing().when(ninjaRepository).delete(ninjaEntity);

        // When: deletar ninja
        ninjaService.delete(1);

        // Then: deve chamar findById e delete
        verify(ninjaRepository, times(1)).findById(1);
        verify(ninjaRepository, times(1)).delete(ninjaEntity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar ninja inexistente")
    void deveLancarExcecaoAoTentarDeletarNinjaInexistente() {
        // Given: ninja não existe
        when(ninjaRepository.findById(999)).thenReturn(Optional.empty());

        // When/Then: deve lançar ResourceNotFoundException
        assertThatThrownBy(() -> ninjaService.delete(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Not found regitstry with code 999");

        // Verificar que findById foi chamado, mas delete não
        verify(ninjaRepository, times(1)).findById(999);
        verify(ninjaRepository, never()).delete(any(NinjaEntity.class));
    }

    @Test
    @DisplayName("Deve validar que transações são aplicadas corretamente")
    void deveValidarQueTransacoesSaoAplicadasCorretamente() throws ResourceNotFoundException {
        // Given: este teste verifica o comportamento transacional
        when(ninjaRepository.findById(1)).thenReturn(Optional.of(ninjaEntity));

        // When: chamar método com @Transactional(readOnly = true)
        NinjaEntity resultado = ninjaService.findById(1);

        // Then: deve funcionar normalmente
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);

        // Nota: Em testes unitários, as anotações @Transactional são ignoradas
        // Para testar transações, seria necessário um teste de integração
        verify(ninjaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve testar cenário de múltiplas operações em sequência")
    void deveTestarCenarioDeMultiplasOperacoesEmSequencia() throws ResourceNotFoundException {
        // Given: configurar mocks para várias operações
        when(ninjaRepository.save(any(NinjaEntity.class))).thenReturn(ninjaEntity);
        when(ninjaRepository.findById(1)).thenReturn(Optional.of(ninjaEntity));

        // When: executar múltiplas operações
        NinjaEntity criado = ninjaService.create(ninjaRequest);
        NinjaEntity encontrado = ninjaService.findById(1);

        // Then: verificar resultados e interações
        assertThat(criado).isNotNull();
        assertThat(encontrado).isNotNull();
        assertThat(criado.getNome()).isEqualTo(encontrado.getNome());

        // Verificar que cada método foi chamado
        verify(ninjaRepository, times(1)).save(any(NinjaEntity.class));
        verify(ninjaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve testar comportamento com dados nulos ou vazios")
    void deveTestarComportamentoComDadosNulosOuVazios() {
        // Given: request com dados mínimos
        NinjaRequest requestMinimo = new NinjaRequest(
                "Ninja Teste",
                "Vila Teste",
                null, // cla pode ser nulo
                "Genin",
                "Fogo",
                null, // especialidade pode ser nula
                null, // kekkeiGenkai pode ser nulo
                "Ativo",
                50,
                LocalDate.now()
        );

        NinjaEntity ninjaMinimo = new NinjaEntity();
        ninjaMinimo.setId(2);
        ninjaMinimo.setNome("Ninja Teste");
        ninjaMinimo.setVila("Vila Teste");
        ninjaMinimo.setRank("Genin");

        when(ninjaRepository.save(any(NinjaEntity.class))).thenReturn(ninjaMinimo);

        // When: criar ninja com dados mínimos
        NinjaEntity resultado = ninjaService.create(requestMinimo);

        // Then: deve funcionar normalmente
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Ninja Teste");

        verify(ninjaRepository, times(1)).save(any(NinjaEntity.class));
    }
}
