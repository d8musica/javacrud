package com.cursojava.curso.dao;

import com.cursojava.curso.models.Usuario;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UsuarioDaoImp implements UsuarioDao {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Usuario> getUsuarios() {
        String query = "From Usuario";
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public void eliminarUsuario(Long id) {
        Usuario usuario = entityManager.find(Usuario.class, id);
        if (usuario != null) {
            entityManager.remove(usuario);
        }
    }

    @Override
    public void registrar(Usuario usuario) {
        entityManager.persist(usuario);
    }

    @Override
    public Usuario obtenerUsuarioPorCredenciales(String email, String password) {
        String query = "FROM Usuario WHERE email = :email";
        List<Usuario> usuarios = entityManager.createQuery(query, Usuario.class)
                .setParameter("email", email)
                .getResultList();

        String passHash = usuarios.isEmpty() ? null : usuarios.get(0).getPassword();

        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        return argon2.verify(passHash, password) ? usuarios.get(0) : null;

    }

    @Override
    public boolean verificarCredenciales(String email, String password) {
        Usuario usuario = obtenerUsuarioPorCredenciales(email, password);
        return usuario != null;
    }

    @Override
    public Usuario obtenerUsuarioPorEmail(String email) {
        String query = "FROM Usuario WHERE email = :email";
        List<Usuario> usuarios = entityManager.createQuery(query, Usuario.class)
                .setParameter("email", email)
                .getResultList();

        return usuarios.isEmpty() ? null : usuarios.get(0);
    }
}
