package com.minhafazenda.apoio;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Cliente da API para os testes: monta as requisições do contrato e lê as respostas. */
public class ApiDeTeste {

    public static final int SCHEMA_VERSION = 7;

    private final MockMvc mvc;
    private final JsonMapper json;

    public ApiDeTeste(MockMvc mvc, JsonMapper json) {
        this.mvc = mvc;
        this.json = json;
    }

    public static String novoEmail() {
        return "pessoa-" + UUID.randomUUID() + "@teste.com";
    }

    public static Map<String, Object> corpoDeRegistro(String nome, String email, String nomeFazenda,
            String conviteToken) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("nome", nome);
        corpo.put("credencial", Map.of("tipo", "dev", "email", email));
        corpo.put("deviceId", UUID.randomUUID().toString());
        corpo.put("nomeFazenda", nomeFazenda);
        corpo.put("conviteToken", conviteToken);
        return corpo;
    }

    /** Registra alguém que cria a própria fazenda e vira owner dela. */
    public Conta registrarComFazenda() throws Exception {
        return registrar(corpoDeRegistro("Pessoa de Teste", novoEmail(), "Fazenda de Teste", null));
    }

    /** Registra alguém que entra como collaborator na fazenda do convite. */
    public Conta registrarComConvite(String conviteToken) throws Exception {
        return registrar(corpoDeRegistro("Colaborador de Teste", novoEmail(), null, conviteToken));
    }

    public Conta registrarCollaboratorNaFazendaDe(Conta owner) throws Exception {
        return registrarComConvite(criarConvite(owner));
    }

    public String criarConvite(Conta owner) throws Exception {
        ResultActions resposta = post("/fazendas/" + owner.fazendaId() + "/convites", owner, null)
                .andExpect(status().isCreated());
        return json(resposta).get("token").asString();
    }

    @SuppressWarnings("unchecked")
    private Conta registrar(Map<String, Object> corpo) throws Exception {
        JsonNode resposta = json(post("/auth/registro", corpo).andExpect(status().isCreated()));
        Map<String, String> credencial = (Map<String, String>) corpo.get("credencial");
        return new Conta(
                uuid(resposta.at("/usuario/id")),
                credencial.get("email"),
                (String) corpo.get("deviceId"),
                resposta.get("accessToken").asString(),
                resposta.get("refreshToken").asString(),
                uuid(resposta.get("fazendaId")));
    }

    public ResultActions get(String rota, Conta conta) throws Exception {
        return mvc.perform(autenticada(MockMvcRequestBuilders.get(rota), conta));
    }

    public ResultActions getComToken(String rota, String token) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get(rota).header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }

    public ResultActions getAnonimo(String rota) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get(rota));
    }

    public ResultActions post(String rota, Object corpo) throws Exception {
        return mvc.perform(comCorpo(MockMvcRequestBuilders.post(rota), corpo));
    }

    public ResultActions post(String rota, Conta conta, Object corpo) throws Exception {
        return mvc.perform(autenticada(comCorpo(MockMvcRequestBuilders.post(rota), corpo), conta));
    }

    public ResultActions patch(String rota, Conta conta, Object corpo) throws Exception {
        return mvc.perform(autenticada(comCorpo(MockMvcRequestBuilders.patch(rota), corpo), conta));
    }

    public ResultActions delete(String rota, Conta conta) throws Exception {
        return mvc.perform(autenticada(MockMvcRequestBuilders.delete(rota), conta));
    }

    public ResultActions enviarAvatar(Conta conta, String nomeDoArquivo, String contentType, byte[] conteudo)
            throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", nomeDoArquivo, contentType, conteudo);
        return mvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/usuarios/me/avatar")
                .file(arquivo)
                .header(HttpHeaders.AUTHORIZATION, conta.bearer()));
    }

    public ResultActions sync(Conta conta, List<Map<String, Object>> ops) throws Exception {
        return sync(conta, SCHEMA_VERSION, ops);
    }

    public ResultActions sync(Conta conta, int schemaVersion, List<Map<String, Object>> ops) throws Exception {
        return post("/sync/upload", conta, Map.of("schemaVersion", schemaVersion, "ops", ops));
    }

    public JsonNode json(ResultActions resposta) throws Exception {
        return json.readTree(resposta.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    /** Códigos das operações rejeitadas por POST /sync/upload, na ordem do lote. */
    public List<String> codigosRejeitados(ResultActions resposta) throws Exception {
        return json(resposta).get("rejeitadas").valueStream().map(rejeitada -> rejeitada.get("codigo").asString())
                .toList();
    }

    public static UUID uuid(JsonNode no) {
        return UUID.fromString(no.asString());
    }

    private MockHttpServletRequestBuilder comCorpo(MockHttpServletRequestBuilder requisicao, Object corpo) {
        if (corpo == null) {
            return requisicao;
        }
        return requisicao.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(corpo));
    }

    private static MockHttpServletRequestBuilder autenticada(MockHttpServletRequestBuilder requisicao, Conta conta) {
        return requisicao.header(HttpHeaders.AUTHORIZATION, conta.bearer());
    }
}
