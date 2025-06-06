package com.br.Entity;

import com.br.Enums.Role;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class Super {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    private Role roles;
    @Transient
    private String userType;

}
