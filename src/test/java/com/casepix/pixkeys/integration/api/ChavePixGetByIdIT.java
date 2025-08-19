package com.casepix.pixkeys.integration.api;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ChavePixGetByIdIT extends BaseIT {
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
            .andExpect(jsonPath("$.nomeCorrentista").value("Joao"))
            .andExpect(jsonPath("$.sobrenomeCorrentista").value("Silva"))
            .andExpect(jsonPath("$.createdAt", matchesPattern(ISO_OFFSET_OR_Z_REGEX)))
            .andExpect(jsonPath("$.deletedAt",
                anyOf(nullValue(), matchesPattern(ISO_OFFSET_OR_Z_REGEX))
            ));
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
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }
}
