package com.casepix.pixkeys.integration.api;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChavePixPutIT extends BaseIT {


    @Test
    void put_404_quando_id_inexistente() throws Exception {
        var c = contaDe("PF");
        String body = bodyAlteracao(c.tipoConta(), c.agencia(), c.conta(), "Joao", "Silva");

        mvc.perform(put("/chave-pix/{id}", java.util.UUID.randomUUID())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("KEY_NOT_FOUND"));
    }

    @Test
    void put_200_altera_nome_e_sobrenome_mesma_conta() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "put200@mail.com", "Joao", "Antigo");

        String body = bodyAlteracao(c.tipoConta(), c.agencia(), c.conta(), "Novo", "Sobrenome");

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.nomeCorrentista").value("Novo"))
            .andExpect(jsonPath("$.sobrenomeCorrentista").value("Sobrenome"))
            .andExpect(jsonPath("$.numeroAgencia").value(c.agencia()))
            .andExpect(jsonPath("$.numeroConta").value(c.conta()))
            .andExpect(jsonPath("$.tipoConta").value(c.tipoConta()));

        mvc.perform(get("/chave-pix/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.nomeCorrentista").value("Novo"))
            .andExpect(jsonPath("$.sobrenomeCorrentista").value("Sobrenome"))
            .andExpect(jsonPath("$.numeroAgencia").value(c.agencia()))
            .andExpect(jsonPath("$.numeroConta").value(c.conta()))
            .andExpect(jsonPath("$.tipoConta").value(c.tipoConta()));
    }

    @Test
    void put_200_relink_para_conta_existente_sem_estourar_limite() throws Exception {
        var origem = contaDe("PF");
        UUID id = criarChave(origem, "EMAIL", "relink@mail.com", "Joao", "Origem");

        var destino = criarContaNova("PF", "CORRENTE", "7001", "55550001");
        String body = bodyAlteracao(destino.tipoConta(), destino.agencia(), destino.conta(), "Joao", "Destino");

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numeroAgencia").value(destino.agencia()))
            .andExpect(jsonPath("$.numeroConta").value(destino.conta()))
            .andExpect(jsonPath("$.tipoConta").value(destino.tipoConta()))
            .andExpect(jsonPath("$.nomeCorrentista").value("Joao"))
            .andExpect(jsonPath("$.sobrenomeCorrentista").value("Destino"));

        mvc.perform(get("/chave-pix/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.numeroAgencia").value(destino.agencia()))
            .andExpect(jsonPath("$.numeroConta").value(destino.conta()))
            .andExpect(jsonPath("$.tipoConta").value(destino.tipoConta()))
            .andExpect(jsonPath("$.nomeCorrentista").value("Joao"))
            .andExpect(jsonPath("$.sobrenomeCorrentista").value("Destino"));
    }


    @Test
    void put_404_quando_conta_alvo_inexistente() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "notfound-dest@mail.com", "Joao", "Silva");

        String body = bodyAlteracao("CORRENTE", "9999", "00000000", "Joao", "Silva");

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(body))
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
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void put_422_quando_agencia_nao_numerica_ou_ultrapassa_4_digitos() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "put-val-ag@mail.com", "Joao", "Silva");

        String bad1 = bodyAlteracao(c.tipoConta(), "00A1", c.conta(), "Joao", "Silva");
        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(bad1))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        String bad2 = bodyAlteracao(c.tipoConta(), "12345", c.conta(), "Joao", "Silva");
        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(bad2))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void put_422_quando_conta_nao_numerica_ou_ultrapassa_8_digitos() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "put-val-cc@mail.com", "Joao", "Silva");

        String bad1 = bodyAlteracao(c.tipoConta(), c.agencia(), "12AB5678", "Joao", "Silva");
        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(bad1))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        String bad2 = bodyAlteracao(c.tipoConta(), c.agencia(), "123456789", "Joao", "Silva");
        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(bad2))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void put_422_quando_nome_obrigatorio_ou_ultrapassa_30() throws Exception {
        var c = contaDe("PF");
        UUID id = criarChave(c, "EMAIL", "put-val-nome@mail.com", "Joao", "Silva");

        String blank = bodyAlteracao(c.tipoConta(), c.agencia(), c.conta(), "", "X");
        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(blank))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        String nome31 = "A".repeat(31);
        String longName = bodyAlteracao(c.tipoConta(), c.agencia(), c.conta(), nome31, "X");
        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(longName))
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
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(bad))
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
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(bad))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void put_422_limite_pf_na_conta_destino() throws Exception {
        var destino = criarContaNova("PF", "CORRENTE", "8001", "60000001");
        for (int i = 0; i < 5; i++) {
            String p = payload("EMAIL", "pf-dest-" + i + "@limit.com", destino, "Nome", "PF");
            mvc.perform(post("/chave-pix").contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(p))
                .andExpect(r -> assertThat(r.getResponse().getStatus()).isIn(200, 201));
        }
        assertThat(chavesAtivasNaConta(destino.id())).isEqualTo(5);

        var origem = criarContaNova("PF", "CORRENTE", "8002", "60000002");
        UUID id = criarChave(origem, "EMAIL", "pf-relink@mail.com", "Joao", "Origem");

        String body = bodyAlteracao(destino.tipoConta(), destino.agencia(), destino.conta(), "Joao", "Destino");

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("LIMIT_EXCEEDED"));
    }

    @Test
    void put_422_limite_pj_na_conta_destino() throws Exception {
        var destino = criarContaNova("PJ", "CORRENTE", "8001", "60000001");
        for (int i = 0; i < 20; i++) {
            String p = payload("EMAIL", "pf-dest-" + i + "@limit.com", destino, "Nome", "PF");
            mvc.perform(post("/chave-pix").contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(p))
                .andExpect(r -> assertThat(r.getResponse().getStatus()).isIn(200, 201));
        }

        assertThat(chavesAtivasNaConta(destino.id())).isEqualTo(20);

        var origem = criarContaNova("PF", "CORRENTE", "8002", "60000002");
        UUID id = criarChave(origem, "EMAIL", "pf-relink@mail.com", "Joao", "Origem");

        String body = bodyAlteracao(destino.tipoConta(), destino.agencia(), destino.conta(), "Joao", "Destino");

        mvc.perform(put("/chave-pix/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("LIMIT_EXCEEDED"));
    }
}
