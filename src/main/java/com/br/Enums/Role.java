package com.br.Enums;

public enum Role {
    SUPERVISOR,
    COORDENADOR,
    TECNICO,
    ATLETA;

    public String getAuthorityName() {
        return  this.name();
    }
}
