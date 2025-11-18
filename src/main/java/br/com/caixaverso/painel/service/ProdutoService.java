package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.ProdutoResponseDTO;
import br.com.caixaverso.painel.model.Produto;
import br.com.caixaverso.painel.repository.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoRepository produtoRepository;

    public List<ProdutoResponseDTO> recomendarPorPerfil(String perfil) {
        if (perfil == null || perfil.isBlank()) {
            throw new IllegalArgumentException("perfil é obrigatório.");
        }

        String p = perfil.trim().toUpperCase(Locale.ROOT);

        List<Produto> todos = produtoRepository.listAll();

        List<Produto> filtrados = switch (p) {
            case "CONSERVADOR" -> filtrarConservador(todos);
            case "MODERADO"    -> filtrarModerado(todos);
            case "AGRESSIVO"   -> filtrarAgressivo(todos);
            default -> throw new IllegalArgumentException(
                    "Perfil inválido. Use Conservador, Moderado ou Agressivo.");
        };

        return filtrados.stream()
                .sorted(Comparator.comparingDouble(
                        (Produto prod) -> prod.getRentabilidade() != null
                                ? prod.getRentabilidade()
                                : 0.0
                ).reversed())
                .map(prod -> new ProdutoResponseDTO(
                        prod.getId(),
                        prod.getNome(),
                        prod.getTipo(),
                        prod.getRentabilidade() != null ? prod.getRentabilidade() : 0.0,
                        prod.getRisco()
                ))
                .toList();
    }

    private List<Produto> filtrarConservador(List<Produto> todos) {
        return todos.stream()
                .filter(p -> {
                    String risco = p.getRisco();
                    return risco != null && risco.equalsIgnoreCase("Baixo");
                })
                .toList();
    }

    private List<Produto> filtrarModerado(List<Produto> todos) {
        return todos.stream()
                .filter(p -> {
                    String risco = p.getRisco();
                    if (risco == null) return false;
                    String r = risco.toUpperCase(Locale.ROOT);
                    return r.equals("BAIXO") || r.equals("MÉDIO") || r.equals("MEDIO");
                })
                .toList();
    }

    private List<Produto> filtrarAgressivo(List<Produto> todos) {
        return todos.stream()
                .filter(p -> {
                    String risco = p.getRisco();
                    if (risco == null) return false;
                    return risco.equalsIgnoreCase("Alto");
                })
                .toList();
    }
}
