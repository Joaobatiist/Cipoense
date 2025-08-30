package com.br.Response;

import com.br.Enums.Posicao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor



public class AtletaListagemResponse {
        private Long id;
        private String nomeCompleto;
        private String subDivisao;
        private Posicao posicao;
        private String email;

        public AtletaListagemResponse (Long id, String nomeCompleto,  String subDivisao, Posicao posicao, String email) {
                this.id = id;
                this.nomeCompleto = nomeCompleto;
                this.subDivisao = subDivisao;
                this.posicao = posicao;
                this.email = email;


        }
}
