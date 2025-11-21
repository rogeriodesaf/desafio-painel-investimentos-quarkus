package br.com.caixaverso.painel.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class TelemetriaResourceTest {

    // ------------------------------------------------------------
    // TESTE 1 — Admin tem acesso (200)
    // ------------------------------------------------------------
    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testTelemetriaAdmin() {

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/telemetria")
                .then()
                .statusCode(200)
                .body("servicos", isA(java.util.List.class))
                .body("periodo.inicio", anyOf(isA(String.class), is(nullValue())))
                .body("periodo.fim", isA(String.class));
    }

    // ------------------------------------------------------------
    // TESTE 2 — Usuário comum NÃO tem acesso (403)
    // ------------------------------------------------------------
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testTelemetriaUserForbidden() {

        given()
                .when()
                .get("/telemetria")
                .then()
                .statusCode(403);
    }

    // ------------------------------------------------------------
    // TESTE 3 — Sem token deve retornar 401
    // ------------------------------------------------------------
    @Test
    public void testTelemetriaSemToken() {

        given()
                .when()
                .get("/telemetria")
                .then()
                .statusCode(401);
    }

    // ------------------------------------------------------------
    // TESTE 4 — Estrutura dos serviços
    // ------------------------------------------------------------
    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testEstruturaServicos() {

        given()
                .when()
                .get("/telemetria")
                .then()
                .statusCode(200)
                .body("servicos.size()", greaterThanOrEqualTo(0))
                .body("servicos[0].nome", anyOf(isA(String.class), is(nullValue())))
                .body("servicos[0].quantidadeChamadas", anyOf(isA(Integer.class), is(nullValue())))
                .body("servicos[0].mediaTempoRespostaMs",
                        anyOf(isA(Float.class), isA(Double.class), isA(Integer.class), is(nullValue())));


    }
}
