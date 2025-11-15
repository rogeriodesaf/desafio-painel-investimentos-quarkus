package br.com.caixaverso.painel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer movimentacaoMensal;
    private Double valorTotalInvestido;
    private Integer prefereLiquidez; // 0 = não / 1 = sim

    // Getters e Setters

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

    public Integer getMovimentacaoMensal() {
        return movimentacaoMensal;
    }

    public void setMovimentacaoMensal(Integer movimentacaoMensal) {
        this.movimentacaoMensal = movimentacaoMensal;
    }

    public Double getValorTotalInvestido() {
        return valorTotalInvestido;
    }

    public void setValorTotalInvestido(Double valorTotalInvestido) {
        this.valorTotalInvestido = valorTotalInvestido;
    }

    public Integer getPrefereLiquidez() {
        return prefereLiquidez;
    }

    public void setPrefereLiquidez(Integer prefereLiquidez) {
        this.prefereLiquidez = prefereLiquidez;
    }
}
