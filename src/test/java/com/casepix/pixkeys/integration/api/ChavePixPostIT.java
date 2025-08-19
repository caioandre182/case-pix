package com.casepix.pixkeys.integration.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChavePixPostIT extends BaseIT{

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
                .contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(r -> assertThat(r.getResponse().getStatus()).isEqualTo(200))
            .andReturn();

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
                .contentType(MediaType.APPLICATION_JSON).content(payload))
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
                .contentType(MediaType.APPLICATION_JSON).content(payload))
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

        mvc.perform(post("/chave-pix").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(r -> assertThat(r.getResponse().getStatus()).isIn(200, 201));

        mvc.perform(post("/chave-pix").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_KEY"));
    }

    @Test
    void post_400_payload_invalido_faltando_campos() throws Exception {
        var c = contaDe("PF");
        String json = """
        {
          "valorChave":"a@b.com",
          "tipoConta":"%s",
          "numeroAgencia":"%s",
          "numeroConta":"%s"
        }
        """.formatted(c.tipoConta(), c.agencia(), c.conta());

        mvc.perform(post("/chave-pix").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.details").isArray())
            .andExpect(jsonPath("$.details.length()", greaterThan(0)));
    }

    @Test
    void post_422_email_invalido_sem_arroba() throws Exception {
        var c = contaDe("PF");
        String json = payload("EMAIL", "sem-arroba.com", c, "Joao", null);

        mvc.perform(post("/chave-pix").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void post_422_limite_pf_na_sexta_chave() throws Exception {
        var c = contaDe("PF");
        for (int i = 0; i < 5; i++) {
            String p = payload("EMAIL", "pf"+i+"@limit.com", c, "Joao", "PF");
            mvc.perform(post("/chave-pix").contentType(MediaType.APPLICATION_JSON).content(p))
                .andExpect(r -> assertThat(r.getResponse().getStatus()).isIn(200, 201));
        }
        String sexta = payload("EMAIL", "pf5@limit.com", c, "Joao", "PF");
        mvc.perform(post("/chave-pix").contentType(MediaType.APPLICATION_JSON).content(sexta))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("LIMIT_EXCEEDED"));
    }

    @Test
    void post_422_limite_pj_na_21a_chave() throws Exception {
        var c = contaDe("PJ");
        for (int i = 0; i < 20; i++) {
            String p = payload("EMAIL", "pj"+i+"@limit.com", c, "Empresa", "PJ");
            mvc.perform(post("/chave-pix").contentType(MediaType.APPLICATION_JSON).content(p))
                .andExpect(r -> assertThat(r.getResponse().getStatus()).isIn(200, 201));
        }
        String v21 = payload("EMAIL", "pj20@limit.com", c, "Empresa", "PJ");
        mvc.perform(post("/chave-pix").contentType(MediaType.APPLICATION_JSON).content(v21))
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
                    .contentType(MediaType.APPLICATION_JSON).content(p))
                .andReturn().getResponse().getStatus();
        };

        Future<Integer> f1 = pool.submit(call);
        Future<Integer> f2 = pool.submit(call);
        int s1 = f1.get();
        int s2 = f2.get();
        pool.shutdown();

        assertThat(List.of(s1, s2)).anyMatch(s -> s == 200);
        assertThat(List.of(s1, s2)).anyMatch(s -> s == 409);
    }
}
