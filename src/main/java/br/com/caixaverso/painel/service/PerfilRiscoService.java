package br.com.caixaverso.painel.service;

import br.com.caixaverso.painel.dto.PerfilRiscoResponseDTO;
import br.com.caixaverso.painel.model.Cliente;
import br.com.caixaverso.painel.model.Investimento;
import br.com.caixaverso.painel.repository.ClienteRepository;
import br.com.caixaverso.painel.repository.InvestimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class PerfilRiscoService {

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    InvestimentoRepository investimentoRepository;

    public PerfilRiscoResponseDTO calcularPerfil(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("clienteId é obrigatório.");
        }

        Cliente cliente = clienteRepository.findById(clienteId);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado para o id: " + clienteId);
        }

        List<Investimento> investimentos = investimentoRepository
                .find("clienteId", clienteId)
                .list();

        if (investimentos == null || investimentos.isEmpty()) {
            int pontuacao = 20;
            String perfil = "Conservador";
            String descricao = "Baixo volume de investimentos e pouca movimentação. Perfil conservador, com foco em segurança e liquidez.";
            return new PerfilRiscoResponseDTO(clienteId, perfil, pontuacao, descricao);
        }

        PerfilRiscoScore score = calcularScores(cliente, investimentos);

        String perfil;
        String descricao;

        if (score.pontuacaoFinal() <= 40) {
            perfil = "Conservador";
            descricao = "Perfil conservador, com baixa movimentação e foco em liquidez.";
        } else if (score.pontuacaoFinal() <= 70) {
            perfil = "Moderado";
            descricao = "Perfil equilibrado entre liquidez e rentabilidade.";
        } else {
            perfil = "Agressivo";
            descricao = "Perfil que busca alta rentabilidade, aceitando maior risco.";
        }

        return new PerfilRiscoResponseDTO(clienteId, perfil, score.pontuacaoFinal(), descricao);
    }

    private PerfilRiscoScore calcularScores(Cliente cliente, List<Investimento> investimentos) {
        double volumeScore = calcularScoreVolume(cliente, investimentos);
        double frequenciaScore = calcularScoreFrequencia(cliente, investimentos);
        double preferenciaScore = calcularScorePreferencia(cliente, investimentos);

        int pontuacaoFinal = (int) Math.round((volumeScore + frequenciaScore + preferenciaScore) / 3.0);

        return new PerfilRiscoScore(volumeScore, frequenciaScore, preferenciaScore, pontuacaoFinal);
    }

    private double calcularScoreVolume(Cliente cliente, List<Investimento> investimentos) {
        double totalInvestidoHistorico = investimentos.stream()
                .mapToDouble(inv -> inv.getValor() != null ? inv.getValor() : 0.0)
                .sum();

        double totalCliente = cliente.getValorTotalInvestido() != null
                ? cliente.getValorTotalInvestido()
                : 0.0;

        double volumeTotal = totalInvestidoHistorico + totalCliente;

        if (volumeTotal <= 10_000) {
            return 20.0;
        } else if (volumeTotal <= 50_000) {
            return 50.0;
        } else if (volumeTotal <= 200_000) {
            return 75.0;
        } else {
            return 90.0;
        }
    }

    private double calcularScoreFrequencia(Cliente cliente, List<Investimento> investimentos) {
        Integer movMensal = cliente.getMovimentacaoMensal();
        int movimentacaoDeclarada = movMensal != null ? movMensal : 0;

        LocalDate hoje = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

        long qtdUltimos12Meses = investimentos.stream()
                .filter(inv -> {
                    try {
                        String dataStr = inv.getData();
                        if (dataStr == null || dataStr.isBlank()) {
                            return false;
                        }
                        LocalDate data = LocalDate.parse(dataStr.substring(0, 10), fmt);
                        return !data.isBefore(hoje.minusMonths(12));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        int freqCombinada = movimentacaoDeclarada + (int) qtdUltimos12Meses;

        if (freqCombinada <= 3) {
            return 20.0;
        } else if (freqCombinada <= 8) {
            return 50.0;
        } else if (freqCombinada <= 20) {
            return 70.0;
        } else {
            return 90.0;
        }
    }

    private double calcularScorePreferencia(Cliente cliente, List<Investimento> investimentos) {
        long qtdLiquidez = investimentos.stream()
                .filter(inv -> {
                    String tipo = inv.getTipo();
                    if (tipo == null) return false;
                    String t = tipo.toUpperCase();
                    return t.contains("CDB")
                            || t.contains("TESOURO")
                            || t.contains("LCI")
                            || t.contains("LCA");
                })
                .count();

        long qtdRentabilidade = investimentos.stream()
                .filter(inv -> {
                    String tipo = inv.getTipo();
                    if (tipo == null) return false;
                    String t = tipo.toUpperCase();
                    return t.contains("FUNDO")
                            || t.contains("MULTIMERCADO")
                            || t.contains("ACOES")
                            || t.contains("AÇÕES");
                })
                .count();

        double total = investimentos.size();
        double pesoLiquidez = total > 0 ? (qtdLiquidez / total) : 0.0;
        double pesoRentabilidade = total > 0 ? (qtdRentabilidade / total) : 0.0;

        double mediaRentabilidade = investimentos.stream()
                .mapToDouble(inv -> inv.getRentabilidade() != null ? inv.getRentabilidade() : 0.0)
                .average()
                .orElse(0.0);

        Integer prefereLiquidez = cliente.getPrefereLiquidez();
        boolean declaracaoLiquidez = prefereLiquidez != null && prefereLiquidez == 1;

        if (declaracaoLiquidez && pesoLiquidez >= 0.6 && mediaRentabilidade < 0.12) {
            return 25.0;
        } else if (!declaracaoLiquidez && pesoRentabilidade >= 0.6 && mediaRentabilidade >= 0.15) {
            return 85.0;
        } else {
            return 55.0;
        }
    }
}
