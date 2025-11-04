package com.br.Controller;

import com.br.Entity.documentosFuncionarios;
import com.br.Repository.documentosFuncionariosRepository;
import com.br.Service.documentosFuncionariosService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class documentosFuncionariosControle {

    private final documentosFuncionariosService documentosFuncionariosService;
    private final documentosFuncionariosRepository documentosFuncionariosRepository;

    public documentosFuncionariosControle(documentosFuncionariosService documentosFuncionariosService, documentosFuncionariosRepository documentosFuncionariosRepository) {
        this.documentosFuncionariosService = documentosFuncionariosService;
        this.documentosFuncionariosRepository = documentosFuncionariosRepository;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<documentosFuncionarios> cadastrarDocumentos(

            @RequestParam("file") MultipartFile file,

            @RequestParam("descricao") String descricao


    ) {
        try {
            // Crie o objeto Entity aqui, usando o byte[] do arquivo
            documentosFuncionarios novoDocumento = new documentosFuncionarios();
            novoDocumento.setDescricao(descricao);

            novoDocumento.setDocumentos(file.getBytes()); // Converte o arquivo para byte[]
            novoDocumento.setDataUpload(LocalDateTime.now());

            // Chama o Service
            documentosFuncionarios item = documentosFuncionariosService.cadastroDocumentosFuncionarios(novoDocumento);

            return ResponseEntity.status(HttpStatus.CREATED).body(item);

        } catch (Exception e) {
           throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }



    // GET: CORRIGIDO para aceitar o ID como parâmetro de consulta.
    // Seu método no service será adaptado para buscar por ID ou retornar todos.
    @GetMapping("/buscar")
    public ResponseEntity<List<documentosFuncionarios>> buscarDocumentosFuncionarios (@RequestParam(required = false) UUID id) {
        try{
            List<documentosFuncionarios> buscar = documentosFuncionariosService.exibirDocumentosFuncionarios(id); // Passa o ID
            return ResponseEntity.ok(buscar);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e){
            System.err.println("Erro ao listar Documentos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // PUT: CORRIGIDO para usar @RequestBody, pois espera um corpo JSON com dados atualizados
    @PutMapping("/atualizar")
    public ResponseEntity<documentosFuncionarios> atualizarDocumentosFuncionarios (
            @RequestParam("id") UUID id, // OBRIGATÓRIO: ID do documento a ser atualizado
            @RequestParam(value = "file", required = false) MultipartFile file, // Opcional: Novo arquivo
            @RequestParam(value = "descricao", required = false) String descricao, // Opcional: Nova descrição
            @RequestParam(value = "nomeArquivo", required = false) String nomeArquivo // Opcional: Novo nome
    ) {

        try {
            // 1. CRIAR O OBJETO COM OS DADOS DA REQUISIÇÃO (Substitui o erro de variável inexistente)
            documentosFuncionarios dadosAtualizados = new documentosFuncionarios();
            dadosAtualizados.setId(id);
            dadosAtualizados.setDescricao(descricao);
            dadosAtualizados.setNomeArquivo(nomeArquivo);

            // 2. TRATAR O ARQUIVO (byte[])
            if (file != null && !file.isEmpty()) {
                dadosAtualizados.setDocumentos(file.getBytes());
                dadosAtualizados.setDataUpload(LocalDateTime.now());
            }

            // 3. CHAMA O SERVICE COM O OBJETO MONTADO
            documentosFuncionarios documentoAtualizado = documentosFuncionariosService.atualizarDocumentosFuncionarios(dadosAtualizados);

            return ResponseEntity.ok(documentoAtualizado);

        } catch (RuntimeException e) {
            // Captura exceção de "Documento não encontrado" lançada pelo Service
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar documento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/documento/download/{id}")
    public ResponseEntity<byte[]> downloadDocumento(@PathVariable UUID id) {
        try {
            documentosFuncionarios documento = documentosFuncionariosRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Documento não encontrado"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(documento.getNomeArquivo()) // substitua pelo campo correto
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(documento.getDocumentos()); // Seu @Lob byte[] documentos

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    // DELETE: CORRIGIDO para usar @PathVariable, pois o ID está na URL /apagar/{id}
    @DeleteMapping("/apagar/{id}")
    public ResponseEntity<Void> apagarDocumentosFuncionarios (@PathVariable UUID id) {
        try {
            documentosFuncionariosService.deletarDocumentosFuncionarios(id);
            return ResponseEntity.noContent().build(); // Status 204: OK, mas sem corpo
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Erro ao deletar Documentos: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}