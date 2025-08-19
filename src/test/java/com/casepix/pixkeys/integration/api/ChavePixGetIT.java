package com.casepix.pixkeys.integration.api;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChavePixGetIT extends BaseIT {

    @Test
    void listar_200_sem_filtros() throws Exception {
        var c = contaDe("PF");
        criarChave(c, "EMAIL", "a@mail.com", "Joao", "A");
        criarChave(c, "CELULAR", "+5511999999999", "Joao", "B");

        mvc.perform(get("/chave-pix"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].id", not(blankOrNullString())))
            .andExpect(jsonPath("$.content[0].createdAt", matchesPattern(ISO_OFFSET_OR_Z_REGEX)))
            .andExpect(jsonPath("$.content[0].deletedAt",
                anyOf(nullValue(), matchesPattern(ISO_OFFSET_OR_Z_REGEX))
            ))
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.pageable.pageSize").value(20));
    }

    @Test
    void listar_200_filtra_por_tipoChave() throws Exception {
        var c = contaDe("PF");
        criarChave(c, "EMAIL", "filtro@mail.com", "Joao", null);
        criarChave(c, "CELULAR", "+5511888888888", "Joao", null);

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
        for (int i = 0; i < 3; i++) criarChave(c, "EMAIL", "p" + i + "@page.com", "Joao", null);

        mvc.perform(get("/chave-pix").param("page", "0").param("size", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.number").value(0));

        mvc.perform(get("/chave-pix").param("page", "1").param("size", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.number").value(1));
    }


    @Test
    void listar_422_quando_informa_so_agencia_sem_conta() throws Exception {
        mvc.perform(get("/chave-pix")
                .param("tipoConta", "CORRENTE")
                .param("numeroAgencia", "0001"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void listar_422_quando_informa_so_conta_sem_agencia() throws Exception {
        mvc.perform(get("/chave-pix")
                .param("tipoConta", "CORRENTE")
                .param("numeroConta", "12345678"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void listar_200_quando_filtra_por_agencia_conta_e_tipoConta() throws Exception {
        var c = contaDe("PF");
        criarChave(c, "EMAIL", "accfilter@mail.com", "Joao", "PF");

        mvc.perform(get("/chave-pix")
                .param("tipoConta", c.tipoConta())
                .param("numeroAgencia", c.agencia())
                .param("numeroConta", c.conta()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].tipoConta", everyItem(equalTo(c.tipoConta()))))
            .andExpect(jsonPath("$.content[*].numeroAgencia", everyItem(equalTo(c.agencia()))))
            .andExpect(jsonPath("$.content[*].numeroConta", everyItem(equalTo(c.conta()))));
    }


    @Test
    void listar_200_quando_filtra_so_por_inclusao() throws Exception {
        var c = contaDe("PF");
        // cria duas chaves com um pequeno gap
        var t0 = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).minusSeconds(2);
        criarChave(c, "EMAIL", "inc1@mail.com", "Joao", null);
        Thread.sleep(40);
        criarChave(c, "EMAIL", "inc2@mail.com", "Joao", null);
        var t2 = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).plusSeconds(2);

        mvc.perform(get("/chave-pix")
                .param("inclusaoDe", t0.toString())
                .param("inclusaoAte", t2.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(2)));
    }

    @Test
    void listar_200_quando_filtra_so_por_inativacao() throws Exception {
        var c = contaDe("PF");
        var id = criarChave(c, "EMAIL", "will-deactivate@mail.com", "Joao", null);
        inativar(id);

        var de = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).minusDays(1);
        var ate = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).plusDays(1);

        mvc.perform(get("/chave-pix")
                .param("inativacaoDe", de.toString())
                .param("inativacaoAte", ate.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].valorChave", hasItem("will-deactivate@mail.com")));
    }

}
