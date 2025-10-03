package com.br.Entity;

import com.br.Enums.posicao;
import com.br.Enums.role;
import com.br.Enums.subDivisao;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
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
public class atleta {

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

    @Column(name = "isencao", nullable = true)
    private boolean isencao;

    @Enumerated(EnumType.STRING)
    private posicao posicao;

    @Enumerated(EnumType.STRING)
    private subDivisao subDivisao;

    @Enumerated(EnumType.STRING)
    private role roles;

    @Column(name = "dataDeNascimento")
    private LocalDate dataNascimento;

    @Column(name = "cpf", nullable = true, unique = true)
    private String cpf;

    @Column(name = "massa", nullable = false)
    private double massa;

    @Lob
    @Column(name = "foto", columnDefinition = "LONGBLOB")
    private byte[] foto;

    @Column(name = "foto_content_type")
    private String fotoContentType;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "responsavel_id")
    private responsavel responsavel;

    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<documentoAtleta> documentos = new HashSet<>();

    @ManyToMany(mappedBy = "destinatariosAtletas")
    private Set<comunicado> comunicadosRecebidos = new HashSet<>();

    // --- NOVOS CAMPOS PARA PDF NO BANCO ---
    @Column(name = "is_apto_para_jogar", nullable = false)
    private Boolean isAptoParaJogar = true;

    @Lob
    @Column(name = "documento_pdf_bytes", columnDefinition = "LONGBLOB", nullable = true)
    private byte[] documentoPdfBytes;

    @Column(name = "documento_pdf_content_type", nullable = true)
    private String documentoPdfContentType;

    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<presenca> presencas;



    public int getIdade() {
        if (this.dataNascimento == null) {
            return 0;
        }
        return LocalDate.now().getYear() - this.dataNascimento.getYear();
    }
}