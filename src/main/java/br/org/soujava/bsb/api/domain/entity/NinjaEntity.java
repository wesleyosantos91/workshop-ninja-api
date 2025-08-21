package br.org.soujava.bsb.api.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "NINJA")
public class NinjaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NINJA", nullable = false)
    private Integer id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "VILA", nullable = false, length = 50)
    private String vila;

    @Column(name = "CLA", length = 50)
    private String cla;

    @Column(name = "RANK", nullable = false, length = 20)
    private String rank;

    @Column(name = "CHAKRA_TIPO", nullable = false, length = 30)
    private String chakraTipo;

    @Column(name = "ESPECIALIDADE", length = 50)
    private String especialidade;

    @Column(name = "KEKKEI_GENKAI", length = 50)
    private String kekkeiGenkai;

    @ColumnDefault("'Ativo'")
    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "NIVEL_FORCA")
    private Integer nivelForca;

    @ColumnDefault("CURRENT_DATE")
    @Column(name = "DATA_REGISTRO")
    private LocalDate dataRegistro;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getVila() {
        return vila;
    }

    public void setVila(String vila) {
        this.vila = vila;
    }

    public String getCla() {
        return cla;
    }

    public void setCla(String cla) {
        this.cla = cla;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getChakraTipo() {
        return chakraTipo;
    }

    public void setChakraTipo(String chakraTipo) {
        this.chakraTipo = chakraTipo;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getKekkeiGenkai() {
        return kekkeiGenkai;
    }

    public void setKekkeiGenkai(String kekkeiGenkai) {
        this.kekkeiGenkai = kekkeiGenkai;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getNivelForca() {
        return nivelForca;
    }

    public void setNivelForca(Integer nivelForca) {
        this.nivelForca = nivelForca;
    }

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDate dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

}