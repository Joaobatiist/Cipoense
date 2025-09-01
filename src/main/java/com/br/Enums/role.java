package com.br.Enums;

public enum role {
    SUPERVISOR,
    COORDENADOR,
    TECNICO,
    ATLETA;

    public String getAuthorityName() {
        return  this.name();
    }
}
