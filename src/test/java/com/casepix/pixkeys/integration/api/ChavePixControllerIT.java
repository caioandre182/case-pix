package com.casepix.pixkeys.integration.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ChavePixControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    MockMvc mvc;

    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void cleanPixChave() {
        jdbc.update("DELETE FROM pix_chave");
    }

    @Test
    void criar_ok_retorna_2xx_e_persiste() throws Exception {
        String payload = """
        {
          "tipoChave": "EMAIL",
          "valorChave": "User+Tag@Mail.COM",
          "tipoConta": "CORRENTE",
          "numeroAgencia": "0001",
          "numeroConta": "12345678",
          "nomeCorrentista": "Joao",
          "sobrenomeCorrentista": "Silva"
        }
        """;

        var result = mvc.perform(post("/chave-pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(result1 -> {
                int status = result1.getResponse().getStatus();
                assertThat(status).isIn(200, 201);
            })
            .andReturn();

        String body = result.getResponse().getContentAsString().trim();
        String possibleId = body.replaceAll("[\"{}\\s]", "")
            .replace("id:", "")
            .replace("id:", ""); // defensivo

        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM pix_chave WHERE valor_chave = ?",
            Integer.class, "user+tag@mail.com"
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void criar_422_quando_celular_invalido() throws Exception {
        String payload = """
        {
          "tipoChave": "CELULAR",
          "valorChave": "5511999999999",
          "tipoConta": "CORRENTE",
          "numeroAgencia": "0001",
          "numeroConta": "12345678",
          "nomeCorrentista": "Joao"
        }
        """;

        mvc.perform(post("/chave-pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void criar_404_quando_conta_inexistente() throws Exception {
        String payload = """
        {
          "tipoChave": "EMAIL",
          "valorChave": "a@b.com",
          "tipoConta": "CORRENTE",
          "numeroAgencia": "9999",
          "numeroConta": "00000000",
          "nomeCorrentista": "Joao"
        }
        """;

        mvc.perform(post("/chave-pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    void criar_409_quando_chave_duplicada() throws Exception {
        String payload = """
        {
          "tipoChave": "EMAIL",
          "valorChave": "dup@mail.com",
          "tipoConta": "CORRENTE",
          "numeroAgencia": "0001",
          "numeroConta": "12345678",
          "nomeCorrentista": "Joao"
        }
        """;

        mvc.perform(post("/chave-pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(200, 201));

        mvc.perform(post("/chave-pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_KEY"));
    }

    @Test
    void post_400_payload_invalido_faltando_campos() throws Exception {
        var c = contaDe("PF");
        // falta 'tipoChave' e 'nomeCorrentista'
        String json = """
      {
        "valorChave":"a@b.com",
        "tipoConta":"%s",
        "numeroAgencia":"%s",
        "numeroConta":"%s"
      }
    """.formatted(c.tipoConta(), c.agencia(), c.conta());

        mvc.perform(post("/chave-pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.details").isArray())
            .andExpect(jsonPath("$.details.length()", greaterThan(0)));
    }

    @Test
    void post_422_email_invalido_sem_arroba() throws Exception {
        var c = contaDe("PF");
        String json = payload("EMAIL", "sem-arroba.com", c, "Joao", null);

        mvc.perform(post("/chave-pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void post_422_limite_pf_na_sexta_chave() throws Exception {
        var c = contaDe("PF");
        for (int i = 0; i < 5; i++) {
            String p = payload("EMAIL", "pf"+i+"@limit.com", c, "Joao", "PF");
            mvc.perform(post("/chave-pix")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(p))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(200, 201));
        }
        String sexta = payload("EMAIL", "pf5@limit.com", c, "Joao", "PF");
        mvc.perform(post("/chave-pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(sexta))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("LIMIT_EXCEEDED"));
    }

    @Test
    void post_422_limite_pj_na_21a_chave() throws Exception {
        var c = contaDe("PJ");
        // cria 20 chaves válidas
        for (int i = 0; i < 20; i++) {
            String p = payload("EMAIL", "pj"+i+"@limit.com", c, "Empresa", "PJ");
            mvc.perform(post("/chave-pix")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(p))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(200, 201));
        }
        // 21ª deve dar 422 LIMIT_EXCEEDED
        String v21 = payload("EMAIL", "pj20@limit.com", c, "Empresa", "PJ");
        mvc.perform(post("/chave-pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(v21))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("LIMIT_EXCEEDED"));
    }

    @Test
    void post_concorrencia_mesma_chave_um_sucesso_um_conflito() throws Exception {
        var c = contaDe("PF");
        String p = payload("EMAIL", "race@mail.com", c, "Joao", "Conc");

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(2);

        Callable<Integer> call = () -> {
            barrier.await();
            return mvc.perform(post("/chave-pix")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(p))
                .andReturn()
                .getResponse()
                .getStatus();
        };

        Future<Integer> f1 = pool.submit(call);
        Future<Integer> f2 = pool.submit(call);
        int s1 = f1.get();
        int s2 = f2.get();
        pool.shutdown();

        assertThat(List.of(s1, s2)).anyMatch(s -> s == 200);
        assertThat(List.of(s1, s2)).anyMatch(s -> s == 409);
    }

    @Test
    void get_por_id_200_retorna_item_com_campos_ok() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "getid@mail.com", "Joao", "Silva");

        mvc.perform(get("/chave-pix/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.tipoChave").value("EMAIL"))
            .andExpect(jsonPath("$.valorChave").value("getid@mail.com"))
            .andExpect(jsonPath("$.tipoConta").value(c.tipoConta()))
            .andExpect(jsonPath("$.numeroAgencia").value(c.agencia()))
            .andExpect(jsonPath("$.numeroConta").value(c.conta()))
            .andExpect(jsonPath("$.nomeCorrentista").value("Joao Silva"))
            .andExpect(jsonPath("$.dataInclusao", matchesPattern(ISO_OFFSET_REGEX)))
            .andExpect(jsonPath("$.dataInativacao").value(""));
    }

    @Test
    void get_por_id_404_quando_nao_existe() throws Exception {
        mvc.perform(get("/chave-pix/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("KEY_NOT_FOUND"));
    }

    @Test
    void get_por_id_400_quando_uuid_invalido() throws Exception {
        mvc.perform(get("/chave-pix/{id}", "nao-e-um-uuid"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("BAD_REQUEST")); // se seu handler usa outro code, ajuste aqui
    }

    @Test
    void listar_200_sem_filtros() throws Exception {
        var c = contaDe("PF");
        criarChave(c, "EMAIL",   "a@mail.com",      "Joao", "A");
        criarChave(c, "CELULAR", "+5511999999999",  "Joao", "B");

        mvc.perform(get("/chave-pix"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].id", not(blankOrNullString())))
            .andExpect(jsonPath("$.content[0].dataInclusao", matchesPattern(ISO_OFFSET_REGEX)))
            .andExpect(jsonPath("$.content[0].dataInativacao", anyOf(equalTo(""), nullValue())))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.pageable.pageSize").value(20));
    }

    @Test
    void listar_200_filtra_por_tipoChave() throws Exception {
        var c = contaDe("PF");
        criarChave(c, "EMAIL",   "filtro@mail.com", "Joao", null);
        criarChave(c, "CELULAR", "+5511888888888",  "Joao", null);

        mvc.perform(get("/chave-pix").param("tipoChave", "EMAIL"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].tipoChave", everyItem(equalTo("EMAIL"))));
    }

    @Test
    void listar_200_filtra_por_conta() throws Exception {
        var pf = contaDe("PF");
        var pj = contaDe("PJ");

        criarChave(pf, "EMAIL", "na_pf@mail.com", "Joao", null);
        criarChave(pj, "EMAIL", "na_pj@mail.com", "Empresa", null);

        mvc.perform(get("/chave-pix")
                .param("tipoConta", pf.tipoConta())
                .param("numeroAgencia", pf.agencia())
                .param("numeroConta", pf.conta()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].tipoConta", everyItem(equalTo(pf.tipoConta()))))
            .andExpect(jsonPath("$.content[*].numeroAgencia", everyItem(equalTo(pf.agencia()))))
            .andExpect(jsonPath("$.content[*].numeroConta", everyItem(equalTo(pf.conta()))));
    }

    @Test
    void listar_200_paginacao() throws Exception {
        var c = contaDe("PF");
        for (int i = 0; i < 3; i++) {
            criarChave(c, "EMAIL", "p"+i+"@page.com", "Joao", null);
        }

        mvc.perform(get("/chave-pix").param("page","0").param("size","2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.number").value(0));

        mvc.perform(get("/chave-pix").param("page","1").param("size","2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    void listar_422_quando_combina_inclusao_e_inativacao() throws Exception {
        var c = contaDe("PF");
        criarChave(c, "EMAIL", "mix@mail.com", "Joao", null);

        String agora = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).toString();

        mvc.perform(get("/chave-pix")
                .param("inclusaoDe", agora)
                .param("inativacaoDe", agora))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void listar_200_filtra_por_intervalo_de_inclusao() throws Exception {
        var c = contaDe("PF");

        var t0 = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).minusSeconds(2);
        criarChave(c, "EMAIL", "janela1@mail.com", "Joao", null);
        Thread.sleep(40);
        criarChave(c, "EMAIL", "janela2@mail.com", "Joao", null);
        var t2 = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).plusSeconds(2);

        mvc.perform(get("/chave-pix")
                .param("inclusaoDe", t0.toString())
                .param("inclusaoAte", t2.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(2)));

        var corte = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).minusSeconds(1);
        mvc.perform(get("/chave-pix").param("inclusaoDe", corte.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].valorChave", hasItem("janela2@mail.com")));
    }

    @Test
    void listar_404_quando_sem_resultado() throws Exception {
        mvc.perform(get("/chave-pix")
                .param("tipoChave", "EMAIL")
                .param("numeroAgencia", "9999"))
            .andExpect(status().isNotFound())
            .andExpect(result -> {
                var body = result.getResponse().getContentAsString();
                String code = com.jayway.jsonpath.JsonPath.read(body, "$.code");
                assertThat(code).isIn("NOT_FOUND", "KEY_NOT_FOUND");
            });
    }

    @Test
    void put_404_quando_id_inexistente() throws Exception {
        var c = contaDe("PF");
        String body = bodyAlteracao(c.tipoConta(), c.agencia(), c.conta(), "Joao", "Silva");

        mvc.perform(put("/chave-pix/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("KEY_NOT_FOUND"));
    }

    @Test
    void put_200_altera_nome_e_sobrenome_mesma_conta() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "put200@mail.com", "Joao", "Antigo");

        String body = bodyAlteracao(c.tipoConta(), c.agencia(), c.conta(), "Novo", "Sobrenome");

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.nomeCorrentista").value("Novo Sobrenome"))
            .andExpect(jsonPath("$.numeroAgencia").value(c.agencia()))
            .andExpect(jsonPath("$.numeroConta").value(c.conta()))
            .andExpect(jsonPath("$.tipoConta").value(c.tipoConta()));

        var row = jdbc.queryForMap("SELECT nome_correntista, sobrenome_correntista FROM pix_chave WHERE id = ?", id);
        assertThat(row.get("nome_correntista")).isEqualTo("Novo");
        assertThat(row.get("sobrenome_correntista")).isEqualTo("Sobrenome");
    }

    @Test
    void put_200_relink_para_conta_existente_sem_estourar_limite() throws Exception {
        var origem = contaDe("PF");
        UUID id = criarChave(origem, "EMAIL", "relink@mail.com", "Joao", "Origem");

        var destino = criarContaNova("PF", "CORRENTE", "7001", "55550001");

        String body = bodyAlteracao(destino.tipoConta(), destino.agencia(), destino.conta(), "Joao", "Destino");

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numeroAgencia").value(destino.agencia()))
            .andExpect(jsonPath("$.numeroConta").value(destino.conta()))
            .andExpect(jsonPath("$.tipoConta").value(destino.tipoConta()))
            .andExpect(jsonPath("$.nomeCorrentista").value("Joao Destino"));
    }

    @Test
    void put_404_quando_conta_alvo_inexistente() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "notfound-dest@mail.com", "Joao", "Silva");

        String body = bodyAlteracao("CORRENTE", "9999", "00000000", "Joao", "Silva");

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    void put_422_quando_chave_inativa() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "inactive@mail.com", "Joao", "Silva");
        inativar(id);

        String body = bodyAlteracao(c.tipoConta(), c.agencia(), c.conta(), "Novo", "Nome");

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void put_422_quando_agencia_nao_numerica_ou_ultrapassa_4_digitos() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "put-val-ag@mail.com", "Joao", "Silva");

        String bad1 = bodyAlteracao(c.tipoConta(), "00A1", c.conta(), "Joao", "Silva");
        mvc.perform(put("/chave-pix/{id}", id).contentType(MediaType.APPLICATION_JSON).content(bad1))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        String bad2 = bodyAlteracao(c.tipoConta(), "12345", c.conta(), "Joao", "Silva");
        mvc.perform(put("/chave-pix/{id}", id).contentType(MediaType.APPLICATION_JSON).content(bad2))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void put_422_quando_conta_nao_numerica_ou_ultrapassa_8_digitos() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "put-val-cc@mail.com", "Joao", "Silva");

        String bad1 = bodyAlteracao(c.tipoConta(), c.agencia(), "12AB5678", "Joao", "Silva");
        mvc.perform(put("/chave-pix/{id}", id).contentType(MediaType.APPLICATION_JSON).content(bad1))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        String bad2 = bodyAlteracao(c.tipoConta(), c.agencia(), "123456789", "Joao", "Silva");
        mvc.perform(put("/chave-pix/{id}", id).contentType(MediaType.APPLICATION_JSON).content(bad2))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void put_422_quando_nome_obrigatorio_ou_ultrapassa_30() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "put-val-nome@mail.com", "Joao", "Silva");

        String blank = bodyAlteracao(c.tipoConta(), c.agencia(), c.conta(), "", "X");
        mvc.perform(put("/chave-pix/{id}", id).contentType(MediaType.APPLICATION_JSON).content(blank))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        String nome31 = "A".repeat(31);
        String longName = bodyAlteracao(c.tipoConta(), c.agencia(), c.conta(), nome31, "X");
        mvc.perform(put("/chave-pix/{id}", id).contentType(MediaType.APPLICATION_JSON).content(longName))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void put_422_quando_sobrenome_ultrapassa_45() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "put-val-sobre@mail.com", "Joao", "Silva");

        String sobrenome46 = "B".repeat(46);
        String bad = bodyAlteracao(c.tipoConta(), c.agencia(), c.conta(), "Joao", sobrenome46);

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bad))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void put_422_quando_tipoConta_invalido() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "put-val-tc@mail.com", "Joao", "Silva");

        String bad = """
        { "tipoConta":"SALARIO", "numeroAgencia":"%s", "numeroConta":"%s",
          "nomeCorrentista":"Joao", "sobrenomeCorrentista":"Silva" }
        """.formatted(c.agencia(), c.conta());

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bad))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void put_422_limite_pf_na_conta_destino() throws Exception {
        var destino = criarContaNova("PF", "CORRENTE", "8001", "60000001");

        for (int i = 0; i < 5; i++) {
            String p = payload("EMAIL", "pf-dest-" + i + "@limit.com", destino, "Nome", "PF");
            mvc.perform(post("/chave-pix").contentType(MediaType.APPLICATION_JSON).content(p))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(200, 201));
        }

        assertThat(chavesAtivasNaConta(destino.id())).isEqualTo(5);

        var origem = criarContaNova("PF", "CORRENTE", "8002", "60000002");
        UUID id = criarChave(origem, "EMAIL", "pf-relink@mail.com", "Joao", "Origem");

        String body = bodyAlteracao(destino.tipoConta(), destino.agencia(), destino.conta(), "Joao", "Destino");

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(r -> {
                System.out.println("PUT PF status=" + r.getResponse().getStatus());
                System.out.println("PUT PF body=" + r.getResponse().getContentAsString());
            })
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("LIMIT_EXCEEDED"));
    }

    @Test
    void put_422_limite_pj_na_conta_destino() throws Exception {
        var destino = criarContaNova("PJ", "CORRENTE", "8101", "61000001");

        for (int i = 0; i < 20; i++) {
            String p = payload("EMAIL", "pj-dest-" + i + "@limit.com", destino, "Empresa", "PJ");
            mvc.perform(post("/chave-pix").contentType(MediaType.APPLICATION_JSON).content(p))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(200, 201));
        }

        var origem = criarContaNova("PJ", "CORRENTE", "8102", "61000002");
        UUID id = criarChave(origem, "EMAIL", "pj-relink@mail.com", "Empresa", "Origem");

        String body = bodyAlteracao(destino.tipoConta(), destino.agencia(), destino.conta(), "Empresa", "Destino");

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(r -> {
                System.out.println("PUT PJ status=" + r.getResponse().getStatus());
                System.out.println("PUT PJ body=" + r.getResponse().getContentAsString());
            })
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("LIMIT_EXCEEDED"));
    }

    private record ContaSeed(UUID id, String tipoConta, String agencia, String conta) {}

    private ContaSeed contaDe(String tipoPessoa) {
        return jdbc.query(
            """
            SELECT c.id, c.tipo_conta, c.numero_agencia, c.numero_conta
              FROM conta_bancaria c
              JOIN titular t ON t.id = c.titular_id
             WHERE t.tipo_pessoa = ?
             LIMIT 1
            """,
            ps -> ps.setString(1, tipoPessoa),
            rs -> rs.next() ? new ContaSeed(
                rs.getObject(1, java.util.UUID.class), rs.getString(2), rs.getString(3), rs.getString(4)
            ) : null
        );
    }

    private String payload(String tipoChave, String valor, ContaSeed c, String nome, String sobrenome) {
        return """
        {
          "tipoChave":"%s",
          "valorChave":"%s",
          "tipoConta":"%s",
          "numeroAgencia":"%s",
          "numeroConta":"%s",
          "nomeCorrentista":"%s",
          "sobrenomeCorrentista":"%s"
        }
        """.formatted(tipoChave, valor, c.tipoConta(), c.agencia(), c.conta(), nome, sobrenome == null ? "" : sobrenome);
    }

    private static final String ISO_OFFSET_REGEX =
        "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?[+-]\\d{2}:\\d{2}$";

    private UUID criarChave(ContaSeed c, String tipoChave, String valor, String nome, String sobrenome) throws Exception {
        MvcResult res = mvc.perform(post("/chave-pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload(tipoChave, valor, c, nome, sobrenome)))
            .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(200, 201))
            .andReturn();

        String raw = res.getResponse().getContentAsString().trim();
        String id = null;
        id = com.jayway.jsonpath.JsonPath.read(raw, "$.idRegistro");
        return UUID.fromString(id);
    }

    private String bodyAlteracao(String tipoConta, String agencia, String conta, String nome, String sobrenome) {
        return """
        {
          "tipoConta":"%s",
          "numeroAgencia":"%s",
          "numeroConta":"%s",
          "nomeCorrentista":"%s",
          "sobrenomeCorrentista":"%s"
        }
        """.formatted(tipoConta, agencia, conta, nome, sobrenome == null ? "" : sobrenome);
    }

    private ContaSeed criarContaNova(String tipoPessoa, String tipoConta, String agencia, String conta) {
        UUID titularId = UUID.randomUUID();
        UUID contaId = UUID.randomUUID();

        jdbc.update("""
        INSERT INTO titular (id, tipo_pessoa, cpf, cnpj, nome, sobrenome, created_at)
        VALUES (?, ?, NULL, NULL, ?, ?, NOW())
    """, titularId, tipoPessoa, "Tit " + tipoPessoa, "Teste");

        jdbc.update("""
        INSERT INTO conta_bancaria (id, titular_id, tipo_conta, numero_agencia, numero_conta, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, NOW(), NOW())
    """, contaId, titularId, tipoConta, agencia, conta);

        return new ContaSeed(contaId, tipoConta, agencia, conta);
    }

    private void inativar(UUID id) {
        jdbc.update("UPDATE pix_chave SET deleted_at = NOW() WHERE id = ?", id);
    }

    private int chavesAtivasNaConta(UUID contaId) {
        return jdbc.queryForObject(
            "SELECT COUNT(*) FROM pix_chave WHERE conta_id = ? AND deleted_at IS NULL",
            Integer.class, contaId
        );
    }

}
