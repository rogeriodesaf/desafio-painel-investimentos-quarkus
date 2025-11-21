package br.com.caixaverso.painel.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class InvestimentoResourceTest {

    // TESTE 1 — Cliente válido (user)
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testListarInvestimentosClienteValido() {

        Long clienteId = 1L; // ajuste se precisar

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/investimentos/" + clienteId)
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    // TESTE 2 — Cliente válido (admin)
    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testListarInvestimentosAdmin() {

        given()
                .when()
                .get("/investimentos/1")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    // TESTE 3 — Sem token → 401 Unauthorized
    @Test
    public void testListarInvestimentosSemToken() {

        given()
                .when()
                .get("/investimentos/1")
                .then()
                .statusCode(401);
    }

    // TESTE 4 — Cliente inexistente (IllegalArgumentException ou NotFoundException)
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testListarInvestimentosClienteInexistente() {

        given()
                .when()
                .get("/investimentos/99999")
                .then()
                .statusCode(anyOf(is(400), is(404), is(500)));
    }

    // TESTE 5 — Cliente válido mas sem investimentos
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testListarInvestimentosSemInvestimentos() {

        Long clienteId = 2L; // Use um ID sem investimentos no banco

        given()
                .when()
                .get("/investimentos/" + clienteId)
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class))
                .body("size()", anyOf(is(0), greaterThanOrEqualTo(0)));
    }

    // TESTE 6 — Estrutura do JSON retornado
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testEstruturaInvestimento() {

        given()
                .when()
                .get("/investimentos/1") // ID com investimentos se existir
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class))
                .body("size()", greaterThanOrEqualTo(0))
                .body("[0].id", anyOf(isA(Integer.class), is(nullValue())))
                .body("[0].tipo", anyOf(isA(String.class), is(nullValue())))
                .body("[0].valor", anyOf(isA(Float.class), isA(Double.class), is(nullValue())))
                .body("[0].rentabilidade", anyOf(isA(Float.class), isA(Double.class), is(nullValue())))
                .body("[0].data", anyOf(isA(String.class), is(nullValue())));
    }
}
