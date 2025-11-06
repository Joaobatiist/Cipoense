package com.br.Request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class comunicadoRequest {

    private String assunto;
    private String mensagem;
    private LocalDate data;

    // IDs dos destinatários por tipo
    private List<UUID> atletasIds;
    private List<UUID> coordenadorIds;
    private List<UUID> supervisorIds;
    private List<UUID> tecnicoIds;

    // Construtores
    public comunicadoRequest() {}

    public comunicadoRequest(String assunto, String mensagem, LocalDate data,
                             List<UUID> atletasIds, List<UUID> coordenadorIds,
                             List<UUID> supervisorIds, List<UUID> tecnicoIds) {
        this.assunto = assunto;
        this.mensagem = mensagem;
        this.data = data;
        this.atletasIds = atletasIds;
        this.coordenadorIds = coordenadorIds;
        this.supervisorIds = supervisorIds;
        this.tecnicoIds = tecnicoIds;
    }

    // Getters e Setters
    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<UUID> getAtletasIds() {
        return atletasIds;
    }

    public void setAtletasIds(List<UUID> atletasIds) {
        this.atletasIds = atletasIds;
    }

    public List<UUID> getCoordenadorIds() {
        return coordenadorIds;
    }

    public void setCoordenadorIds(List<UUID> coordenadorIds) {
        this.coordenadorIds = coordenadorIds;
    }

    public List<UUID> getSupervisorIds() {
        return supervisorIds;
    }

    public void setSupervisorIds(List<UUID> supervisorIds) {
        this.supervisorIds = supervisorIds;
    }

    public List<UUID> getTecnicoIds() {
        return tecnicoIds;
    }

    public void setTecnicoIds(List<UUID> tecnicoIds) {
        this.tecnicoIds = tecnicoIds;
    }

    @Override
    public String toString() {
        return "ComunicadoRequest{" +
                "assunto='" + assunto + '\'' +
                ", mensagem='" + mensagem + '\'' +
                ", data=" + data +
                ", atletasIds=" + atletasIds +
                ", coordenadorIds=" + coordenadorIds +
                ", supervisorIds=" + supervisorIds +
                ", tecnicoIds=" + tecnicoIds +
                '}';
    }
}