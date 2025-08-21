package br.org.soujava.bsb.api.domain.repository;

import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Ninja Repository")
class NinjaRepositoryTest {

    @Autowired
    private NinjaRepository ninjaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve carregar os dados iniciais do data.sql")
    void deveCarregarDadosIniciais() {
        // Given: dados já carregados pelo data.sql

        // When: buscar todos os ninjas
        List<NinjaEntity> ninjas = ninjaRepository.findAll();

        // Then: deve ter os 4 ninjas iniciais
        assertThat(ninjas).hasSize(4);
        assertThat(ninjas)
            .extracting(NinjaEntity::getNome)
            .containsExactlyInAnyOrder(
                "Naruto Uzumaki",
                "Sasuke Uchiha",
                "Sakura Haruno",
                "Gaara"
            );
    }

    @Test
    @DisplayName("Deve buscar ninja por ID existente")
    void deveBuscarNinjaPorIdExistente() {
        // Given: ID de um ninja existente (Naruto é geralmente ID 1)
        Integer idNaruto = 1;

        // When: buscar por ID
        Optional<NinjaEntity> ninjaEncontrado = ninjaRepository.findById(idNaruto);

        // Then: deve encontrar o ninja
        assertThat(ninjaEncontrado).isPresent();
        assertThat(ninjaEncontrado.get().getNome()).isEqualTo("Naruto Uzumaki");
        assertThat(ninjaEncontrado.get().getVila()).isEqualTo("Konoha");
        assertThat(ninjaEncontrado.get().getRank()).isEqualTo("Hokage");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar ninja por ID inexistente")
    void deveRetornarVazioParaIdInexistente() {
        // Given: ID que não existe
        Integer idInexistente = 999;

        // When: buscar por ID inexistente
        Optional<NinjaEntity> ninja = ninjaRepository.findById(idInexistente);

        // Then: deve retornar Optional vazio
        assertThat(ninja).isEmpty();
    }

    @Test
    @DisplayName("Deve salvar novo ninja com sucesso")
    void deveSalvarNovoNinja() {
        // Given: novo ninja para salvar
        NinjaEntity novoNinja = new NinjaEntity();
        novoNinja.setNome("Kakashi Hatake");
        novoNinja.setVila("Konoha");
        novoNinja.setCla("Hatake");
        novoNinja.setRank("Jounin");
        novoNinja.setChakraTipo("Raio");
        novoNinja.setEspecialidade("Ninjutsu");
        novoNinja.setKekkeiGenkai("Sharingan");
        novoNinja.setStatus("Ativo");
        novoNinja.setNivelForca(92);
        novoNinja.setDataRegistro(LocalDate.now());

        // When: salvar ninja
        NinjaEntity ninjaSalvo = ninjaRepository.save(novoNinja);

        // Then: deve salvar com ID gerado
        assertThat(ninjaSalvo.getId()).isNotNull();
        assertThat(ninjaSalvo.getNome()).isEqualTo("Kakashi Hatake");

        // Verificar se foi realmente persistido
        Optional<NinjaEntity> ninjaVerificacao = ninjaRepository.findById(ninjaSalvo.getId());
        assertThat(ninjaVerificacao).isPresent();
        assertThat(ninjaVerificacao.get().getNome()).isEqualTo("Kakashi Hatake");
    }

    @Test
    @DisplayName("Deve atualizar ninja existente")
    void deveAtualizarNinjaExistente() {
        // Given: buscar ninja existente para atualizar
        NinjaEntity sasuke = ninjaRepository.findById(2).orElseThrow();
        String statusOriginal = sasuke.getStatus();

        // When: atualizar status do Sasuke para "Ativo" (redenção!)
        sasuke.setStatus("Ativo");
        sasuke.setRank("Hokage"); // Promoção!
        NinjaEntity sasukeAtualizado = ninjaRepository.save(sasuke);

        // Then: deve atualizar sem criar novo registro
        assertThat(sasukeAtualizado.getId()).isEqualTo(2);
        assertThat(sasukeAtualizado.getStatus()).isEqualTo("Ativo");
        assertThat(sasukeAtualizado.getRank()).isEqualTo("Hokage");
        assertThat(sasukeAtualizado.getStatus()).isNotEqualTo(statusOriginal);
    }

    @Test
    @DisplayName("Deve deletar ninja por ID")
    void deveDeletarNinjaPorId() {
        // Given: criar ninja temporário para deletar
        NinjaEntity ninjaTemp = new NinjaEntity();
        ninjaTemp.setNome("Ninja Temporário");
        ninjaTemp.setVila("Vila Teste");
        ninjaTemp.setRank("Genin");
        ninjaTemp.setChakraTipo("Agua");
        ninjaTemp = ninjaRepository.save(ninjaTemp);
        Integer idParaDeletar = ninjaTemp.getId();

        // Verificar que existe
        assertThat(ninjaRepository.findById(idParaDeletar)).isPresent();

        // When: deletar ninja
        ninjaRepository.deleteById(idParaDeletar);

        // Then: não deve mais existir
        assertThat(ninjaRepository.findById(idParaDeletar)).isEmpty();
    }

    @Test
    @DisplayName("Deve contar total de ninjas")
    void deveContarTotalDeNinjas() {
        // When: contar ninjas
        long totalNinjas = ninjaRepository.count();

        // Then: deve ter pelo menos os 4 iniciais
        assertThat(totalNinjas).isGreaterThanOrEqualTo(4L);
    }

    @Test
    @DisplayName("Deve verificar se ninja existe por ID")
    void deveVerificarSeNinjaExiste() {
        // Given: IDs para teste
        Integer idExistente = 1;
        Integer idInexistente = 999;

        // When/Then: verificar existência
        assertThat(ninjaRepository.existsById(idExistente)).isTrue();
        assertThat(ninjaRepository.existsById(idInexistente)).isFalse();
    }

    @Test
    @DisplayName("Deve salvar múltiplos ninjas em lote")
    void deveSalvarMultiplosNinjasEmLote() {
        // Given: lista de novos ninjas
        NinjaEntity ninja1 = criarNinja("Rock Lee", "Konoha", "Jounin");
        NinjaEntity ninja2 = criarNinja("Neji Hyuga", "Konoha", "Jounin");
        NinjaEntity ninja3 = criarNinja("Tenten", "Konoha", "Jounin");

        List<NinjaEntity> novosNinjas = List.of(ninja1, ninja2, ninja3);

        // When: salvar em lote
        List<NinjaEntity> ninjasSalvos = ninjaRepository.saveAll(novosNinjas);

        // Then: todos devem ter IDs gerados
        assertThat(ninjasSalvos).hasSize(3);
        assertThat(ninjasSalvos).allMatch(ninja -> ninja.getId() != null);
        assertThat(ninjasSalvos)
            .extracting(NinjaEntity::getNome)
            .containsExactly("Rock Lee", "Neji Hyuga", "Tenten");
    }

    @Test
    @DisplayName("Deve usar TestEntityManager para operações de baixo nível")
    void deveUsarTestEntityManagerParaOperacoesBaixoNivel() {
        // Given: criar ninja usando EntityManager
        NinjaEntity ninja = criarNinja("Jiraiya", "Konoha", "Sannin");

        // When: persistir e flush usando TestEntityManager
        NinjaEntity ninjaPersistido = entityManager.persistAndFlush(ninja);

        // Clear do contexto para forçar busca no banco
        entityManager.clear();

        // Then: buscar usando repository deve encontrar
        Optional<NinjaEntity> ninjaEncontrado = ninjaRepository.findById(ninjaPersistido.getId());
        assertThat(ninjaEncontrado).isPresent();
        assertThat(ninjaEncontrado.get().getNome()).isEqualTo("Jiraiya");
    }

    /**
     * Método helper para criar ninjas nos testes
     */
    private NinjaEntity criarNinja(String nome, String vila, String rank) {
        NinjaEntity ninja = new NinjaEntity();
        ninja.setNome(nome);
        ninja.setVila(vila);
        ninja.setRank(rank);
        ninja.setChakraTipo("Fogo");
        ninja.setStatus("Ativo");
        ninja.setNivelForca(80);
        ninja.setDataRegistro(LocalDate.now());
        return ninja;
    }
}
