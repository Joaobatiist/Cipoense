
package com.br.Entity;

import com.br.Enums.posicao;
import com.br.Enums.role;
import com.br.Enums.subDivisao;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

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
            this.matricula = (int) (Math.random() * 1000000);
        }
    }

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name="matricula", nullable = false, unique = true)
    private Integer matricula;

    //Informações Pessoais Essenciais
    @Column(name="nome", nullable = false)
    private String nome;

    @Column(name="senha", nullable = false)
    private String senha;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private role roles;

    @Column(name = "data_de_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "cpf", nullable = true, unique = true)
    private String cpf;

    @Column(name = "RG", nullable = true, unique = true)
    private String rg;

    @Column(name = "endereço")
    private String endereco;

    //Saúde e caracteristicas

    @Column(name = "massa", nullable = false)
    private double massa;

    @Column(name = "altura", nullable = false)
    private double altura;

    @Column(name = "tipoSanguineo")
    private String tipoSanguineo;

    @Column(name = "alergias")
    private String alergias;

    @Column(name = "problemaDeSaude")
    private String problemaDeSaude;

    //Escola
    @Column(name = "horarioDeAula")
    private String horarioDeAula;

    @Column(name = "escola")
    private String escola;

    @Column(name = "contatoEscola")
    private String contatoEscola;

    @Column(name = "anoEscolar")
    private Integer anoEscolar;

    //Informações Cadastrais Organizacionais

    @Enumerated(EnumType.STRING)
    private posicao posicao;

    @Enumerated(EnumType.STRING)
    private subDivisao subDivisao;

    @Column(name = "foto", columnDefinition = "TEXT")
    private String foto;

    @Column(name = "foto_content_type")
    private String fotoContentType;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "responsavel_id")
    private responsavel responsavel;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
    @JoinColumn(name= "responsavel2_id")
    private responsavel responsavel2;

    @Column(name = "isencao", nullable = true)
    private boolean isencao;

    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<documentoAtleta> documentos = new HashSet<>();

    @ManyToMany(mappedBy = "destinatariosAtletas")
    private Set<comunicado> comunicadosRecebidos = new HashSet<>();

    @Column(name = "is_apto_para_jogar", nullable = false)
    private Boolean isAptoParaJogar = true;

    // ALTERAÇÃO: Mudança de byte[] para String (Base64)
    @Column(name = "documento_pdf_bytes", columnDefinition = "LONGTEXT")
    private String documentoPdfBytes;

    @Column(name = "documento_pdf_content_type")
    private String documentoPdfContentType;

    @ManyToMany(mappedBy = "atletasEscalados")
    private Set<eventos> eventos = new HashSet<>();

    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<presenca> presencas;

    public int getIdade() {
        if (this.dataNascimento != null) {
            return Period.between(this.dataNascimento, LocalDate.now()).getYears();
        }
        return 0;
    }


    public Set<eventos> getEventos() {
        return eventos;
    }

    public void setEventos(Set<eventos> eventos) {
        this.eventos = eventos;
    }
}