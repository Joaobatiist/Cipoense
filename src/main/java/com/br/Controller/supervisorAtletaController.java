package com.br.Controller;

import com.br.Entity.atleta;
import com.br.Repository.presencaRepository;
import com.br.Response.atletaProfileDto;
import com.br.Repository.presencaRepository;
import com.br.Service.atletaSupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor/atletas")
public class supervisorAtletaController {

    @Autowired
    private atletaSupervisorService atletaSupervisorService;

    @Autowired
    private  presencaRepository presencaRepository;



    @GetMapping

    public ResponseEntity<List<atletaProfileDto>> getAllAtletas() {
        List<atletaProfileDto> atletas = atletaSupervisorService.getAllAtletas();
        return ResponseEntity.ok(atletas);
    }

    @GetMapping("/{atletaId}")

    public ResponseEntity<atletaProfileDto> getAtletaById(@PathVariable UUID atletaId) {
        atletaProfileDto profile = atletaSupervisorService.getAtletaProfile(atletaId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{atletaId}")

    public ResponseEntity<atletaProfileDto> updateAtleta(
            @PathVariable UUID atletaId,
            @RequestBody atletaProfileDto profileDto) {
        atletaProfileDto updatedProfile = atletaSupervisorService.updateAtleta(atletaId, profileDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("deletar/{atletaId}")

    public ResponseEntity<Void> deleteAtleta(@PathVariable UUID atletaId) {
        atletaSupervisorService.deleteAtleta(atletaId);


        return ResponseEntity.noContent().build();
    }

    // --- NOVO ENDPOINT DE UPLOAD DE PDF (retorna Base64) ---
    @PostMapping("/{atletaId}/documento-pdf")

    public ResponseEntity<String> uploadAtletaDocumentoPdf(
            @PathVariable UUID atletaId,
            @RequestParam("file") MultipartFile file) throws IOException {
        String pdfBase64 = atletaSupervisorService.uploadDocumentoPdf(atletaId, file);
        return ResponseEntity.ok(pdfBase64);
    }

    // --- NOVO ENDPOINT PARA DELETAR O PDF PRINCIPAL ---
    @DeleteMapping("/{atletaId}/documento-pdf")

    public ResponseEntity<Void> deleteAtletaMainPdf(@PathVariable UUID atletaId) {
        atletaSupervisorService.deleteMainPdf(atletaId);
        return ResponseEntity.noContent().build();
    }


    // Mantenha os endpoints para outros documentos (DocumentoAtleta) se ainda forem usados
    // @DeleteMapping("/{atletaId}/documents/{documentId}")
    // @PreAuthorize("hasRole('SUPERVISOR')")
    // public ResponseEntity<Void> deleteAtletaDocument(...) { ... }
}