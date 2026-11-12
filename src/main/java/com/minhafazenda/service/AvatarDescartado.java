package com.minhafazenda.service;

/** Publicado quando um usuário deixa de usar um avatar; o arquivo só é apagado depois do commit. */
public record AvatarDescartado(String url) {
}
