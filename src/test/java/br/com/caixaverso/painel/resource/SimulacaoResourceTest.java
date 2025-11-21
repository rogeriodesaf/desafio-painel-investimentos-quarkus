package br.com.caixaverso.painel.resource;

import br.com.caixaverso.painel.dto.SimulacaoRequestDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class SimulacaoResourceTest {

    // ------------------------------------------------------------
    // TESTE 1 — POST /simular-investimento (sucesso)
    // ------------------------------------------------------------
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testSimularInvestimentoSucesso() {

        SimulacaoRequestDTO req = new SimulacaoRequestDTO(
                123L,
                10000.0,
                12,
                "CDB"
        );

        given()
                .contentType(ContentType.JSON)
                .body(req)
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(200)
                .body("produtoValidado", notNullValue())
                .body("resultadoSimulacao.valorFinal", notNullValue())
                .body("dataSimulacao", notNullValue());
    }

    // ------------------------------------------------------------
    // TESTE 2 — POST sem autenticação deve dar 401
    // ------------------------------------------------------------
    @Test
    public void testSimularInvestimentoSemToken() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/simular-investimento")
                .then()
                .statusCode(401);
    }

    // ------------------------------------------------------------
    // TESTE 3 — GET /simulacoes
    // ------------------------------------------------------------
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testListarSimulacoes() {
        given()
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    // ------------------------------------------------------------
    // TESTE 4 — GET /simulacoes/por-produto-dia
    // ------------------------------------------------------------
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testListarSimulacoesPorProdutoDia() {
        given()
                .when()
                .get("/simulacoes/por-produto-dia")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }
}
