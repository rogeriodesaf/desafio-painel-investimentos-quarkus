package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.model.Produto;
import br.com.caixaverso.painel.repository.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoRepository repository;

    // Lista todos os produtos
    public List<Produto> listarTodos() {
        return repository.listAll();
    }

    // Busca produtos recomendados conforme perfil
    public List<Produto> buscarPorPerfil(String perfil) {
        if (perfil == null || perfil.trim().isEmpty()) {
            throw new IllegalArgumentException("Perfil não pode ser nulo ou vazio.");
        }

        String perfilNormalizado = perfil.trim().toLowerCase();

        switch (perfilNormalizado) {
            case "conservador":
                return repository.findByRisco("Baixo");

            case "moderado":
                // Baixo + Médio para perfil moderado
                return repository.list("risco in ('Baixo', 'Médio')");

            case "agressivo":
                // Médio + Alto para perfil agressivo
                return repository.list("risco in ('Médio', 'Alto')");

            default:
                // Pensando no desafio: rejeitar perfis inválidos claramente
                throw new IllegalArgumentException("Perfil inválido: " + perfil +
                        ". Valores aceitos: conservador, moderado, agressivo.");
        }
    }

    // Busca produto pelo ID (usado em simulação)
    public Produto buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id do produto não pode ser nulo.");
        }

        Produto produto = repository.findById(id);

        if (produto == null) {
            // Aqui evitamos NullPointerException lá na frente
            // e deixamos bem claro o problema
            throw new IllegalArgumentException("Produto não encontrado para o id: " + id);
        }

        return produto;
    }
}
