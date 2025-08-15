package com.br.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // 1. Endpoints públicos (não requerem autenticação)
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/atletas/nomes").permitAll()

                        // 2. Regras de autorização específicas (mais específicas primeiro)
                        .requestMatchers(HttpMethod.POST, "/cadastro/funcionarios").hasAnyAuthority("SUPERVISOR", "COORDENADOR")

                        // --- AJUSTE AQUI: Endpoint de análise de IA do próprio atleta ---
                        // Apenas o ATLETA pode ver sua própria análise de desempenho
                        // O endpoint agora é '/api/atleta/minha-analise', como na classe que te passei
                        .requestMatchers(HttpMethod.GET, "/api/atleta/minha-analise").hasAuthority("ATLETA")
                        // O endpoint '/api/analise/meu-desempenho' antigo foi removido para evitar conflitos.
                        // Caso você mantenha o controller antigo, use essa linha:
                        // .requestMatchers(HttpMethod.GET, "/api/analise/meu-desempenho").hasAuthority("ATLETA")

                        // Endpoints do ATLETA para seu próprio perfil
                        .requestMatchers(HttpMethod.GET,"/api/atleta/profile/**").hasAuthority("ATLETA")
                        .requestMatchers(HttpMethod.POST,"/api/atleta/documents").hasAuthority("ATLETA")
                        .requestMatchers(HttpMethod.DELETE,"/api/atleta/documents/{documentId}").hasAuthority("ATLETA")

                        // Visualização das análises de outros atletas
                        .requestMatchers(HttpMethod.GET, "/api/analises/atleta/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints do SUPERVISOR/COORDENADOR/TECNICO para atletas (CRUD de documentos, etc.)
                        .requestMatchers(HttpMethod.GET,"/api/supervisor/atletas/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT,"/api/supervisor/atletas/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.DELETE,"/api/supervisor/atletas/{atletaId}/documento-pdf").hasAuthority("SUPERVISOR")
                        .requestMatchers(HttpMethod.POST,"/api/supervisor/atletas/{atletaId}/documento-pdf").hasAnyAuthority("SUPERVISOR", "COORDENADOR")

                        // Endpoints de ESTOQUE
                        .requestMatchers(HttpMethod.POST, "/api/estoque").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.GET, "/api/estoque", "/api/estoque/{id}").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/estoque/{id}").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/estoque/{id}").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de USUÁRIOS PARA COMUNICADO
                        .requestMatchers(HttpMethod.GET, "/api/usuarios-para-comunicado").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de PRESENÇA
                        .requestMatchers(HttpMethod.POST, "/api/presenca/registrar").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.GET, "/api/presenca/atletas", "/api/presenca/presentes", "/api/presenca/historico").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de COMUNICADOS (Criação/Modificação/Deleção)
                        .requestMatchers(HttpMethod.POST, "/api/comunicados").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/comunicados/{id}").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/comunicados/{id}").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de CADASTRO de Alunos/Responsáveis
                        .requestMatchers(HttpMethod.POST, "/api/cadastro").hasAnyAuthority("SUPERVISOR", "COORDENADOR")

                        // Acesso à lista de Atletas (geral)
                        .requestMatchers(HttpMethod.GET, "/api/atletas").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.GET, "/api/atletas/listagem").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.GET, "/api/atletas/subdivisoes").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de EVENTOS (Criação/Modificação/Deleção)
                        .requestMatchers(HttpMethod.POST, "/api/eventos").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/eventos/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/eventos/**").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        // Permite que qualquer usuário autenticado (incluindo ATLETA) veja seus próprios eventos
                        .requestMatchers(HttpMethod.GET, "/api/eventos").authenticated()

                        // Endpoints de Relatórios (Geral, Tático, Desempenho) - Criação/Modificação/Deleção
                        .requestMatchers(HttpMethod.POST, "/api/relatoriogeral/cadastrar").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/relatoriogeral/{id}").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/relatoriogeral/{id}").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        // Visualização de relatórios gerais para todos os envolvidos
                        // ATENÇÃO: Se o atleta puder ver todos, o controle de acesso por ID deve estar no controller/service.
                        .requestMatchers(HttpMethod.GET, "/api/relatoriogeral/visualizar/{id}").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO", "ATLETA")
                        .requestMatchers(HttpMethod.GET, "/api/relatoriogeral/buscarporid/{id}").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO", "ATLETA")
                        // Permite listar todos os relatórios (geralmente para a comissão técnica)
                        .requestMatchers(HttpMethod.GET, "/api/relatoriogeral/listar").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")

                        // Endpoints de Relatórios Táticos
                        .requestMatchers(HttpMethod.POST, "/api/relatorios/tatico").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/relatorios/tatico/atualizar").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/relatorios/tatico/deletar").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.GET, "/api/relatorios/tatico/visualizar").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO", "ATLETA")

                        // Endpoints de Relatórios de Desempenho
                        .requestMatchers(HttpMethod.POST, "/api/relatorios/desempenho").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/relatorios/desempenho/atualizar").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/relatorios/desempenho/deletar").hasAnyAuthority("SUPERVISOR", "COORDENADOR")
                        .requestMatchers(HttpMethod.GET, "/api/relatorios/desempenho/visualizar").hasAnyAuthority("SUPERVISOR", "COORDENADOR", "TECNICO", "ATLETA")

                        // Endpoints que exigem APENAS AUTENTICAÇÃO (qualquer usuário logado)
                        .requestMatchers(HttpMethod.GET, "/api/comunicados").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/comunicados/{id}").authenticated()

                        // Regras de CATCH-ALL para sub-caminhos (sempre no final das regras específicas)
                        .requestMatchers("/api/supervisor/**").hasAuthority("SUPERVISOR")
                        .requestMatchers("/api/coordenador/**").hasAuthority("COORDENADOR")
                        .requestMatchers("/api/tecnico/**").hasAuthority("TECNICO")

                        // Acesso a endpoints do atleta em geral
                        // Esta regra é mais ampla, então a movi para o final do bloco
                        // das regras do atleta.
                        .requestMatchers("/api/atleta/**").hasAuthority("ATLETA")

                        // Última regra: qualquer outra requisição não mapeada deve ser autenticada.
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}