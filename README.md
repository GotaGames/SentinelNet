# Sentinel Net

Sistema de Gestão e Monitorização de Servidores baseado no conceito Zero Trust, desenvolvido para a disciplina de Base de Dados.

## 1. Descrição do Problema

As redes corporativas tradicionais confiam excessivamente no perímetro externo e negligenciam a segurança interna. Quando um invasor compromete uma máquina interna de menor relevância, como um servidor de testes ou o terminal de um fornecedor, passa a circular na rede como utilizador legítimo. A partir daí, move-se lateralmente, escala privilégios e ataca os alvos de alto valor, como bases de dados centrais ou servidores de produção com propriedade intelectual.

O caso da Rockstar Games é um exemplo real desta dinâmica: o acesso inicial foi secundário, mas após movimentação lateral houve roubo massivo de dados, incluindo código-fonte do GTA VI.

As equipas de resposta a incidentes enfrentam dois obstáculos principais. Primeiro, a fadiga e o tempo de reação, porque humanos analisam logs em minutos ou horas enquanto scripts automáticos atacam em milissegundos. Segundo, a cegueira forense, porque quando a invasão é detetada os logs estão dispersos por milhares de sistemas, sem encadeamento, tornando quase impossível identificar o Paciente Zero.

## 2. Descrição da Solução

O Sentinel Net é uma solução de software que atua como motor autónomo de monitorização, mitigação de ameaças e auditoria forense para redes de servidores corporativos, baseada em Zero Trust. O sistema assenta em três pilares:

- Monitorização e correlação em tempo real de todas as operações entre servidores e clientes.
- Isolamento atómico autónomo, com bloqueio de comunicação e alteração de estado do servidor em milissegundos.
- Encadeamento forense imutável, em que cada log aponta para o log anterior da mesma sessão, permitindo reconstruir o caminho completo do ataque até à origem.

## 3. Arquitetura

O sistema está organizado em três camadas:

- Camada de dados: base de dados MySQL designada `sentinel_net`, com oito tabelas relacionais, restrições de integridade e triggers de auditoria.
- Camada de serviço: classes Java responsáveis pelo motor de risco, pela lógica de processamento e pelo acesso à base de dados através do padrão DAO.
- Camada de apresentação: três interfaces Java Swing, correspondentes ao Cliente, ao Processamento e ao Painel do Administrador.

A aplicação liga-se ao MySQL local do XAMPP através do driver JDBC MySQL Connector/J.

## 4. Modelo de Dados

### 4.1 Tabelas

- `vlan`: identifica as VLANs normais e a VLAN de quarentena.
- `servidor`: registra os servidores, o seu IP interno, o nível de criticidade e o estado atual.
- `cliente`: registra os utilizadores e serviços que originam requisições.
- `conexao_permitida`: define a whitelist de comunicação entre clientes ou servidores de origem e servidores de destino.
- `requisicao`: registra cada pedido intercetado, com IP de origem, IP externo, ação solicitada e estado.
- `log_operacao`: registra cada evento processado, com nível de risco e referência ao log pai.
- `administrador`: registra os técnicos do SOC.
- `alerta_enviado`: registra as notificações geradas, com meio de envio, tipo e motivo detalhado.

### 4.2 Relações principais

- `vlan` para `servidor` e `vlan` para `cliente`: 1:N.
- `cliente` e `servidor` para `requisicao`: 1:N, com origem exclusiva.
- `servidor` para `log_operacao`: 1:N.
- `requisicao` para `log_operacao`: 1:1.
- `log_operacao` para `log_operacao`: 1:1 autoreferenciado, através de `id_log_pai`.
- `log_operacao` para `alerta_enviado`: 1:N.
- `administrador` para `alerta_enviado`: 1:N.

## 5. Requisitos

- XAMPP com MySQL ativo (porta 3306).
- Java JDK 17 ou superior.
- IntelliJ IDEA (Community ou Ultimate).
- DBeaver para gestão da base de dados.
- MySQL Connector/J (driver JDBC).
- Git para clonar o repositório.

Tens razão, ficou genérico. Vou dar-te a secção completa com o procedimento real de clonagem do GitHub, incluindo os comandos, a configuração do IntelliJ, e a resolução de problemas comuns.

## 6. Clonar o Projeto

### 6.1 Pré-requisitos para clonar

Antes de começar, confirma que tens instalado:

- Git. Para verificar, abre o terminal e executa `git --version`. Se não aparecer a versão, instala o Git a partir de https://git-scm.com/downloads.
- IntelliJ IDEA.
- Java JDK 17 ou superior. Para verificar, executa `java -version`.
- XAMPP, com o MySQL a correr.
- DBeaver.

### 6.2 Obter o URL do repositório

1. Abre o repositório do projeto no GitHub.
2. Clica no botão verde **Code**.
3. No separador **HTTPS**, copia o URL: `https://github.com/GotaGames/SentinelNet.git`.


### 6.3 Clonar via terminal

Abre o terminal na pasta onde queres guardar o projeto e executa:

```bash
git clone https://github.com/GotaGames/SentinelNet.git sentinel-net
```


O terminal vai mostrar algo como:

```
Cloning into 'sentinel-net'...
remote: Enumerating objects: 42, done.
remote: Counting objects: 100% (42/42), done.
remote: Compressing objects: 100% (30/30), done.
remote: Total 42 (delta 10), reused 42 (delta 10), pack-reused 0
Receiving objects: 100% (42/42), 15.23 KiB | 1.20 MiB/s, done.
Resolving deltas: 100% (10/10), done.
```


### 6.4 Clonar diretamente pelo IntelliJ

Se preferires não usar o terminal, o IntelliJ tem a clonagem integrada.

1. Abre o IntelliJ IDEA.
2. Na janela de boas-vindas, clica em **Get from VCS**. Se já tiveres um projeto aberto, vai a **File**, **New**, **Project from Version Control**.
3. No campo **URL**, cola o URL do repositório.
4. No campo **Directory**, escolhe a pasta onde queres guardar o projeto.
5. Clica em **Clone**.
6. O IntelliJ vai descarregar o projeto, configurar o SDK e abrir a estrutura de pastas.
7. Se o IntelliJ pedir para importar um `pom.xml` ou um módulo, aceita.


### 6.5 Após a clonagem

Depois de clonares o projeto, faz o seguinte no IntelliJ.

1. Confirma que o JDK está configurado. Vai a **File**, **Project Structure**, **Project**, e verifica se o SDK é o JDK 17 ou superior.
2. Adiciona o driver MySQL Connector/J às bibliotecas, seguindo a secção 7 deste documento.
3. Confirma que o ficheiro `ConexaoPool.java` tem os dados corretos de ligação, seguindo a secção 10.
4. Cria a base de dados e as tabelas, seguindo a secção 9.
5. Executa o `Main.java`, seguindo a secção 11.

### 6.6 Atualizar o projeto depois da clonagem

Se o repositório for atualizado por outro membro do grupo, podes trazer as alterações para a tua cópia local.

```bash
cd sentinel-net
git pull origin main
```

Se houver conflitos, o Git vai avisar. Nesse caso, abre os ficheiros em conflito, resolve manualmente as diferenças, e depois executa:

```bash
git add .
git commit -m "Resolucao de conflitos"
git push origin main
```

### 6.7 Problemas comuns na clonagem

**Erro `Permission denied (publickey)`:** estás a usar o URL SSH sem teres a chave configurada no GitHub. Usa o URL HTTPS ou configura a chave SSH.

**Erro `Repository not found`:** o URL está errado, confirma o URL.

**Erro `SSL certificate problem`:** o Git não confia no certificado do servidor. Podes desativar temporariamente a verificação com `git config --global http.sslVerify false`, mas não é recomendado em ambiente de produção.


**IntelliJ não reconhece o projeto:** se a clonagem criou uma pasta sem ficheiros de configuração do IntelliJ, abre o IntelliJ, vai a **File**, **Open**, e seleciona a pasta. O IntelliJ vai detetar a estrutura e configurar o projeto automaticamente.

## 7. Configurar o Driver JDBC

### 7.1 Descarregar o Connector/J

1. Acede a https://dev.mysql.com/downloads/connector/j/.
2. Em Select Operating System, escolhe Platform Independent.
3. Descarrega o ZIP Archive.
4. Extrai o ficheiro `mysql-connector-j-<versao>.jar`.

### 7.2 Adicionar ao IntelliJ

1. No IntelliJ, vai a File, Project Structure.
2. No painel esquerdo, seleciona Libraries.
3. Clica em + e escolhe Java.
4. Navega até ao ficheiro `.jar` extraído, seleciona-o e clica em OK.
5. Seleciona o módulo do projeto e clica em OK.
6. Clica em Apply e depois em OK.

Alternativa com Maven: acrescenta ao `pom.xml`:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.3.0</version>
</dependency>
```

## 8. Estrutura do Projeto Java

```
src/
  main/
    java/
      dao/
        ConexaoPool.java
        ServidorDAO.java
        ClienteDAO.java
        RequisicaoDAO.java
        LogDAO.java
        AlertaDAO.java
      model/
        Servidor.java
        Requisicao.java
        LogOperacao.java
      service/
        MotorRisco.java
        ServicoProcessamento.java
      view/
        JanelaCliente.java
        JanelaProcessamento.java
        JanelaAdmin.java
      Main.java
    resources/
      mysql-connector-j-8.x.x.jar
```

## 9. Criar a Base de Dados

### 9.1 Iniciar o MySQL

1. Abre o painel de controlo do XAMPP.
2. Clica em Start no módulo MySQL. A porta padrão é 3306.
3. Confirma que o estado está Running.

### 9.2 Ligar o DBeaver ao MySQL

1. Abre o DBeaver.
2. Clica em Nova Conexão, escolhe MySQL e clica em Next.
3. Preenche Host `localhost`, Porta `3306`, Username `root` e Password vazia (ou a que definiste no XAMPP).
4. Clica em Test Connection. Se der sucesso, clica em Finish.

### 9.3 Criar a base de dados e as tabelas

No DBeaver, abre um SQL Editor na ligação MySQL e executa:

```sql
CREATE DATABASE sentinel_net
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sentinel_net;

CREATE TABLE vlan (
    id_vlan INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    tipo ENUM('Normal', 'Quarentena') NOT NULL DEFAULT 'Normal'
) ENGINE=InnoDB;

CREATE TABLE administrador (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefone VARCHAR(20)
) ENGINE=InnoDB;

CREATE TABLE servidor (
    id_servidor INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    ip_interno VARCHAR(45) NOT NULL UNIQUE,
    nivel_criticidade INT NOT NULL CHECK (nivel_criticidade BETWEEN 1 AND 5),
    status_atual ENUM('Ativo', 'Sob Suspeita', 'Isolado') NOT NULL DEFAULT 'Ativo',
    id_vlan INT NOT NULL,
    CONSTRAINT fk_servidor_vlan FOREIGN KEY (id_vlan)
        REFERENCES vlan(id_vlan) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo ENUM('Funcionario', 'Fornecedor', 'Cliente', 'Servico') NOT NULL,
    ip_interno VARCHAR(45) NOT NULL UNIQUE,
    id_vlan INT NOT NULL,
    CONSTRAINT fk_cliente_vlan FOREIGN KEY (id_vlan)
        REFERENCES vlan(id_vlan) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE conexao_permitida (
    id_conexao INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NULL,
    id_servidor_origem INT NULL,
    id_servidor_destino INT NOT NULL,
    CONSTRAINT fk_conexao_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente) ON DELETE RESTRICT,
    CONSTRAINT fk_conexao_serv_origem FOREIGN KEY (id_servidor_origem)
        REFERENCES servidor(id_servidor) ON DELETE RESTRICT,
    CONSTRAINT fk_conexao_serv_destino FOREIGN KEY (id_servidor_destino)
        REFERENCES servidor(id_servidor) ON DELETE RESTRICT,
    CONSTRAINT chk_conexao_origem_exclusiva CHECK (
        (id_cliente IS NOT NULL AND id_servidor_origem IS NULL) OR
        (id_cliente IS NULL AND id_servidor_origem IS NOT NULL)
    )
) ENGINE=InnoDB;

CREATE TABLE requisicao (
    id_requisicao INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NULL,
    id_servidor_origem INT NULL,
    id_servidor_destino INT NOT NULL,
    ip_origem_requisicao VARCHAR(45) NOT NULL,
    ip_emissao_externo VARCHAR(45),
    acao_solicitada VARCHAR(100) NOT NULL,
    data_hora_requisicao DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    estado_requisicao ENUM('Pendente', 'Permitida', 'Bloqueada', 'Isolada') NOT NULL DEFAULT 'Pendente',
    CONSTRAINT fk_requisicao_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente) ON DELETE RESTRICT,
    CONSTRAINT fk_requisicao_serv_origem FOREIGN KEY (id_servidor_origem)
        REFERENCES servidor(id_servidor) ON DELETE RESTRICT,
    CONSTRAINT fk_requisicao_serv_destino FOREIGN KEY (id_servidor_destino)
        REFERENCES servidor(id_servidor) ON DELETE RESTRICT,
    CONSTRAINT chk_requisicao_origem_exclusiva CHECK (
        (id_cliente IS NOT NULL AND id_servidor_origem IS NULL) OR
        (id_cliente IS NULL AND id_servidor_origem IS NOT NULL)
    )
) ENGINE=InnoDB;

CREATE TABLE log_operacao (
    id_log INT AUTO_INCREMENT PRIMARY KEY,
    id_servidor INT NOT NULL,
    id_requisicao INT NULL,
    acao_executada VARCHAR(100) NOT NULL,
    data_hora_evento DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    nivel_risco INT NOT NULL,
    id_log_pai INT NULL,
    CONSTRAINT fk_log_servidor FOREIGN KEY (id_servidor)
        REFERENCES servidor(id_servidor) ON DELETE RESTRICT,
    CONSTRAINT fk_log_requisicao FOREIGN KEY (id_requisicao)
        REFERENCES requisicao(id_requisicao) ON DELETE RESTRICT,
    CONSTRAINT fk_log_pai FOREIGN KEY (id_log_pai)
        REFERENCES log_operacao(id_log) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE alerta_enviado (
    id_alerta INT AUTO_INCREMENT PRIMARY KEY,
    id_log INT NOT NULL,
    id_admin INT NOT NULL,
    meio_envio ENUM('SMS', 'Email', 'Painel_Web') NOT NULL,
    tipo_alerta ENUM('Suspeita', 'Critico', 'Isolamento') NOT NULL,
    motivo TEXT NULL,
    data_hora_alerta DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_alerta_log FOREIGN KEY (id_log)
        REFERENCES log_operacao(id_log) ON DELETE RESTRICT,
    CONSTRAINT fk_alerta_admin FOREIGN KEY (id_admin)
        REFERENCES administrador(id_admin) ON DELETE RESTRICT
) ENGINE=InnoDB;
```

### 9.4 Inserir os dados iniciais

```sql
INSERT INTO vlan (nome, tipo) VALUES
('VLAN-Testes', 'Normal'),
('VLAN-Producao', 'Normal'),
('VLAN-BaseDados', 'Normal'),
('VLAN-Fornecedores', 'Normal'),
('VLAN-Quarentena', 'Quarentena');

INSERT INTO administrador (nome, email, telefone) VALUES
('Ana Silva', 'ana.silva@soc.empresa.pt', '+351912345678'),
('Bruno Costa', 'bruno.costa@soc.empresa.pt', '+351913456789');

INSERT INTO servidor (nome, ip_interno, nivel_criticidade, status_atual, id_vlan) VALUES
('Servidor-Testes-Dev', '10.0.10.5', 1, 'Ativo', 1),
('Servidor-Testes-QA', '10.0.10.6', 1, 'Ativo', 1),
('Servidor-App-Producao', '10.0.20.10', 5, 'Ativo', 2),
('BD-Producao-Central', '10.0.30.10', 5, 'Ativo', 3),
('Servidor-Ficheiros', '10.0.20.15', 3, 'Ativo', 2),
('Terminal-Fornecedor-01', '10.0.40.20', 1, 'Ativo', 4);

INSERT INTO cliente (nome, tipo, ip_interno, id_vlan) VALUES
('Joao Dev', 'Funcionario', '10.0.10.100', 1),
('Maria Design', 'Funcionario', '10.0.10.101', 1),
('Fornecedor ACME', 'Fornecedor', '10.0.40.50', 4),
('Cliente Externo API', 'Servico', '10.0.20.200', 2);

INSERT INTO conexao_permitida (id_cliente, id_servidor_origem, id_servidor_destino) VALUES
(NULL, 3, 4),
(NULL, 5, 4),
(1, NULL, 3),
(2, NULL, 3),
(4, NULL, 3);
```

### 9.5 Criar os triggers de auditoria

```sql
DELIMITER //

CREATE TRIGGER trg_log_operacao_after_insert
AFTER INSERT ON log_operacao
FOR EACH ROW
BEGIN
    IF NEW.nivel_risco >= 8 THEN
        INSERT INTO alerta_enviado (id_log, id_admin, meio_envio, tipo_alerta, motivo)
        VALUES (NEW.id_log, 1, 'Painel_Web', 'Critico',
            CONCAT('Log de risco ', NEW.nivel_risco, ' gerado automaticamente pelo sistema.'));
    END IF;
END //

CREATE TRIGGER trg_servidor_status_change
AFTER UPDATE ON servidor
FOR EACH ROW
BEGIN
    IF OLD.status_atual <> NEW.status_atual THEN
        INSERT INTO log_operacao (id_servidor, id_requisicao, acao_executada, nivel_risco, id_log_pai)
        VALUES (NEW.id_servidor, NULL,
            CONCAT('AlteracaoEstado:', OLD.status_atual, '->', NEW.status_atual),
            5, NULL);
    END IF;
END //

DELIMITER ;
```

### 9.6 Verificar a criação

```sql
SHOW TABLES;
SELECT COUNT(*) FROM servidor;
SELECT COUNT(*) FROM cliente;
SELECT COUNT(*) FROM administrador;
```

O resultado deve ser oito tabelas, seis servidores, quatro clientes e dois administradores.

## 10. Configurar a Ligação no Código Java

O ficheiro `dao/ConexaoPool.java` contém a configuração da ligação. Confirma que os valores correspondem ao teu ambiente:

```java
private static final String URL = "jdbc:mysql://localhost:3306/sentinel_net?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "";
```

Se o teu MySQL tiver password, altera o campo `PASSWORD`. Se a porta não for 3306, altera o URL.

## 11. Executar o Projeto

1. Confirma que o MySQL do XAMPP está em Running.
2. No IntelliJ, abre o ficheiro `Main.java`.
3. Clica com o botão direito no editor e escolhe Run Main.main(), ou pressiona Shift+F10.
4. As três janelas abrem: Terminal de Cliente, Motor de Processamento e Painel do Administrador.

## 12. Funcionamento

### 12.1 Fluxo de processamento

1. O cliente envia uma requisição, que é registada na tabela `requisicao` com estado Pendente.
2. O motor de processamento deteta a requisição pendente.
3. O motor de risco calcula o nível de risco com base no perfil do cliente, na criticidade do servidor de destino, na ação solicitada, no horário e na whitelist.
4. Se o risco for inferior a 5, a requisição é permitida.
5. Se o risco for igual ou superior a 5, a requisição é bloqueada e é contabilizada como tentativa não permitida.
6. Ao atingir 5 tentativas consecutivas em menos de 2 minutos, o servidor é marcado como Sob Suspeita ou Isolado, conforme o risco.
7. Cada evento gera um log encadeado ao log anterior da mesma sessão através de `id_log_pai`.
8. Cada log de risco elevado gera alertas redundantes, com motivo detalhado, gravados na tabela `alerta_enviado`.

### 12.2 Regras por perfil de cliente

- Funcionário Dev: acesso total a qualquer servidor, incluindo servidores isolados.
- Funcionário Design: acesso permitido a servidores de testes, ficheiros e base de dados; alteração de privilégios é sempre suspeita.
- Fornecedor: acesso apenas ao servidor de fornecedores; download massivo e alteração de privilégios são sempre bloqueados.
- Serviço ou Cliente: acesso apenas a servidores de produção, com login e execução de scripts permitidos; outras ações são suspeitas.

### 12.3 Isolamento

O isolamento é registado na base de dados através da alteração do estado do servidor para Isolado. A execução técnica real ocorre em duas camadas: no kernel do servidor, através de regras de firewall aplicadas pelo agente, e na rede, através da movimentação da porta do switch para a VLAN de quarentena com 802.1X e RADIUS. Um servidor isolado apenas aceita requisições do perfil Dev.

## 13. Consultas de Auditoria

### 13.1 Reconstruir a cadeia forense

```sql
WITH RECURSIVE cadeia AS (
    SELECT id_log, id_servidor, acao_executada, nivel_risco, id_log_pai, 1 AS profundidade
    FROM log_operacao
    WHERE id_log = ?
    UNION ALL
    SELECT l.id_log, l.id_servidor, l.acao_executada, l.nivel_risco, l.id_log_pai, c.profundidade + 1
    FROM log_operacao l
    JOIN cadeia c ON l.id_log = c.id_log_pai
)
SELECT * FROM cadeia ORDER BY profundidade DESC;
```

### 13.2 Listar servidores isolados

```sql
SELECT id_servidor, nome, ip_interno, nivel_criticidade
FROM servidor
WHERE status_atual = 'Isolado';
```

### 13.3 Listar alertas críticos com detalhe

```sql
SELECT a.id_alerta, a.tipo_alerta, a.meio_envio, a.motivo,
       l.acao_executada, l.nivel_risco, ad.nome AS administrador
FROM alerta_enviado a
JOIN log_operacao l ON a.id_log = l.id_log
JOIN administrador ad ON a.id_admin = ad.id_admin
WHERE a.tipo_alerta = 'Critico'
ORDER BY a.id_alerta DESC;
```

### 13.4 Contar tentativas não permitidas por cliente e servidor

```sql
SELECT id_cliente, id_servidor_destino, COUNT(*) AS tentativas
FROM requisicao
WHERE estado_requisicao = 'Bloqueada'
  AND data_hora_requisicao >= NOW() - INTERVAL 2 MINUTE
GROUP BY id_cliente, id_servidor_destino
HAVING tentativas >= 5;
```

## 14. Testes

1. Teste de ligação: executar o `Main` e confirmar que as três janelas abrem e mostram dados.
2. Teste de requisição permitida: enviar um Login do Joao Dev para o Servidor-App-Producao e confirmar estado Permitida.
3. Teste de requisição bloqueada: enviar um DownloadMassivo do Fornecedor ACME para o BD-Producao-Central e confirmar estado Bloqueada.
4. Teste de tolerância: enviar 5 requisições não permitidas do mesmo cliente para o mesmo servidor e confirmar que só na quinta o servidor é isolado.
5. Teste de exceção: com um servidor isolado, enviar uma requisição do Joao Dev e confirmar que é Permitida, e do Fornecedor ACME e confirmar que é Bloqueada.
6. Teste de encadeamento forense: fazer duplo clique num log e confirmar que a aba Cadeia Forense reconstrói a sequência até ao Paciente Zero.
7. Teste de alerta: fazer duplo clique num alerta e confirmar que o detalhe inclui ação, cliente, IPs, servidor, hora e tentativas.
8. Teste de integridade: tentar inserir uma requisição com cliente e servidor de origem simultaneamente e confirmar que a base de dados rejeita.

## 15. Resolução de Problemas

### 15.1 Erro de driver não encontrado

Se aparecer `ClassNotFoundException: com.mysql.cj.jdbc.Driver`, o Connector/J não está nas bibliotecas do projeto. Repete a secção 7.

### 15.2 Erro de ligação recusada

Se aparecer `Connection refused`, o MySQL do XAMPP não está a correr ou a porta está errada. Abre o painel do XAMPP e confirma que o MySQL está em Running. Confirma também a porta no URL do `ConexaoPool`.

### 15.3 Erro de acesso negado

Se aparecer `Access denied for user 'root'`, a password no `ConexaoPool` não corresponde à do MySQL. Confirma a password no XAMPP e atualiza o ficheiro.

### 15.4 Tabelas não aparecem no DBeaver

Se depois de executar o script as tabelas não aparecerem, clica com o botão direito na base `sentinel_net` e escolhe Refresh. Se continuarem a não aparecer, verifica se o script foi executado sem erros, lendo a aba de output do DBeaver.

### 15.5 Alertas não aparecem na interface

Se os alertas estiverem na tabela `alerta_enviado` mas não aparecerem na aba de Alertas, confirma que o `AlertaDAO.listarTodos()` está a usar `LEFT JOIN` com `COALESCE` e que a `JanelaAdmin` chama esse método dentro do `atualizar()`.

## 16. Notas Finais

O modelo de dados foi aprovado pelo docente e não deve ser alterado sem autorização. As melhorias implementadas vivem por cima do modelo, na camada de serviço e na camada de apresentação, sem modificar a estrutura relacional aprovada, exceto pela adição do campo `motivo` na tabela `alerta_enviado`, que foi acordada como extensão necessária para o detalhe dos alertas.


## 17. Autores

Projeto desenvolvido no âmbito da disciplina de Base de Dados 1.

- **Nomes:** Gota, Denzel; Vilanculos, Widson; Cossa, Alberto; Mithe, Cleyton; Valoi, Kishua; Lepeque, Octavio.
- **Instituição:** Universidade Eduardo Mondlane, Departamento de Eletrotecnia, Licenciatura em Engenharia Informatica - Pos-laboral, 2 Ano.
- **Ano:** 2026

---

## Licença

Projeto académico de uso educacional.

