package com.br.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "presenca")
public class presenca {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "atleta_id", nullable = false)
    private atleta atleta;

    @Column(nullable = false)
    private Boolean presente;

    private LocalDate data;

}
