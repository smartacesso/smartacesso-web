package br.com.startjob.acesso.entrypoint.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restful-services/access")
@Tag(name = "Sync desktop", description = "API de sincronização do Swing — migração incremental")
public class DesktopAccessController {

    @GetMapping(value = "/action", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Health check da API de acesso/sync")
    public ResponseEntity<String> action() {
        return ResponseEntity.ok("working");
    }
}
