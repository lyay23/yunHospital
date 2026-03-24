package com.neuedu.hisweb.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.User;
import com.neuedu.hisweb.service.AuthSessionService;
import com.neuedu.hisweb.utils.JwtUtils;
import com.neuedu.hisweb.utils.UserUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthSessionService authSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("token");
        try {
            if (token == null || token.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtUtils.verify(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            DecodedJWT decodedJWT = jwtUtils.parseToken(token);
            if (decodedJWT == null || !authSessionService.isTokenActive(token, decodedJWT)) {
                filterChain.doFilter(request, response);
                return;
            }

            String kind = decodedJWT.getClaim("kind").asString();
            Integer userType = decodedJWT.getClaim("userType").asInt();
            Set<String> roles = "CUSTOMER".equals(kind) ? RoleMapper.rolesForCustomer() : RoleMapper.rolesForUserType(userType);
            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            Object principal = jwtUtils.getUserByToken(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (principal instanceof User user) {
                UserUtils.setLoginUser(user);
                request.getSession().setAttribute("user", user);
            } else if (principal instanceof Customer customer) {
                UserUtils.setLoginCustomer(customer);
                request.getSession().setAttribute("user", customer);
            }

            filterChain.doFilter(request, response);
        } finally {
            UserUtils.removeUser();
            UserUtils.removeCustomer();
            SecurityContextHolder.clearContext();
        }
    }
}
