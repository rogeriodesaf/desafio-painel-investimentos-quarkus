package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.InvestimentoResponseDTO;
import br.com.caixaverso.painel.model.Cliente;
import br.com.caixaverso.painel.model.Investimento;
import br.com.caixaverso.painel.repository.ClienteRepository;
import br.com.caixaverso.painel.repository.InvestimentoRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class InvestimentoService {

    @Inject
    InvestimentoRepository investimentoRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    TelemetriaService telemetriaService;

    public List<InvestimentoResponseDTO> listarPorCliente(Long clienteId) {

        long inicio = System.currentTimeMillis();

        try {

            if (clienteId == null) {
                throw new IllegalArgumentException("clienteId é obrigatório.");
            }

            Cliente cliente = clienteRepository.findById(clienteId);
            if (cliente == null) {
                throw new NotFoundException("Cliente não encontrado para o id: " + clienteId);
            }

            List<Investimento> investimentos = investimentoRepository
                    .find("clienteId", clienteId)
                    .list();

            // Cliente existe mas não tem investimentos
            if (investimentos == null || investimentos.isEmpty()) {
                return List.of();  // 200 OK + lista vazia
            }

            // Converter para DTO
            return investimentos.stream()
                    .map(inv -> new InvestimentoResponseDTO(
                            inv.getId(),
                            inv.getTipo(),
                            inv.getValor() != null ? inv.getValor() : 0.0,
                            inv.getRentabilidade() != null ? inv.getRentabilidade() : 0.0,
                            inv.getData()
                    ))
                    .toList();

        } finally {
            long fim = System.currentTimeMillis();
            telemetriaService.registrar("investimentos", fim - inicio);
        }
    }
}
