package br.com.estetica.automotiva.service;

import br.com.estetica.automotiva.model.Cliente;
import br.com.estetica.automotiva.model.Role;
import br.com.estetica.automotiva.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final UsuarioService usuarioService;

    public ClienteService(ClienteRepository clienteRepository, UsuarioService usuarioService) {
        this.clienteRepository = clienteRepository;
        this.usuarioService = usuarioService;
    }

    public Cliente cadastrarCliente(Cliente cliente, String senha, String placasTexto) {
        if (!cliente.isConsentimentoLgpd()) {
            throw new IllegalArgumentException("Consentimento LGPD e obrigatorio para cadastro");
        }
        if (clienteRepository.existsByEmail(cliente.getEmail()) || clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new IllegalArgumentException("Cliente ja cadastrado com este e-mail ou CPF");
        }
        cliente.setPlacas(parsePlacas(placasTexto));
        Cliente salvo = clienteRepository.save(cliente);
        usuarioService.criarUsuario(cliente.getEmail(), senha, Role.CLIENTE);
        return salvo;
    }

    public Cliente atualizarCliente(String id, Cliente dados, String placasTexto) {
        Cliente atual = buscarPorId(id);
        usuarioService.atualizarEmail(atual.getEmail(), dados.getEmail());
        atual.setNome(dados.getNome());
        atual.setCpf(dados.getCpf());
        atual.setTelefone(dados.getTelefone());
        atual.setEmail(dados.getEmail());
        atual.setPlacas(parsePlacas(placasTexto));
        return clienteRepository.save(atual);
    }

    public List<String> parsePlacas(String placasTexto) {
        if (placasTexto == null || placasTexto.isBlank()) return List.of();
        return Arrays.stream(placasTexto.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(p -> !p.isBlank())
                .toList();
    }

    public Cliente buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado"));
    }

    public Cliente buscarPorId(String id) {
        return clienteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado"));
    }

    public List<Cliente> listarTodos() { return clienteRepository.findAll(); }
    public void excluirDefinitivo(String id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.deleteById(id);
        usuarioService.excluirPorEmail(cliente.getEmail());
    }
}
