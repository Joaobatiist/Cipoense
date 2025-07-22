package com.br.Entity;

import com.br.Enums.Posicao;
import com.br.Enums.Role;
import com.br.Enums.SubDivisao;
import jakarta.persistence.*;
import lombok.*;

import java.util.Random;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "atleta")
public class Atleta {

    @PrePersist
    public void gerarMatricula() {
        if (this.matricula == null) {
            Random random = new Random();
            int min = 100000;
            int max = 999999;
            this.matricula = random.nextInt(max - min + 1) + min;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="matricula", nullable = false, unique = true)
    private Integer matricula;

    @Column(name="nome", nullable = false)
    private String nome;

    @Column(name="senha", nullable = false)
    private String senha;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name = "isencao")
    private boolean isencao;

    @Enumerated(EnumType.STRING)
    private Posicao posicao;

    @Enumerated(EnumType.STRING)
    private SubDivisao subDivisao;

    @Enumerated(EnumType.STRING)
    private Role roles;

    @Column(name = "dataDeNascimento")
    private LocalDate dataNascimento;

    @Column(name = "cpf", nullable = true, unique = true)
    private String cpf;

    @Column(name = "massa", nullable = false)
    private double massa;

    @Lob
    @Column(name = "foto", columnDefinition = "LONGBLOB")
    private byte[] foto;

    // --- IMPORTANTE: Adicione o campo para o tipo de conteúdo da foto aqui ---
    @Column(name = "foto_content_type")
    private String fotoContentType;
    // -----------------------------------------------------------------------

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) // Adicione orphanRemoval = true se Responsavel deve ser excluído com Atleta
    @JoinColumn(name = "responsavel_id")
    private Responsavel responsavel;

    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DocumentoAtleta> documentos = new HashSet<>();

    @ManyToMany(mappedBy = "destinatariosAtletas")
    private Set<Comunicado> comunicadosRecebidos = new HashSet<>();

    public int getIdade() {
        if (this.dataNascimento == null) {
            return 0;
        }
        return LocalDate.now().getYear() - this.dataNascimento.getYear();
    }
}