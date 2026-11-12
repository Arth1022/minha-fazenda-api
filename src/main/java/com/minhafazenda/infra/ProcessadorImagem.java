package com.minhafazenda.infra;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.stereotype.Component;

import com.minhafazenda.exception.ApiException;
import com.minhafazenda.exception.CodigoErro;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

/** Regrava a foto em JPEG quadrado. Regravar descarta o EXIF, que pode conter o GPS da fazenda. */
@Component
public class ProcessadorImagem {

    private static final int LADO_EM_PIXELS = 512;
    private static final double QUALIDADE_JPEG = 0.85;
    // Barra imagens pequenas em bytes que explodem em memória ao decodificar.
    private static final long MAXIMO_DE_PIXELS = 40_000_000L;

    public byte[] paraJpegQuadrado(byte[] original) {
        if (FormatoImagem.detectar(original).isEmpty()) {
            throw new ApiException(CodigoErro.FORMATO_INVALIDO);
        }
        exigirDimensoesSuportadas(original);
        return regravar(original);
    }

    /**
     * Foto de registro (ocorrência, comprovante, horímetro): mantém a proporção, limita o maior
     * lado e regrava em JPEG sem EXIF. Comprovante precisa continuar legível, por isso o lado é
     * bem maior que o do avatar.
     */
    public byte[] paraJpegReduzido(byte[] original, int ladoMaximo) {
        if (FormatoImagem.detectar(original).isEmpty()) {
            throw new ApiException(CodigoErro.FORMATO_INVALIDO, "A imagem precisa ser JPEG, PNG ou WebP.");
        }
        int[] lados = exigirDimensoesSuportadas(original);
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        try {
            Thumbnails.Builder<?> imagem = Thumbnails.of(new ByteArrayInputStream(original));
            if (Math.max(lados[0], lados[1]) > ladoMaximo) {
                imagem.size(ladoMaximo, ladoMaximo);
            } else {
                imagem.scale(1.0);
            }
            imagem.outputFormat("jpg").outputQuality(QUALIDADE_JPEG).toOutputStream(saida);
        } catch (IOException | RuntimeException e) {
            throw new ApiException(CodigoErro.FORMATO_INVALIDO, "A imagem precisa ser JPEG, PNG ou WebP.");
        }
        return saida.toByteArray();
    }

    /** Largura e altura lidas do cabeçalho, sem decodificar a imagem. */
    private static int[] exigirDimensoesSuportadas(byte[] original) {
        int[] lados = dimensoes(original);
        if ((long) lados[0] * lados[1] > MAXIMO_DE_PIXELS) {
            throw new ApiException(CodigoErro.ARQUIVO_GRANDE, "A foto tem dimensões grandes demais.");
        }
        return lados;
    }

    private static int[] dimensoes(byte[] original) {
        try (ImageInputStream entrada = ImageIO.createImageInputStream(new ByteArrayInputStream(original))) {
            Iterator<ImageReader> leitores = ImageIO.getImageReaders(entrada);
            if (!leitores.hasNext()) {
                throw new ApiException(CodigoErro.FORMATO_INVALIDO);
            }
            ImageReader leitor = leitores.next();
            try {
                leitor.setInput(entrada);
                return new int[] {leitor.getWidth(0), leitor.getHeight(0)};
            } finally {
                leitor.dispose();
            }
        } catch (IOException e) {
            throw new ApiException(CodigoErro.FORMATO_INVALIDO);
        }
    }

    private static byte[] regravar(byte[] original) {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        try {
            Thumbnails.of(new ByteArrayInputStream(original))
                    .size(LADO_EM_PIXELS, LADO_EM_PIXELS)
                    .crop(Positions.CENTER)
                    .outputFormat("jpg")
                    .outputQuality(QUALIDADE_JPEG)
                    .toOutputStream(saida);
        } catch (IOException | RuntimeException e) {
            // Decodificadores lançam exceções variadas para arquivos corrompidos.
            throw new ApiException(CodigoErro.FORMATO_INVALIDO);
        }
        return saida.toByteArray();
    }
}
