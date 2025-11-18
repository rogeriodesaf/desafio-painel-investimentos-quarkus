package br.com.caixaverso.painel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "simulacao_dia")
public class SimulacaoDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String produto;

    @Column(name = "data")
    private String dataSimples;

    private Integer quantidadeSimulacoes;
    private Double mediaValorFinal;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getDataSimples() {
        return dataSimples;
    }

    public void setDataSimples(String dataSimples) {
        this.dataSimples = dataSimples;
    }

    public Integer getQuantidadeSimulacoes() {
        return quantidadeSimulacoes;
    }

    public void setQuantidadeSimulacoes(Integer quantidadeSimulacoes) {
        this.quantidadeSimulacoes = quantidadeSimulacoes;
    }

    public Double getMediaValorFinal() {
        return mediaValorFinal;
    }

    public void setMediaValorFinal(Double mediaValorFinal) {
        this.mediaValorFinal = mediaValorFinal;
    }
}
