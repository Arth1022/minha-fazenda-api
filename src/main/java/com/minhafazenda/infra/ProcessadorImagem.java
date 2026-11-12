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

    private static void exigirDimensoesSuportadas(byte[] original) {
        if (contarPixels(original) > MAXIMO_DE_PIXELS) {
            throw new ApiException(CodigoErro.ARQUIVO_GRANDE, "A foto tem dimensões grandes demais.");
        }
    }

    private static long contarPixels(byte[] original) {
        try (ImageInputStream entrada = ImageIO.createImageInputStream(new ByteArrayInputStream(original))) {
            Iterator<ImageReader> leitores = ImageIO.getImageReaders(entrada);
            if (!leitores.hasNext()) {
                throw new ApiException(CodigoErro.FORMATO_INVALIDO);
            }
            ImageReader leitor = leitores.next();
            try {
                leitor.setInput(entrada);
                return (long) leitor.getWidth(0) * leitor.getHeight(0);
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
