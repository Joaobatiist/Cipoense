package com.br.Request;

import com.br.Enums.subDivisao;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class eventoRequest {
    // Lista de IDs dos atletas a serem vinculados ao evento
    private List<UUID> atletasIds;

    // Getters e Setters (pode usar Lombok @Data se preferir)

    public List<UUID> getAtletasIds() {
        return atletasIds;
    }

    public void setAtletasIds(List<UUID> atletasIds) {
        this.atletasIds = atletasIds;
    }

}