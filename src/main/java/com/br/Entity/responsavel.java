package com.br.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="responsavel")
public class responsavel {
    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID Id;
    private String nome;
    @Column(unique = true)
    private String telefone;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String cpf;

}