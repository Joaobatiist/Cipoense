package com.br.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name="Tecnico")
public class Tecnico extends Super{
}
