package br.com.estetica.automotiva.config;

import br.com.estetica.automotiva.model.Role;
import br.com.estetica.automotiva.model.TipoServico;
import br.com.estetica.automotiva.repository.TipoServicoRepository;
import br.com.estetica.automotiva.repository.UsuarioRepository;
import br.com.estetica.automotiva.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final TipoServicoRepository tipoServicoRepository;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public DataInitializer(UsuarioRepository usuarioRepository, UsuarioService usuarioService, TipoServicoRepository tipoServicoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.tipoServicoRepository = tipoServicoRepository;
    }

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            usuarioService.criarUsuario(adminEmail, adminPassword, Role.GESTOR);
        }
        if (tipoServicoRepository.count() == 0) {
            seed("Lavagem simples", "Lavagem externa com shampoo automotivo e secagem.", "50.00", 45);
            seed("Lavagem completa", "Lavagem externa, aspiracao interna, vidros e acabamento.", "90.00", 90);
            seed("Lavagem com cera", "Lavagem completa com aplicacao de cera protetora.", "130.00", 120);
            seed("Lavagem detalhada", "Higienizacao detalhada de interior, rodas, motor e acabamento fino.", "220.00", 180);
        }
    }

    private void seed(String nome, String descricao, String preco, int duracao) {
        TipoServico tipo = new TipoServico();
        tipo.setNome(nome);
        tipo.setDescricao(descricao);
        tipo.setPreco(new BigDecimal(preco));
        tipo.setDuracaoMinutos(duracao);
        tipoServicoRepository.save(tipo);
    }
}
