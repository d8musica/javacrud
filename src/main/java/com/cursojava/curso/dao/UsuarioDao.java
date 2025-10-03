package com.cursojava.curso.dao;

import com.cursojava.curso.models.Usuario;

import java.util.List;

public interface UsuarioDao {
    List<Usuario> getUsuarios();
    void eliminarUsuario(Long id);
    void registrar(Usuario usuario);
    Usuario obtenerUsuarioPorCredenciales(String email, String password);
    boolean verificarCredenciales(String email, String password);
    Usuario obtenerUsuarioPorEmail(String email);
}
