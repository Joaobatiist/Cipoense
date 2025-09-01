package com.br.Response;

import com.br.Enums.posicao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor



public class atletaListagemResponse {
        private Long id;
        private String nomeCompleto;
        private String subDivisao;
        private posicao posicao;
        private String email;
        private String foto;

        public atletaListagemResponse(Long id, String nomeCompleto, String subDivisao, posicao posicao, String email) {
                this.id = id;
                this.nomeCompleto = nomeCompleto;
                this.subDivisao = subDivisao;
                this.posicao = posicao;
                this.email = email;



        }
}
