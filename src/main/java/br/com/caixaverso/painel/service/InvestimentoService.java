package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.model.Cliente;
import br.com.caixaverso.painel.model.Investimento;
import br.com.caixaverso.painel.repository.ClienteRepository;
import br.com.caixaverso.painel.repository.InvestimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Service responsável por fornecer os investimentos associados a um cliente.
 *
 * No PDF do desafio, existe o endpoint:
 *
 *     GET /investimentos/{clienteId}
 *
 * cujo retorno consiste em uma lista de investimentos contendo:
 *  - id
 *  - tipo
 *  - valor
 *  - rentabilidade
 *  - data
 *
 * Este service NÃO cria novos investimentos.
 * Ele apenas faz:
 *  1. validar o parâmetro
 *  2. verificar se o cliente existe
 *  3. consultar o repositório
 *  4. devolver a lista encontrada
 *
 * A responsabilidade da criação de investimentos é do fluxo de simulação,
 * executado em outro service (SimulacaoService).
 */
@ApplicationScoped
public class InvestimentoService {

    @Inject
    InvestimentoRepository investimentoRepository;

    @Inject
    ClienteRepository clienteRepository;

    /**
     * Lista todos os investimentos associados a um cliente.
     *
     * Passos realizados:
     *  1. valida clienteId
     *  2. verifica existência do cliente no banco
     *  3. consulta a tabela investimento filtrando por cliente_id
     *  4. retorna a lista (pode estar vazia)
     *
     * @param clienteId identificador do cliente
     * @return lista de investimentos válidos ou vazia
     */
    public List<Investimento> listarPorCliente(Long clienteId) {

        /** 1. Validação do parâmetro */
        if (clienteId == null) {
            throw new IllegalArgumentException("clienteId não pode ser nulo.");
        }

        /** 2. Verifica se o cliente existe */
        Cliente cliente = clienteRepository.findById(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado para o id: " + clienteId);
        }

        /**
         * 3. Consulta ao repositório
         * O método findByClienteId() já precisa existir no InvestimentoRepository.
         * Ele retorna todos os investimentos associados ao cliente.
         */
        return investimentoRepository.findByClienteId(clienteId);
    }
}
