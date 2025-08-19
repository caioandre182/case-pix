package com.casepix.pixkeys.integration.api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public abstract class BaseIT {

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected JdbcTemplate jdbc;

    @BeforeEach
    void cleanPixChave() {
        jdbc.update("DELETE FROM pix_chave");
        ensureSeed();
    }

    protected record ContaSeed(UUID id, String tipoConta, String agencia, String conta) {
    }

    protected static final String ISO_OFFSET_REGEX =
        "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?[+-]\\d{2}:\\d{2}$";

    protected ContaSeed contaDe(String tipoPessoa) {
        return jdbc.query("""
                SELECT c.id, c.tipo_conta, c.numero_agencia, c.numero_conta
                  FROM conta_bancaria c
                  JOIN titular t ON t.id = c.titular_id
                 WHERE t.tipo_pessoa = ?
                 LIMIT 1
                """,
            ps -> ps.setString(1, tipoPessoa),
            rs -> rs.next()
                ? new ContaSeed(
                rs.getObject(1, java.util.UUID.class), rs.getString(2), rs.getString(3), rs.getString(4))
                : null
        );
    }

    protected void ensureSeed() {
        // cria 1 PF e 1 PJ se não houver
        if (contaDe("PF") == null) {
            criarContaNova("PF", "CORRENTE", "0001", "12345678");
        }
        if (contaDe("PJ") == null) {
            criarContaNova("PJ", "CORRENTE", "0002", "87654321");
        }
    }

    protected String payload(String tipoChave, String valor, ContaSeed c, String nome, String sobrenome) {
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

    protected String bodyAlteracao(String tipoConta, String agencia, String conta, String nome, String sobrenome) {
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

    protected UUID criarChave(ContaSeed c, String tipoChave, String valor, String nome, String sobrenome) throws Exception {
        MvcResult res = mvc.perform(post("/chave-pix")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload(tipoChave, valor, c, nome, sobrenome)))
            .andReturn();

        int s = res.getResponse().getStatus();
        assertThat(s).isIn(200, 201);

        String raw = res.getResponse().getContentAsString().trim();
        String id = com.jayway.jsonpath.JsonPath.read(raw, "$.idRegistro");
        return UUID.fromString(id);
    }

    protected void inativar(UUID id) {
        jdbc.update("UPDATE pix_chave SET deleted_at = NOW() WHERE id = ?", id);
    }

    protected int chavesAtivasNaConta(UUID contaId) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM pix_chave WHERE conta_id = ? AND deleted_at IS NULL",
            Integer.class, contaId
        );
        return java.util.Objects.requireNonNullElse(count, 0);
    }


    protected ContaSeed criarContaNova(String tipoPessoa, String tipoConta, String agencia, String conta) {
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

    public static final String ISO_OFFSET_OR_Z_REGEX =
        "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})$";
}
