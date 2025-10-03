package com.cursojava.curso.controllers;

import com.cursojava.curso.dao.UsuarioDao;
import com.cursojava.curso.models.Usuario;
import com.cursojava.curso.utils.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioDao usuarioDao;
    @Autowired
    private JWTUtil jwtUtil;

    @RequestMapping(value="/api/usuario/{id}")
    public Usuario getUsuario(@PathVariable Long id) {
        // TODO: Replace with actual database lookup
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setEmail("juanperez@io.com");
        usuario.setTelefono("123456789");
        usuario.setPassword("12345");
        return usuario;
    }

    @RequestMapping(value="/api/usuarios")
    public ResponseEntity<List<Usuario>> getUsuarios(@RequestHeader(value="Authorization", required = false) String token) {
        // Validate JWT token and extract user ID
        String usuarioId = jwtUtil.validateAuthorizationHeader(token);

        // If token is invalid, return 401 Unauthorized
        if (usuarioId == null) {
            System.out.println("Authorization failed - token validation returned null");
            return ResponseEntity.status(401).build();
        }

        // Token is valid, proceed with getting users
        // Log which user is accessing the data (for audit purposes)
        System.out.println("User " + usuarioId + " is accessing users list");

        List<Usuario> usuarios = usuarioDao.getUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @RequestMapping(value="/api/usuarios", method = RequestMethod.POST)
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            // Hash the password using Argon2 before storing
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            String hash = argon2.hash(1, 1024, 1, usuario.getPassword());
            usuario.setPassword(hash);

            // Save user to database
            usuarioDao.registrar(usuario);

            return ResponseEntity.ok("Usuario registrado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al registrar usuario: " + e.getMessage());
        }
    }

    @RequestMapping(value="/api/usuarios/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> eliminarUsuario(
            @PathVariable Long id,
            @RequestHeader(value="Authorization") String token) {

        // Validate JWT token and extract user ID
        String usuarioId = jwtUtil.validateAuthorizationHeader(token);

        // If token is invalid, return 401 Unauthorized
        if (usuarioId == null) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        try {
            // Log which user is deleting data (for audit purposes)
            System.out.println("User " + usuarioId + " is deleting user with ID: " + id);

            // Proceed with deletion
            usuarioDao.eliminarUsuario(id);

            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error al eliminar usuario: " + e.getMessage());
        }
    }
}
