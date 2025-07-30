package com.br.Security;

import com.br.Repository.SupervisorRepository;
import com.br.Repository.CoordenadorRepository;
import com.br.Repository.TecnicoRepository;
import com.br.Repository.AtletaRepository;

import com.br.Entity.Super;
import com.br.Entity.Atleta;
import com.br.Enums.Role;
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
            return new CustomUserDetails(
                    user.getId(),
                    user.getEmail(),
                    user.getSenha(),
                    getAuthorities(user.getRoles()), // Retorna autoridades como "SUPERVISOR"
                    user.getRoles().name(),
                    user.getNome()
            );
        }

        Optional<Atleta> foundAtleta = atletaRepository.findByEmail(email);
        if (foundAtleta.isPresent()) {
            Atleta atleta = foundAtleta.get();
            // Retorna CustomUserDetails com as roles sem o prefixo "ROLE_"
            return new CustomUserDetails(
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

    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        if (role == null) {
            return Collections.emptyList();
        }
        // Cria SimpleGrantedAuthority usando o nome da autoridade sem o prefixo "ROLE_"
        return Collections.singletonList(new SimpleGrantedAuthority(role.getAuthorityName()));
    }
}