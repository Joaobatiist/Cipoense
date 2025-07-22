package com.br.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import org.slf4j.Logger; // ⭐ ADICIONAR ESTE IMPORT
import org.slf4j.LoggerFactory; // ⭐ ADICIONAR ESTE IMPORT

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class); // ⭐ ADICIONAR LOG

    private final JwtUtil jwtUtil;
    private final com.br.Security.UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, com.br.Security.UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        logger.info("----------- JwtAuthenticationFilter: Iniciando o filtro para URI: {} -----------", request.getRequestURI()); // ⭐ LOG
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Verifica se o cabeçalho de autorização existe e começa com "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("JwtAuthenticationFilter: Cabeçalho Authorization ausente ou malformado. Prosseguindo sem autenticação JWT."); // ⭐ LOG
            filterChain.doFilter(request, response); // Continua a cadeia de filtros sem autenticar
            return;
        }

        jwt = authHeader.substring(7); // Extrai o token JWT (depois de "Bearer ")
        logger.info("JwtAuthenticationFilter: Token JWT extraído: {}...", jwt.substring(0, Math.min(jwt.length(), 50))); // ⭐ LOG (limita o tamanho)

        try {
            userEmail = jwtUtil.extractUsername(jwt); // Extrai o email do token
            logger.info("JwtAuthenticationFilter: Email/Username extraído do token: {}", userEmail); // ⭐ LOG

            // Se o email foi extraído e o usuário ainda não está autenticado no contexto de segurança
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); // Carrega os detalhes do usuário
                logger.info("JwtAuthenticationFilter: UserDetails carregado para: {}", userDetails.getUsername()); // ⭐ LOG

                // Valida o token com os detalhes do usuário
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    logger.info("JwtAuthenticationFilter: Token é válido para o usuário: {}", userDetails.getUsername()); // ⭐ LOG

                    // Cria um objeto de autenticação e o define no contexto de segurança
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // A senha já foi verificada pelo JWT
                            userDetails.getAuthorities() // As roles do usuário
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("JwtAuthenticationFilter: Usuário '{}' autenticado no SecurityContext com roles: {}", userDetails.getUsername(), userDetails.getAuthorities()); // ⭐ LOG
                } else {
                    logger.warn("JwtAuthenticationFilter: Token JWT inválido para o usuário: {}. Motivo: isTokenValid retornou false.", userEmail); // ⭐ LOG
                }
            } else if (userEmail == null) {
                logger.warn("JwtAuthenticationFilter: Não foi possível extrair o username do token JWT (userEmail é null)."); // ⭐ LOG
            } else {
                logger.info("JwtAuthenticationFilter: Usuário já autenticado no SecurityContext para a requisição: {}", request.getRequestURI()); // ⭐ LOG
            }

        } catch (Exception e) {
            logger.error("JwtAuthenticationFilter: Erro ao processar o token JWT para URI {}: {}", request.getRequestURI(), e.getMessage(), e); // ⭐ LOG
            // Você pode querer retornar um 401 aqui se o token for malformado/inválido
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // return;
        }

        filterChain.doFilter(request, response); // Continua a cadeia de filtros
        logger.info("----------- JwtAuthenticationFilter: Filtro finalizado para URI: {} -----------", request.getRequestURI()); // ⭐ LOG
    }
}