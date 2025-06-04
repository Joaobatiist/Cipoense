// src/main/java/com/br/Security/UserDetailsServiceImpl.java
package com.br.Security; // Ajuste o pacote para security

import com.br.Repository.AlunoRepository; // Certifique-se de que o import esteja correto
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AlunoRepository alunoRepository;

    public UserDetailsServiceImpl(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return alunoRepository.findByEmail(email)
                .map(aluno -> User.builder()
                        .username(aluno.getEmail())
                        .password(aluno.getSenha()) // Esta é a senha HASHED do banco de dados
                        .authorities(getAuthorities(aluno.getRoles())) // Carrega as roles
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
    }

    // Método para converter a string de roles em GrantedAuthority
    private Collection<? extends GrantedAuthority> getAuthorities(String roles) {
        // Assume que as roles estão separadas por vírgula (ex: "ALUNO,TECNICO")
        return Arrays.stream(roles.split(","))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase()))
                .collect(Collectors.toList());
    }
}