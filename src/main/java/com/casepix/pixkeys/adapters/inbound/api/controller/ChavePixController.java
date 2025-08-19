package com.casepix.pixkeys.adapters.inbound.api.controller;

import com.casepix.pixkeys.adapters.inbound.api.dto.*;
import com.casepix.pixkeys.adapters.inbound.api.mapper.ChavePixApiMapper;
import com.casepix.pixkeys.application.port.in.AlterarChavePixUseCase;
import com.casepix.pixkeys.application.port.in.ConsultarChavePixPorIdUseCase;
import com.casepix.pixkeys.application.port.in.ConsultarChavesUseCase;
import com.casepix.pixkeys.application.port.in.CriarChavePixUseCase;
import com.casepix.pixkeys.application.port.in.command.AlterarContaTitularCommand;
import com.casepix.pixkeys.domain.model.ChavePix;
import com.casepix.pixkeys.infra.error.GlobalExceptionHandler;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/chave-pix")
public class ChavePixController {
    private final CriarChavePixUseCase criarChave;
    private final ConsultarChavesUseCase buscarChaves;
    private final ConsultarChavePixPorIdUseCase buscarChavePorId;
    private final AlterarChavePixUseCase alterarChave;

    public ChavePixController(
        CriarChavePixUseCase criarChave,
        ConsultarChavesUseCase buscarChaves,
        ConsultarChavePixPorIdUseCase buscarChavePorId,
        AlterarChavePixUseCase alterarChave
    ){
        this.criarChave = criarChave;
        this.buscarChaves = buscarChaves;
        this.buscarChavePorId = buscarChavePorId;
        this.alterarChave = alterarChave;
    }

    @Operation(
        summary = "Cadastra uma chave PIX",
        description = "Valida formato e vincula a chave à agência/conta existente"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Chave criada com sucesso",
        content = @Content(schema = @Schema(implementation = ChavePix.class))
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
        var result = criarChave.executar(cmd);

        var location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(result.idRegistro())
            .toUri();

        return ResponseEntity.ok(new CriarChavePixResponse(result.idRegistro()));
    }

    @Operation(
        summary = "Consulta chave PIX por ID",
        description = "Retorna os dados da chave"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Chave encontrada",
        content = @Content(schema = @Schema(implementation = ConsultarChavePixResponse.class))
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
    public ResponseEntity<ConsultarChavePixResponse> buscarPorId(@PathVariable UUID id){
        var consulta = buscarChavePorId.executar(id);

        return ResponseEntity.ok(ChavePixApiMapper.toResponse(consulta));
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
            array = @ArraySchema(schema = @Schema(implementation = ConsultarChavePixResponse.class))
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Chave não encontrada",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class),
            examples = @ExampleObject(
                name = "Not found",
                value = "{\"code\":\"KEY_NOT_FOUND\",\"message\":\"Chave não encontrada: 123e4567-e89b-12d3-a456-426614174000\",\"details\":[]}"
            )
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
    @GetMapping
    public ResponseEntity<Page<ConsultarChavePixResponse>> listar(
        @Valid @ParameterObject ConsultarChavePixRequest req,
        @ParameterObject
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var page = buscarChaves.executar(ChavePixApiMapper.toQuery(req), pageable);

        if (page.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        var body = page.map(ChavePixApiMapper::toResumo);
        return ResponseEntity.ok(body);
    }

    @Operation(
        summary = "Altera uma chave PIX por id",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AlterarDadosContaRequest.class),
                examples = @ExampleObject(
                    name = "Exemplo",
                    value = """
                        {
                          "tipoConta": "CORRENTE",
                          "numeroAgencia": "0001",
                          "numeroConta": "12345678",
                          "nomeCorrentista": "Joao",
                          "sobrenomeCorrentista": "Silva"
                        }
                        """
                )
            )
        )
    )
    @PutMapping("/{id}")
    public ResponseEntity<AlterarDadosContaResponse> alterar(
        @PathVariable UUID id,
        @Valid @RequestBody AlterarDadosContaRequest req
    ){
        var cmd = ChavePixApiMapper.toCommand(id, req);
        var result = alterarChave.executar(cmd);

        return ResponseEntity.ok(ChavePixApiMapper.toResponse(result));
    }

}
