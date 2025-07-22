package com.br.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Comunicado")
public class Comunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assunto;
    private String mensagem;
    private LocalDate data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remetente_coordenador_id") // ID do coordenador remetente
    private Coordenador remetenteCoordenador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remetente_supervisor_id") // ID do supervisor remetente
    private Supervisor remetenteSupervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remetente_tecnico_id") // ID do técnico remetente
    private Tecnico remetenteTecnico;

    // Destinatários Alunos
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "comunicado_destinatario_atletas",
            joinColumns = @JoinColumn(name = "comunicado_id"),
            inverseJoinColumns = @JoinColumn(name = "atleta_id")
    )
    private Set<Atleta> destinatariosAtletas = new HashSet<>();


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "comunicado_destinatario_coordenador", // Nova tabela de junção
            joinColumns = @JoinColumn(name = "comunicado_id"),
            inverseJoinColumns = @JoinColumn(name = "coordenador_id")
    )
    private Set<Coordenador> destinatariosCoordenadores = new HashSet<>();


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "comunicado_destinatario_supervisor", // Nova tabela de junção
            joinColumns = @JoinColumn(name = "comunicado_id"),
            inverseJoinColumns = @JoinColumn(name = "supervisor_id")
    )
    private Set<Supervisor> destinatariosSupervisores = new HashSet<>();

    // Destinatários Técnicos
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "comunicado_destinatario_tecnico", // Nova tabela de junção
            joinColumns = @JoinColumn(name = "comunicado_id"),
            inverseJoinColumns = @JoinColumn(name = "tecnico_id")
    )
    private Set<Tecnico> destinatariosTecnicos = new HashSet<>();


    public void addDestinatarioAtleta(Atleta atleta) {
        this.destinatariosAtletas.add(atleta);
    }
    public void removeDestinatarioAtleta(Atleta atleta) {
        this.destinatariosAtletas.remove(atleta);
    }

    public void addDestinatarioCoordenador(Coordenador coordenador) {
        this.destinatariosCoordenadores.add(coordenador);
    }
    public void removeDestinatarioCoordenador(Coordenador coordenador) {
        this.destinatariosCoordenadores.remove(coordenador);
    }

    public void addDestinatarioSupervisor(Supervisor supervisor) {
        this.destinatariosSupervisores.add(supervisor);
    }
    public void removeDestinatarioSupervisor(Supervisor supervisor) {
        this.destinatariosSupervisores.remove(supervisor);
    }

    public void addDestinatarioTecnico(Tecnico tecnico) {
        this.destinatariosTecnicos.add(tecnico);
    }
    public void removeDestinatarioTecnico(Tecnico tecnico) {
        this.destinatariosTecnicos.remove(tecnico);
    }
}