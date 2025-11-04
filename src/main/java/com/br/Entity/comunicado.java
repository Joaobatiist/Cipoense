package com.br.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Comunicado")
public class comunicado {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;
    private String assunto;
    private String mensagem;
    private LocalDate data;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remetenteFuncionario_ID")
    private funcionario remetenteFuncionario;

    // Destinatários Alunos
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "comunicado_destinatario_atletas",
            joinColumns = @JoinColumn(name = "comunicado_id"),
            inverseJoinColumns = @JoinColumn(name = "atleta_id")
    )
    private Set<atleta> destinatariosAtletas = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "comunicado_destinatario_funcionario",
            joinColumns = @JoinColumn(name = "comunicado_id"),
            inverseJoinColumns = @JoinColumn(name = "funcionario_id")
    )
    private Set<funcionario> destinatariosFuncionarios = new HashSet<>();


    public void addDestinatarioFuncionario(funcionario funcionario) {this.destinatariosFuncionarios.add(funcionario);}
    public void removeDestinatarioFuncionario(funcionario funcionario) {this.destinatariosFuncionarios.remove(funcionario);}

    public void addDestinatarioAtleta(atleta atleta) {
        this.destinatariosAtletas.add(atleta);
    }
    public void removeDestinatarioAtleta(atleta atleta) {
        this.destinatariosAtletas.remove(atleta);
    };
}

