package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.SimulacaoPorProdutoDiaDTO;
import br.com.caixaverso.painel.model.SimulacaoDia;
import br.com.caixaverso.painel.repository.SimulacaoDiaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class SimulacaoDiaService {

    @Inject
    SimulacaoDiaRepository simulacaoDiaRepository;

    public List<SimulacaoPorProdutoDiaDTO> listarTudo() {
        return simulacaoDiaRepository.listAll().stream()
                .map(s -> new SimulacaoPorProdutoDiaDTO(
                        s.getProduto(),
                        s.getData(),
                        s.getQuantidadeSimulacoes(),
                        s.getMediaValorFinal()
                ))
                .toList();
    }

    @Transactional
    public void registrar(String produto, int quantidade, double media) {
        if (produto == null || produto.isBlank()) {
            throw new IllegalArgumentException("produto não pode ser vazio.");
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException("quantidadeSimulacoes deve ser >= 0");
        }
        if (media < 0) {
            throw new IllegalArgumentException("mediaValorFinal deve ser >= 0");
        }

        SimulacaoDia registro = new SimulacaoDia();
        registro.setProduto(produto);
        registro.setQuantidadeSimulacoes(quantidade);
        registro.setMediaValorFinal(media);

        String hoje = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        registro.setData(hoje);

        simulacaoDiaRepository.persist(registro);
    }
}
