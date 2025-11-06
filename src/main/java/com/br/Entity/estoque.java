package com.br.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "Estoque")
public class estoque {


    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private Integer quantidade;
    @Column(nullable = false)
    private String  justificativa;
    private String data;


}
