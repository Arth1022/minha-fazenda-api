package com.minhafazenda.midia;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.minhafazenda.config.PermissoesProperties;
import com.minhafazenda.entity.FazendaMembro;
import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;
import com.minhafazenda.infra.ProcessadorImagem;
import com.minhafazenda.service.MembroService;

import lombok.RequiredArgsConstructor;

/**
 * Envio e leitura das mídias da fazenda. O caminho no bucket é sempre
 * {fazendaId}/{pasta do tipo}/{id}.{extensão}; o id vem do app (o mesmo arquivo enviado de
 * novo cai no mesmo caminho, então repetir o envio é seguro).
 */
@Service
@RequiredArgsConstructor
public class MidiaService {

    /** Foto de registro: o maior lado depois de reduzir. */
    static final int LADO_MAXIMO_FOTO = 1920;

    private static final Pattern CAMINHO = Pattern.compile(
            "^([0-9a-f-]{36})/(imagens|audios|documentos)/([0-9a-f-]{36})\\.([a-z0-9]{2,4})$");

    private final ArmazenamentoMidia armazenamento;
    private final MembroService membroService;
    private final ProcessadorImagem processadorImagem;
    private final MidiaProperties properties;
    private final PermissoesProperties permissoes;
    private final Clock relogio;

    public MidiaResponse enviar(UUID usuarioId, UUID fazendaId, String tipoInformado, UUID id, MultipartFile arquivo) {
        membroService.exigirMembro(usuarioId, fazendaId);
        TipoMidia tipo = TipoMidia.doValor(tipoInformado)
                .orElseThrow(() -> new ApiException(CodigoErro.VALIDACAO, "tipo: use imagem, audio ou documento"));
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ApiException(CodigoErro.VALIDACAO, "arquivo: é obrigatório");
        }
        if (arquivo.getSize() > tipo.limiteEmBytes()) {
            throw new ApiException(CodigoErro.ARQUIVO_GRANDE,
                    "O arquivo precisa ter no máximo %d MB.".formatted(tipo.limiteEmMegabytes()));
        }
        byte[] bytes = lerBytes(arquivo);
        FormatoMidia formato = FormatoMidia.detectar(tipo, bytes)
                .orElseThrow(() -> new ApiException(CodigoErro.FORMATO_INVALIDO, mensagemDeFormato(tipo)));

        byte[] conteudo = bytes;
        if (tipo == TipoMidia.IMAGEM) {
            // Regravar tira o EXIF (que guarda o GPS da fazenda) e reduz a foto da câmera
            conteudo = processadorImagem.paraJpegReduzido(bytes, LADO_MAXIMO_FOTO);
            formato = FormatoMidia.JPEG;
        }
        String caminho = "%s/%s/%s.%s".formatted(fazendaId, tipo.pasta(), id != null ? id : UUID.randomUUID(),
                formato.extensao());
        armazenamento.salvar(caminho, conteudo, formato.contentType());
        return resposta(caminho);
    }

    public MidiaResponse url(UUID usuarioId, UUID fazendaId, String caminho) {
        exigirCaminhoDaFazenda(fazendaId, caminho);
        membroService.exigirMembro(usuarioId, fazendaId);
        if (!armazenamento.existe(caminho)) {
            throw new ApiException(CodigoErro.NAO_ENCONTRADO, "Arquivo não encontrado.");
        }
        return resposta(caminho);
    }

    public void apagar(UUID usuarioId, UUID fazendaId, String caminho) {
        exigirCaminhoDaFazenda(fazendaId, caminho);
        FazendaMembro membro = membroService.exigirMembro(usuarioId, fazendaId);
        if (!membro.ehOwner() && !permissoes.collaboratorPodeApagar()) {
            throw new ApiException(CodigoErro.SEM_PERMISSAO, "Seu papel não permite apagar arquivos.");
        }
        armazenamento.apagar(caminho);
    }

    private MidiaResponse resposta(String caminho) {
        Duration validade = Duration.ofMinutes(properties.validadeUrlMinutos());
        Instant expiraEm = relogio.instant().plus(validade);
        return new MidiaResponse(caminho, armazenamento.urlAssinada(caminho, validade), expiraEm);
    }

    /** O caminho tem que ser da fazenda da rota: ninguém lê arquivo de outra fazenda trocando o caminho. */
    private static void exigirCaminhoDaFazenda(UUID fazendaId, String caminho) {
        Matcher partes = caminho == null ? null : CAMINHO.matcher(caminho);
        if (partes == null || !partes.matches()) {
            throw new ApiException(CodigoErro.VALIDACAO, "caminho: formato inválido");
        }
        if (!partes.group(1).equals(fazendaId.toString())) {
            throw new ApiException(CodigoErro.SEM_PERMISSAO);
        }
    }

    private static String mensagemDeFormato(TipoMidia tipo) {
        return switch (tipo) {
            case IMAGEM -> "A imagem precisa ser JPEG, PNG ou WebP.";
            case AUDIO -> "O áudio precisa ser M4A, MP3, AAC, OGG, WebM ou WAV.";
            case DOCUMENTO -> "O documento precisa ser PDF.";
        };
    }

    private static byte[] lerBytes(MultipartFile arquivo) {
        try {
            return arquivo.getBytes();
        } catch (IOException e) {
            throw new ApiException(CodigoErro.VALIDACAO, "arquivo: não foi possível ler o envio");
        }
    }
}
