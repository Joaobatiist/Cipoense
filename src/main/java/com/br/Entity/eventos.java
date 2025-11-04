package com.br.Entity;


import com.br.Enums.subDivisao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="eventos")
public class eventos {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;
    private LocalDate data;
    private String descricao;
    private String professor;
    private String local;
    private String horario;

    @Enumerated(EnumType.STRING)
    private subDivisao subDivisao;
    @JsonManagedReference("evento-presenca")
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<presenca> presencas = new ArrayList<>();
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "eventos_atletas",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "atleta_id")
    )
    private Set<atleta> atletasEscalados = new HashSet<>();
}
