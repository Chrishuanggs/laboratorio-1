package com.ginomarin.persistencia.controllers;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoints de demostración para la clase. Cada uno muestra un tipo de regla distinta.
 */
@RestController
@RequestMapping("/api/v1/demo")
public class DemoSeguridadController {

    /** 1. Público: permitido en SecurityConfig con permitAll(). No necesita token. */
    @GetMapping("/publico")
    public Map<String, String> publico() {
        return Map.of("mensaje", "Cualquiera puede ver esto, incluso sin token");
    }

    /** 2. Autenticado: cualquier usuario con un token válido. Devuelve lo que Spring "ve" del token. */
    @GetMapping("/yo")
    public Map<String, Object> yo(JwtAuthenticationToken authentication, @AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "usuario", authentication.getName(),
                "email", String.valueOf(jwt.getClaimAsString("email")),
                "authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).sorted().toList(),
                "emisor", String.valueOf(jwt.getIssuer()),
                "expira", String.valueOf(jwt.getExpiresAt())
        );
    }

    /** 3. Por ROL con @PreAuthorize: solo ADMIN. hasRole('ADMIN') busca la authority "ROLE_ADMIN". */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> soloAdmin() {
        return Map.of("mensaje", "Bienvenido al panel de administración");
    }

    /** 4. Varios roles: ADMIN o VETERINARIO. */
    @GetMapping("/personal")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public Map<String, String> soloPersonal() {
        return Map.of("mensaje", "Área exclusiva del personal de la veterinaria");
    }

    /** 5. Por ROL con @Secured: equivalente al anterior pero sin expresiones (requiere el prefijo ROLE_). */
    @GetMapping("/veterinario")
    @Secured("ROLE_VETERINARIO")
    public Map<String, String> soloVeterinario() {
        return Map.of("mensaje", "Solo usuarios con rol VETERINARIO");
    }

    /** 6. Por PERMISO: no importa el rol, importa si el token trae el permiso. */
    @GetMapping("/permiso-eliminar")
    @PreAuthorize("hasAuthority('duenno:eliminar')")
    public Map<String, String> conPermisoEliminar() {
        return Map.of("mensaje", "Tienes el permiso duenno:eliminar");
    }

    /** 7. Regla con datos de la petición: cada quien ve su perfil, ADMIN ve todos. */
    @GetMapping("/perfil/{username}")
    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    public Map<String, String> perfil(@PathVariable String username) {
        return Map.of("perfil", username);
    }
}
