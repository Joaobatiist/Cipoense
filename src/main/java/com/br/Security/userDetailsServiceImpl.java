package com.br.Security;

import com.br.Repository.supervisorRepository;
import com.br.Repository.coordenadorRepository;
import com.br.Repository.tecnicoRepository;
import com.br.Repository.atletaRepository;

import com.br.Entity.Super;
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
import java.util.stream.Stream;

@Service
public class userDetailsServiceImpl implements UserDetailsService {

    private final supervisorRepository supervisorRepository;
    private final coordenadorRepository coordenadorRepository;
    private final tecnicoRepository tecnicoRepository;
    private final atletaRepository atletaRepository;

    public userDetailsServiceImpl(supervisorRepository supervisorRepository,
                                  coordenadorRepository coordenadorRepository,
                                  tecnicoRepository tecnicoRepository,
                                  atletaRepository atletaRepository) {
        this.supervisorRepository = supervisorRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.atletaRepository = atletaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<? extends Super> foundSuperUser = Stream.of(
                        supervisorRepository.findByEmail(email),
                        coordenadorRepository.findByEmail(email),
                        tecnicoRepository.findByEmail(email)
                )
                .flatMap(Optional::stream)
                .findFirst();

        if (foundSuperUser.isPresent()) {
            Super user = foundSuperUser.get();
            // Retorna CustomUserDetails com as roles sem o prefixo "ROLE_"
            return new customUserDetails(
                    user.getId(),
                    user.getEmail(),
                    user.getSenha(),
                    getAuthorities(user.getRoles()), // Retorna autoridades como "SUPERVISOR"
                    user.getRoles().name(),
                    user.getNome()
            );
        }

        Optional<atleta> foundAtleta = atletaRepository.findByEmail(email);
        if (foundAtleta.isPresent()) {
            atleta atleta = foundAtleta.get();
            // Retorna CustomUserDetails com as roles sem o prefixo "ROLE_"
            return new customUserDetails(
                    atleta.getId(),
                    atleta.getEmail(),
                    atleta.getSenha(),
                    getAuthorities(atleta.getRoles()), // Retorna autoridades como "ATLETA"
                    atleta.getRoles().name(),
                    atleta.getNome()
            );
        }

        throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(role role) {
        if (role == null) {
            return Collections.emptyList();
        }
        // Cria SimpleGrantedAuthority usando o nome da autoridade sem o prefixo "ROLE_"
        return Collections.singletonList(new SimpleGrantedAuthority(role.getAuthorityName()));
    }
}