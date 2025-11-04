package com.br.Security;

import com.br.Repository.atletaRepository;
import com.br.Repository.funcionarioRepository; // Novo e único Repository para Funcionario
import com.br.Entity.funcionario;
import com.br.Entity.atleta;
import com.br.Enums.role;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class userDetailsServiceImpl implements UserDetailsService {

    private final funcionarioRepository funcionarioRepository; // Apenas o Repositório unificado
    private final atletaRepository atletaRepository;

    public userDetailsServiceImpl(funcionarioRepository funcionarioRepository,
                                  atletaRepository atletaRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.atletaRepository = atletaRepository;
    }

    // -------------------------------------------------------------------------
    // Método principal de carregamento de usuário
    // -------------------------------------------------------------------------

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Tenta encontrar o usuário na tabela unificada de Funcionarios
        Optional<funcionario> foundFuncionario = funcionarioRepository.findByEmail(email);

        if (foundFuncionario.isPresent()) {
            funcionario user = foundFuncionario.get();
            // Retorna CustomUserDetails com a role, que será COORDENADOR, SUPERVISOR ou TECNICO
            return new customUserDetails(
                    user.getId(),
                    user.getEmail(),
                    user.getSenha(),
                    getAuthorities(user.getRole()), // Usa a Role específica armazenada na entidade Funcionario
                    user.getRole().name(),
                    user.getNome()
            );
        }

        // 2. Se não for encontrado como Funcionario, tenta encontrar como Atleta
        Optional<atleta> foundAtleta = atletaRepository.findByEmail(email);
        if (foundAtleta.isPresent()) {
            atleta atleta = foundAtleta.get();
            // Retorna CustomUserDetails
            return new customUserDetails(
                    atleta.getId(),
                    atleta.getEmail(),
                    atleta.getSenha(),
                    getAuthorities(atleta.getRoles()),
                    atleta.getRoles().name(),
                    atleta.getNome()
            );
        }

        // 3. Se não for encontrado em nenhuma tabela
        throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
    }

    // -------------------------------------------------------------------------
    // Método auxiliar (inalterado)
    // -------------------------------------------------------------------------

    private Collection<? extends GrantedAuthority> getAuthorities(role role) {
        if (role == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role.getAuthorityName()));
    }
}