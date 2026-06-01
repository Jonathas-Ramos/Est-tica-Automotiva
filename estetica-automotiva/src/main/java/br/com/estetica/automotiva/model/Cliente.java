package br.com.estetica.automotiva.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "clientes")
public class Cliente {
    @Id
    private String id;

    @NotBlank
    private String nome;

    @Indexed(unique = true)
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 digitos")
    private String cpf;

    @NotBlank
    private String telefone;

    @Email
    @Indexed(unique = true)
    private String email;

    private List<String> placas = new ArrayList<>();
    private boolean consentimentoLgpd;
    private LocalDateTime dataCadastro = LocalDateTime.now();

    public String cpfParcial() {
        if (cpf == null || cpf.length() < 11) return "***";
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }

    public String telefoneParcial() {
        if (telefone == null || telefone.length() < 4) return "****";
        return "****" + telefone.substring(telefone.length() - 4);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getPlacas() { return placas; }
    public void setPlacas(List<String> placas) { this.placas = placas; }
    public boolean isConsentimentoLgpd() { return consentimentoLgpd; }
    public void setConsentimentoLgpd(boolean consentimentoLgpd) { this.consentimentoLgpd = consentimentoLgpd; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
}
