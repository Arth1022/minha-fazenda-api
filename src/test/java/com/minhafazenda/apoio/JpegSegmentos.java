package com.minhafazenda.apoio;

import java.util.ArrayList;
import java.util.List;

/** Marcadores de um JPEG antes dos dados da imagem; é neles que ficam EXIF e outros metadados. */
public record JpegSegmentos(List<Integer> marcadores) {

    private static final int APP0_JFIF = 0xE0;
    private static final int APP1_EXIF = 0xE1;
    private static final int APP15 = 0xEF;
    private static final int COMENTARIO = 0xFE;
    private static final int INICIO_DA_IMAGEM = 0xDA;

    public static JpegSegmentos de(byte[] jpeg) {
        List<Integer> marcadores = new ArrayList<>();
        int posicao = 2;
        while (posicao + 4 <= jpeg.length && (jpeg[posicao] & 0xFF) == 0xFF) {
            int marcador = jpeg[posicao + 1] & 0xFF;
            if (marcador == INICIO_DA_IMAGEM) {
                break;
            }
            marcadores.add(marcador);
            int tamanho = ((jpeg[posicao + 2] & 0xFF) << 8) | (jpeg[posicao + 3] & 0xFF);
            posicao += 2 + tamanho;
        }
        return new JpegSegmentos(marcadores);
    }

    public boolean temExif() {
        return marcadores.contains(APP1_EXIF);
    }

    /** APP1 a APP15 e comentários carregam metadados; APP0 é só o cabeçalho JFIF. */
    public List<Integer> metadados() {
        return marcadores.stream()
                .filter(marcador -> (marcador > APP0_JFIF && marcador <= APP15) || marcador == COMENTARIO)
                .toList();
    }
}
