package com.cursojava.curso.controllers;

import com.cursojava.curso.dao.UsuarioDao;
import com.cursojava.curso.models.Usuario;
import com.cursojava.curso.utils.JWTUtil;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private JWTUtil jwtUtil;

    @RequestMapping(value="/api/login", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> login(@RequestBody Usuario loginData) {

        // First, get user by email (without password verification)
        Usuario usuario = usuarioDao.obtenerUsuarioPorEmail(loginData.getEmail());

        if (usuario != null) {
            // Verify password using Argon2
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

            if (argon2.verify(usuario.getPassword(), loginData.getPassword())) {
                // Password matches, generate JWT token
                String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getId());

                // Create response object
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("usuario", usuario);

                // Don't return the password for security
                usuario.setPassword(null);

                System.out.println("User " + usuario.getId() + " (" + usuario.getEmail() + ") logged in successfully");

                return ResponseEntity.ok(response);
            }
        }

        // Invalid credentials
        System.out.println("Failed login attempt for email: " + loginData.getEmail());
        return ResponseEntity.status(401).build();
    }
}
