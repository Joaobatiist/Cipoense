package com.br.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class AtletaListagemResponse {
        private Long id;
        private String nomeCompleto;
        private String subDivisao;
}
