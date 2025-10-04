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
@Table(name="eventos")
public class eventos {

    @Id
    @UuidGenerator
    @GeneratedValue
    private UUID id;
    private String data;
    private String descricao;
    private String professor;
    private String local;
    private String horario;

}
