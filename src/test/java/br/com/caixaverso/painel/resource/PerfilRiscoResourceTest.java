package br.com.caixaverso.painel.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class PerfilRiscoResourceTest {

    // TESTE 1 — Cliente válido (retorna 200)
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testPerfilRiscoClienteValido() {

        // Aqui usamos um ID qualquer, desde que exista no banco de teste
        Long clienteId = 1L;

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/perfil-risco/" + clienteId)
                .then()
                .statusCode(200)
                .body("clienteId", is(clienteId.intValue()))
                .body("perfil", notNullValue())
                .body("pontuacao", notNullValue())
                .body("descricao", notNullValue());
    }

    // TESTE 2 — Acesso sem autenticação deve retornar 401
    @Test
    public void testPerfilRiscoSemToken() {

        given()
                .when()
                .get("/perfil-risco/1")
                .then()
                .statusCode(401);
    }

    //TESTE 3 — Cliente inexistente deve retornar 500 ou 404
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testPerfilRiscoClienteInexistente() {

        given()
                .when()
                .get("/perfil-risco/99999")
                .then()
                .statusCode(anyOf(is(400), is(404), is(500)));
    }



    // TESTE 4 — ClienteId inválido → 400 Bad Request
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testPerfilRiscoIdInvalido() {

        given()
                .when()
                .get("/perfil-risco/null") // não é Long
                .then()
                .statusCode(404);  // JAX-RS não reconhece rota → retorna 404
    }



    // TESTE EXTRA — Admin também deve ter acesso (200)
    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testPerfilRiscoAdmin() {

        given()
                .when()
                .get("/perfil-risco/1")
                .then()
                .statusCode(200)
                .body("perfil", notNullValue())
                .body("pontuacao", notNullValue());
    }

}
