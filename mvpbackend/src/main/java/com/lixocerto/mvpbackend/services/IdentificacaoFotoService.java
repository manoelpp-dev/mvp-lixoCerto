package com.lixocerto.mvpbackend.services;

import com.lixocerto.mvpbackend.dto.LixoDetalheDTO;
import com.lixocerto.mvpbackend.model.Lixo;
import com.lixocerto.mvpbackend.repositories.LixoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IdentificacaoFotoService {

    private final LixoRepository lixoRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public IdentificacaoFotoService(
            LixoRepository lixoRepository,
            @Value("${gemini.api.key}") String apiKey) {
        this.lixoRepository = lixoRepository;
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Transactional(readOnly = true)
    public LixoDetalheDTO identificarPorFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie uma foto.");
        }

        List<Lixo> lixos = lixoRepository.findAll();
        if (lixos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cadastre pelo menos 1 lixo no banco.");
        }

        String opcoes = lixos.stream().map(Lixo::getNome).collect(Collectors.joining(", "));
        String nomeIdentificado = consultarGemini(foto, opcoes);

        Lixo lixo = lixoRepository.findFirstByNomeContainingIgnoreCase(nomeIdentificado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Não foi possível identificar o resíduo: " + nomeIdentificado));

        return new LixoDetalheDTO(
                lixo.getId(),
                lixo.getNome(),
                lixo.getDescricao(),
                lixo.getCategoria().getTipo_de_lixo(),
                lixo.getInstrucaoDescarte().getCorLixo(),
                lixo.getInstrucaoDescarte().getInstrucao(),
                lixo.getInstrucaoDescarte().getSugestaoReciclagem()
        );
    }

    @SuppressWarnings("unchecked")
    private String consultarGemini(MultipartFile foto, String opcoes) {
        try {
            String base64 = Base64.getEncoder().encodeToString(foto.getBytes());
            String mime = foto.getContentType() != null ? foto.getContentType() : "image/jpeg";
            String prompt = "Classifique a imagem usando APENAS um destes nomes exatos: [" + opcoes + "]. Se não for nenhum, responda DESCONHECIDO.";

            Map<String, Object> body = Map.of(
                    "contents", List.of(Map.of("parts", List.of(
                            Map.of("text", prompt),
                            Map.of("inline_data", Map.of("mime_type", mime, "data", base64))
                    )))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-goog-api-key", apiKey);

            String host = "generativelanguage" + ".googleapis.com";
            String path = "/v1beta/models/gemini-flash-lite-latest:generateContent";
            URI uri = URI.create("https://" + host + path);

            ResponseEntity<String> res = restTemplate.postForEntity(uri, new HttpEntity<>(body, headers), String.class);

            Map<String, Object> map = objectMapper.readValue(res.getBody(), Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) map.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            return parts.get(0).get("text").toString().trim();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Falha na IA: " + e.getMessage());
        }
    }
}
