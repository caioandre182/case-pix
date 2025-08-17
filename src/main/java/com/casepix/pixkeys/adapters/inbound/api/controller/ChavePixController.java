package com.casepix.pixkeys.adapters.inbound.api.controller;

import com.casepix.pixkeys.adapters.inbound.api.dto.ChavePixListaItemResponse;
import com.casepix.pixkeys.adapters.inbound.api.dto.CriaChavePixRequest;
import com.casepix.pixkeys.adapters.inbound.api.dto.CriarChavePixResponse;
import com.casepix.pixkeys.adapters.inbound.api.mapper.ChavePixApiMapper;
import com.casepix.pixkeys.application.dto.BuscarChavesFiltro;
import com.casepix.pixkeys.application.port.in.BuscarChavePixPorIdUseCase;
import com.casepix.pixkeys.application.port.in.BuscarChavesUseCase;
import com.casepix.pixkeys.application.port.in.CriarChavePixUseCase;
import com.casepix.pixkeys.domain.enums.TipoChave;
import com.casepix.pixkeys.domain.enums.TipoConta;
import com.casepix.pixkeys.domain.exception.ValidacaoException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/chave-pix")
public class ChavePixController {
    private final CriarChavePixUseCase criarChave;
    private final BuscarChavesUseCase buscarChaves;
    private final BuscarChavePixPorIdUseCase buscarChavePorId;

    public ChavePixController(
        CriarChavePixUseCase criarChave,
        BuscarChavesUseCase buscarChaves,
        BuscarChavePixPorIdUseCase buscarChavePorId
    ){
        this.criarChave = criarChave;
        this.buscarChaves = buscarChaves;
        this.buscarChavePorId = buscarChavePorId;
    }

    @Operation(
        summary = "Cadastra uma chave PIX",
        description = "Valida formato e vincula a chave à agência/conta existente"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Chave criada com sucesso",
        content = @Content(schema = @Schema(implementation = CriarChavePixResponse.class))
    )
    @ApiResponse(
        responseCode = "422",
        description = "Violação de regra/validação",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Duplicata", value = """
          { "code":"DUPLICATE_KEY","message":"Chave PIX já cadastrada","details":[] }
        """),
                @ExampleObject(name = "Limite por conta", value = """
          { "code":"VALIDATION_ERROR","message":"Titular PF atingiu o limite de 5 chaves ativas","details":[] }
        """)
            }
        )
    )
    @PostMapping
    public ResponseEntity<CriarChavePixResponse> criar(
        @Valid @RequestBody CriaChavePixRequest req){

        var cmd = ChavePixApiMapper.toCommand(req);
        CriarChavePixResponse result = criarChave.executar(cmd);

        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "Consulta chave PIX por ID",
        description = "Retorna os dados da chave"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Chave encontrada",
        content = @Content(schema = @Schema(implementation = ChavePixListaItemResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Chave não encontrada",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "Not found", value = """
        { "code":"KEY_NOT_FOUND","message":"Chave não encontrada: <uuid>","details":[] }
        """)
        )
    )
    @GetMapping("/{id}")
    public ResponseEntity<ChavePixListaItemResponse> buscarPorId(@PathVariable UUID id){
        if (id == null) {
            throw new ValidacaoException("Quando informar ID, não envie outros filtros. Use /chave-pix/{id}.");
        }

        return ResponseEntity.ok(buscarChavePorId.executar(id));
    }


    @Operation(
        summary = "Consulta chaves PIX",
        description = """
    Filtra por Tipo de chave, Agência/Conta, Data de inclusão ou Data de inativação.
    Regras:
    1) filtros combináveis (tipo/agência/conta/data);
    2) Não combine inclusão e inativação (escolha um grupo).
    """
    )
    @ApiResponse(
        responseCode = "200",
        description = "Consulta realizada com sucesso",
        content = @Content(
            array = @ArraySchema(schema = @Schema(implementation = ChavePixListaItemResponse.class))
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Nenhum registro para os filtros",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "Sem resultados", value = """
        { "code":"NOT_FOUND","message":"Nenhuma chave encontrada para os filtros informados","details":[] }
        """)
        )
    )
    @ApiResponse(
        responseCode = "422",
        description = "Violação de regra/validação",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "ID com outros filtros", value = """
            { "code":"VALIDATION_ERROR","message":"Quando informar ID, não envie outros filtros. Use /chave-pix/{id}.","details":[] }
            """),
                @ExampleObject(name = "Inclusão + Inativação", value = """
            { "code":"VALIDATION_ERROR","message":"Não combine filtros de inclusão e inativação; escolha apenas um grupo.","details":[] }
            """)
            }
        )
    )
    @GetMapping(params = "!id")
    public ResponseEntity<Page<ChavePixListaItemResponse>> listar(
        @RequestParam(required = false) TipoChave tipoChave,
        @RequestParam(required = false) TipoConta tipoConta,
        @RequestParam(required = false) String numeroAgencia,
        @RequestParam(required = false) String numeroConta,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime inclusaoDe,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime inclusaoAte,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime inativacaoDe,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime inativacaoAte,
        @ParameterObject
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        var filtro = new BuscarChavesFiltro(
            tipoChave, tipoConta, numeroAgencia, numeroConta,
            inclusaoDe == null ? null : inclusaoDe.toInstant(),
            inclusaoAte == null ? null : inclusaoAte.toInstant(),
            inativacaoDe == null ? null : inativacaoDe.toInstant(),
            inativacaoAte == null ? null : inativacaoAte.toInstant()
        );

        var page = buscarChaves.executar(filtro, pageable);
        return ResponseEntity.ok(page);
    }
}
