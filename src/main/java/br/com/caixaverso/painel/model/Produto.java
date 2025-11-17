package br.com.caixaverso.painel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String tipo;
    private Double rentabilidade;
    private String risco;

    // Parâmetros de elegibilidade
    private Double valorMinimo;
    private Double valorMaximo;
    private Integer prazoMinMeses;
    private Integer prazoMaxMeses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getRentabilidade() {
        return rentabilidade;
    }

    public void setRentabilidade(Double rentabilidade) {
        this.rentabilidade = rentabilidade;
    }

    public String getRisco() {
        return risco;
    }

    public void setRisco(String risco) {
        this.risco = risco;
    }

    public Double getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(Double valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public Double getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(Double valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public Integer getPrazoMinMeses() {
        return prazoMinMeses;
    }

    public void setPrazoMinMeses(Integer prazoMinMeses) {
        this.prazoMinMeses = prazoMinMeses;
    }

    public Integer getPrazoMaxMeses() {
        return prazoMaxMeses;
    }

    public void setPrazoMaxMeses(Integer prazoMaxMeses) {
        this.prazoMaxMeses = prazoMaxMeses;
    }
}
