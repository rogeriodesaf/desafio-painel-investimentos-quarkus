package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.InvestimentoResponseDTO;
import br.com.caixaverso.painel.model.Investimento;
import br.com.caixaverso.painel.repository.InvestimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class InvestimentoService {

    @Inject
    InvestimentoRepository investimentoRepository;

    public List<InvestimentoResponseDTO> listarPorCliente(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("clienteId é obrigatório.");
        }

        List<Investimento> investimentos = investimentoRepository
                .find("clienteId", clienteId)
                .list();

        if (investimentos == null || investimentos.isEmpty()) {
            // Deixa o endpoint retornar lista vazia — isso é ok pro desafio
            return List.of();
        }

        return investimentos.stream()
                .map(inv -> new InvestimentoResponseDTO(
                        inv.getId(),
                        inv.getTipo(),
                        inv.getValor() != null ? inv.getValor() : 0.0,
                        inv.getRentabilidade() != null ? inv.getRentabilidade() : 0.0,
                        inv.getData()
                ))
                .toList();
    }
}
