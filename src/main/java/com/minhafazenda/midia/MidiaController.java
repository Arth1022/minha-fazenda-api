package com.minhafazenda.midia;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.minhafazenda.security.UsuarioAutenticado;

import lombok.RequiredArgsConstructor;

/**
 * Fotos, áudios e documentos da fazenda no Supabase Storage (bucket privado). O registro que
 * usa o arquivo guarda {@code midia:<caminho>} na coluna de URI e sobe pelo sync normal.
 */
@RestController
@RequestMapping("/fazendas/{fazendaId}/midias")
@RequiredArgsConstructor
public class MidiaController {

    private final MidiaService midiaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    MidiaResponse enviar(UsuarioAutenticado usuario, @PathVariable UUID fazendaId,
            @RequestParam("tipo") String tipo,
            @RequestParam(name = "id", required = false) UUID id,
            @RequestPart("arquivo") MultipartFile arquivo) {
        return midiaService.enviar(usuario.id(), fazendaId, tipo, id, arquivo);
    }

    @GetMapping("/url")
    MidiaResponse url(UsuarioAutenticado usuario, @PathVariable UUID fazendaId, @RequestParam String caminho) {
        return midiaService.url(usuario.id(), fazendaId, caminho);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void apagar(UsuarioAutenticado usuario, @PathVariable UUID fazendaId, @RequestParam String caminho) {
        midiaService.apagar(usuario.id(), fazendaId, caminho);
    }
}
