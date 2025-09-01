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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class jwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(jwtAuthenticationFilter.class);

    private final jwtUtil jwtUtil;
    private final userDetailsServiceImpl userDetailsService;

    public jwtAuthenticationFilter(jwtUtil jwtUtil, userDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        logger.info("----------- JwtAuthenticationFilter: Iniciando o filtro para URI: {} -----------", request.getRequestURI());
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("JwtAuthenticationFilter: Cabeçalho Authorization ausente ou malformado. Prosseguindo sem autenticação JWT.");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        logger.info("JwtAuthenticationFilter: Token JWT extraído: {}...", jwt.substring(0, Math.min(jwt.length(), 50)));

        try {
            userEmail = jwtUtil.extractUsername(jwt); // Extrai o email do token
            logger.info("JwtAuthenticationFilter: Email/Username extraído do token: {}", userEmail);


            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                logger.info("JwtAuthenticationFilter: UserDetails carregado para: {}", userDetails.getUsername());


                if (jwtUtil.validateToken(jwt, userDetails)) {
                    logger.info("JwtAuthenticationFilter: Token é válido para o usuário: {}", userDetails.getUsername());


                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("JwtAuthenticationFilter: Usuário '{}' autenticado no SecurityContext com roles: {}", userDetails.getUsername(), userDetails.getAuthorities());
                } else {
                    logger.warn("JwtAuthenticationFilter: Token JWT inválido para o usuário: {}. Motivo: isTokenValid retornou false.", userEmail);
                }
            } else if (userEmail == null) {
                logger.warn("JwtAuthenticationFilter: Não foi possível extrair o username do token JWT (userEmail é null).");
            } else {
                logger.info("JwtAuthenticationFilter: Usuário já autenticado no SecurityContext para a requisição: {}", request.getRequestURI());
            }

        } catch (Exception e) {
            logger.error("JwtAuthenticationFilter: Erro ao processar o token JWT para URI {}: {}", request.getRequestURI(), e.getMessage(), e);

        }

        filterChain.doFilter(request, response); // Continua a cadeia de filtros
        logger.info("----------- JwtAuthenticationFilter: Filtro finalizado para URI: {} -----------", request.getRequestURI());
    }
}