package com.br.Controller;

import com.br.Repository.atletaRepository;
import com.br.Response.atletaProfileDto;
import com.br.Security.jwtUtil;
import com.br.Service.atletaProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/atleta/profile")
public class atletaProfileController {

    @Autowired
    private atletaProfileService atletaProfileService;

    @Autowired
    private atletaRepository atletaRepository; // Mantido, embora não seja estritamente necessário para a foto

    @Autowired
    private jwtUtil jwtUtil;

    @GetMapping
    @PreAuthorize("hasRole('ATLETA')")
    public ResponseEntity<atletaProfileDto> getProfile(HttpServletRequest request) {
        String token = extractToken(request);
        Long atletaId = jwtUtil.extractUserId(token);

        atletaProfileDto profile = atletaProfileService.getProfile(atletaId);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore()) // Adicionado para garantir que o navegador não cacheie o perfil
                .body(profile);
    }

    @PutMapping
    @PreAuthorize("hasRole('ATLETA')")
    public ResponseEntity<atletaProfileDto> updateProfile(
            HttpServletRequest request,
            @RequestBody atletaProfileDto profileDto) {
        String token = extractToken(request);
        Long atletaId = jwtUtil.extractUserId(token);

        atletaProfileDto updatedProfile = atletaProfileService.updateProfile(atletaId, profileDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping("/photo")
    @PreAuthorize("hasRole('ATLETA')")
    public ResponseEntity<String> uploadPhoto(HttpServletRequest request,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        String token = extractToken(request);
        Long atletaId = jwtUtil.extractUserId(token);

        // --- INÍCIO DA MUDANÇA ESSENCIAL AQUI ---
        // Chame o método do serviço que já faz o salvamento e retorna a string Base64
        String base64FotoUrl = atletaProfileService.uploadPhoto(atletaId, file);
        return ResponseEntity.ok(base64FotoUrl);

    }

    @PostMapping("/documents")
    @PreAuthorize("hasRole('ATLETA')")
    public ResponseEntity<List<atletaProfileDto.DocumentoDto>> uploadDocuments(
            HttpServletRequest request,
            @RequestParam("files") MultipartFile[] files) {
        String token = extractToken(request);
        Long atletaId = jwtUtil.extractUserId(token);

        List<atletaProfileDto.DocumentoDto> documents = atletaProfileService.uploadDocuments(atletaId, files);
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/documents/{documentId}")
    @PreAuthorize("hasRole('ATLETA')")
    public ResponseEntity<Void> deleteDocument(
            HttpServletRequest request,
            @PathVariable Long documentId) {
        String token = extractToken(request);
        Long atletaId = jwtUtil.extractUserId(token);

        atletaProfileService.deleteDocument(atletaId, documentId);
        return ResponseEntity.noContent().build();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Token JWT não encontrado");
    }
}