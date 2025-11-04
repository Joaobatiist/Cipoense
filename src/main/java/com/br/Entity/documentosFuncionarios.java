package com.br.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Documentos_Funcionarios")
public class documentosFuncionarios {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    private String descricao;
    @Lob
    private byte[] documentos;
    private String nomeArquivo;
    LocalDateTime dataUpload;
}
