package com.br.Security;

import com.br.Repository.SupervisorRepository;
import com.br.Repository.CoordenadorRepository;
import com.br.Repository.TecnicoRepository;
import com.br.Repository.AtletaRepository;

import com.br.Entity.Super;
import com.br.Entity.Atleta;
import com.br.Enums.Role;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream; // Import java.util.stream.Stream

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SupervisorRepository supervisorRepository;
    private final CoordenadorRepository coordenadorRepository;
    private final TecnicoRepository tecnicoRepository;
    private final AtletaRepository atletaRepository;

    public UserDetailsServiceImpl(SupervisorRepository supervisorRepository,
                                  CoordenadorRepository coordenadorRepository,
                                  TecnicoRepository tecnicoRepository,
                                  AtletaRepository atletaRepository) {
        this.supervisorRepository = supervisorRepository;
        this.coordenadorRepository = coordenadorRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.atletaRepository = atletaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Use Stream.of to create a stream of Optionals, then flatMap to get the first present one
        Optional<? extends Super> foundSuperUser = Stream.of(
                        supervisorRepository.findByEmail(email),
                        coordenadorRepository.findByEmail(email),
                        tecnicoRepository.findByEmail(email)
                )
                .flatMap(Optional::stream)
                .findFirst();
        if (foundSuperUser.isPresent()) {
            Super user = foundSuperUser.get();
            return User.builder()
                    .username(user.getEmail())
                    .password(user.getSenha()) // HASHED password from the database
                    .authorities(getAuthorities(user.getRoles()))
                    .build();
        }


        Optional<Atleta> foundAtleta = atletaRepository.findByEmail(email);
        if (foundAtleta.isPresent()) {
            Atleta atleta = foundAtleta.get();
            return User.builder()
                    .username(atleta.getEmail())
                    .password(atleta.getSenha()) // HASHED password from the database
                    .authorities(getAuthorities(atleta.getRoles()))
                    .build();
        }


        throw new UsernameNotFoundException("Usuário não encontrado com o email: " + email);
    }


    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        if (role == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role.getAuthorityName()));
    }
}