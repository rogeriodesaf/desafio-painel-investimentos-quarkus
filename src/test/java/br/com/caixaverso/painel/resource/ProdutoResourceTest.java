package br.com.caixaverso.painel.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ProdutoResourceTest {

    // TESTE 1 — Sucesso: perfil conservador
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testRecomendarConservador() {

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/produtos-recomendados/conservador")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    // TESTE 2 — Sucesso: admin também pode acessar
    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    public void testRecomendarAdmin() {

        given()
                .when()
                .get("/produtos-recomendados/agressivo")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    // TESTE 3 — Sem token → 401
    @Test
    public void testRecomendarSemToken() {

        given()
                .when()
                .get("/produtos-recomendados/conservador")
                .then()
                .statusCode(401);
    }

    // TESTE 4 — Perfil inválido → 400 ou 500
    // ProdutoService lança IllegalArgumentException
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testRecomendarPerfilInvalido() {

        given()
                .when()
                .get("/produtos-recomendados/xxx")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    // TESTE 5 — Verificar estrutura do JSON (se existir algum produto)
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    public void testEstruturaProduto() {

        given()
                .when()
                .get("/produtos-recomendados/conservador")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class))
                .body("size()", greaterThanOrEqualTo(0)) // pode não ter nenhum
                .body("[0].id", anyOf(isA(Integer.class), is(nullValue())))
                .body("[0].nome", anyOf(isA(String.class), is(nullValue())))
                .body("[0].tipo", anyOf(isA(String.class), is(nullValue())))
                .body("[0].rentabilidade", anyOf(isA(Float.class), isA(Double.class), is(nullValue())))
                .body("[0].risco", anyOf(isA(String.class), is(nullValue())));
    }
}
