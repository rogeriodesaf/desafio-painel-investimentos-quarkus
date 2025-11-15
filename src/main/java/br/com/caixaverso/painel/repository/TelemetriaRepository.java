package br.com.caixaverso.painel.repository;

import br.com.caixaverso.painel.model.Telemetria;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TelemetriaRepository implements PanacheRepository<Telemetria> {

    // Buscar telemetria de um serviço específico
    public Telemetria findByServico(String servico) {
        return find("servico", servico).firstResult();
    }
}
