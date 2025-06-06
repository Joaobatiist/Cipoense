package com.br.Enums;

public enum Role {
    SUPERVISOR,
    COORDENADOR,
    TECNICO,
    ALUNO;

    public String getAuthorityName() {
        return "ROLE_" + this.name(); // Retorna "ROLE_SUPERVISOR", "ROLE_COORDENADOR", etc.
    }
}
