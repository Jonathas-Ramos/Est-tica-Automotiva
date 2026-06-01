# Estetica Automotiva

Aplicacao Java Web para controle de clientes e agendamento autonomo de servicos em uma estetica automotiva.

## Funcionalidades

- Login completo com Spring Security, BCrypt e perfis `CLIENTE` e `GESTOR`.
- Cadastro autonomo de clientes com aceite LGPD.
- Gestao de clientes com edicao dos proprios dados pelo cliente, exclusao definitiva pelo gestor e listagem com CPF/telefone parcialmente anonimizados.
- Cadastro e listagem de tipos de servico com nome, descricao, preco vigente, duracao e status.
- Agendamento de servicos por clientes, condicionado a disponibilidade da agenda.
- Agenda diaria ou semanal para o gestor.
- Edicao e cancelamento de agendamentos por clientes e gestor.
- Validacao para impedir sobreposicao de horarios.

## Tecnologias

- Java 17
- Maven
- Spring Boot
- Spring Security
- Spring Data MongoDB
- Thymeleaf
- MongoDB Atlas

## Como executar no IntelliJ

1. Abra o IntelliJ IDEA.
2. Selecione `File > Open` e escolha a pasta deste projeto.
3. Aguarde o Maven baixar as dependencias.
4. Configure a variavel de ambiente `MONGODB_URI` com a string de conexao do MongoDB Atlas.
5. Execute a classe `br.com.estetica.automotiva.EsteticaAutomotivaApplication`.
6. Acesse `http://localhost:8080`.

## Como executar pelo terminal

```bash
mvn spring-boot:run
```

Com MongoDB Atlas:

```bash
set MONGODB_URI=mongodb+srv://USUARIO:SENHA@CLUSTER.mongodb.net/esteticas_automotivas
mvn spring-boot:run
```

No PowerShell:

```powershell
$env:MONGODB_URI="mongodb+srv://USUARIO:SENHA@CLUSTER.mongodb.net/esteticas_automotivas"
mvn spring-boot:run
```

## Acesso inicial

O sistema cria automaticamente um gestor no primeiro start:

- E-mail: `gestor@estetica.com`
- Senha: `admin123`

Em producao, altere esses dados:

```powershell
$env:ADMIN_EMAIL="gestor@suaempresa.com"
$env:ADMIN_PASSWORD="uma-senha-forte"
```

## Testes

```bash
mvn test
```

## MongoDB Atlas

Crie um cluster no MongoDB Atlas, libere o IP de acesso, crie um usuario de banco e copie a connection string para a variavel `MONGODB_URI`. As colecoes usadas sao:

- `usuarios`
- `clientes`
- `tipos_servico`
- `agendamentos`

## LGPD e seguranca

O sistema registra consentimento no cadastro, usa hash BCrypt para senhas e evita exposicao integral de CPF e telefone nas listas administrativas. As credenciais do MongoDB Atlas devem ficar fora do codigo-fonte, em variaveis de ambiente.

## Diagramas UML

Os diagramas PlantUML estao em `docs/uml`:

- `diagrama-classes.puml`
- `diagrama-sequencia-agendamento.puml`

## Publicacao no GitHub

Crie um repositorio no GitHub e execute:

```bash
git init
git add .
git commit -m "Projeto estetica automotiva"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/estetica-automotiva.git
git push -u origin main
```
