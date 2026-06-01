package br.com.estetica.automotiva.service;

import br.com.estetica.automotiva.model.Role;
import br.com.estetica.automotiva.model.Usuario;
import br.com.estetica.automotiva.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .disabled(!usuario.isAtivo())
                .roles(usuario.getRole().name())
                .build();
    }

    public Usuario criarUsuario(String email, String senha, Role role) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail ja cadastrado");
        }
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(senha));
        usuario.setRole(role);
        return usuarioRepository.save(usuario);
    }

    public void atualizarEmail(String emailAtual, String novoEmail) {
        if (emailAtual.equals(novoEmail)) return;
        if (usuarioRepository.existsByEmail(novoEmail)) {
            throw new IllegalArgumentException("E-mail ja cadastrado");
        }
        Usuario usuario = usuarioRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
        usuario.setEmail(novoEmail);
        usuarioRepository.save(usuario);
    }

    public void excluirPorEmail(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuarioRepository::delete);
    }
}
