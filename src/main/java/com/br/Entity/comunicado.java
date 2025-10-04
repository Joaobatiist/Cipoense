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
    @JoinColumn(name = "remetente_coordenador_id") // ID do coordenador remetente
    private coordenador remetenteCoordenador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remetente_supervisor_id") // ID do supervisor remetente
    private supervisor remetenteSupervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remetente_tecnico_id") // ID do técnico remetente
    private tecnico remetenteTecnico;

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
            name = "comunicado_destinatario_coordenador", // Nova tabela de junção
            joinColumns = @JoinColumn(name = "comunicado_id"),
            inverseJoinColumns = @JoinColumn(name = "coordenador_id")
    )
    private Set<coordenador> destinatariosCoordenadores = new HashSet<>();


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "comunicado_destinatario_supervisor", // Nova tabela de junção
            joinColumns = @JoinColumn(name = "comunicado_id"),
            inverseJoinColumns = @JoinColumn(name = "supervisor_id")
    )
    private Set<supervisor> destinatariosSupervisores = new HashSet<>();

    // Destinatários Técnicos
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "comunicado_destinatario_tecnico", // Nova tabela de junção
            joinColumns = @JoinColumn(name = "comunicado_id"),
            inverseJoinColumns = @JoinColumn(name = "tecnico_id")
    )
    private Set<tecnico> destinatariosTecnicos = new HashSet<>();


    public void addDestinatarioAtleta(atleta atleta) {
        this.destinatariosAtletas.add(atleta);
    }
    public void removeDestinatarioAtleta(atleta atleta) {
        this.destinatariosAtletas.remove(atleta);
    }

    public void addDestinatarioCoordenador(coordenador coordenador) {
        this.destinatariosCoordenadores.add(coordenador);
    }
    public void removeDestinatarioCoordenador(coordenador coordenador) {
        this.destinatariosCoordenadores.remove(coordenador);
    }

    public void addDestinatarioSupervisor(supervisor supervisor) {
        this.destinatariosSupervisores.add(supervisor);
    }
    public void removeDestinatarioSupervisor(supervisor supervisor) {
        this.destinatariosSupervisores.remove(supervisor);
    }

    public void addDestinatarioTecnico(tecnico tecnico) {
        this.destinatariosTecnicos.add(tecnico);
    }
    public void removeDestinatarioTecnico(tecnico tecnico) {
        this.destinatariosTecnicos.remove(tecnico);
    }
}