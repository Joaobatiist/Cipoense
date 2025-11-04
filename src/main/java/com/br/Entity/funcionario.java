package com.br.Entity;

import com.br.Enums.role;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table (name = "funcionario")
public class funcionario {
    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;
    private String nome;
    @Column(unique = true)
    private String email;
    private String senha;
    private LocalDate dataNascimento;
    @Column(unique = true)
    private String cpf;
    @Column(unique = true)
    private String telefone;
    @Enumerated(EnumType.STRING)
    private role role;
    @Transient
    private String userType;

}
