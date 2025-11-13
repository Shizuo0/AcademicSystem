# 🎓 Sistema Acadêmico - UNIFOR

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)

**Sistema de Gestão Acadêmica com Integração de Microsserviços**

[Funcionalidades](#-funcionalidades) • [Arquitetura](#-arquitetura) • [Instalação](#-instalação) • [Como Usar](#-como-usar) • [Documentação](#-documentação-técnica)

</div>

---

## 📋 Sobre o Projeto

O **Sistema Acadêmico UNIFOR** é uma aplicação Java desktop que simula operações de gestão acadêmica, incluindo:

- ✅ **Matrícula de discentes** em disciplinas
- 📚 **Reserva de livros** da biblioteca
- 🔄 **Integração com microsserviços externos** para consulta de dados
- 💾 **Persistência local** em banco de dados MySQL
- 🎯 **Validação de regras de negócio** (limite de matrículas, compatibilidade de cursos, etc.)

O sistema foi projetado seguindo princípios de **arquitetura limpa**, com separação clara de responsabilidades em camadas (Controller, Service, Repository, View).

---

## ✨ Funcionalidades

### 🔍 Consultas
- **Consultar Discente**: Visualizar informações detalhadas dos alunos (nome, curso, modalidade, status acadêmico)
- **Consultar Disciplinas por Curso**: Listar disciplinas disponíveis filtradas por curso
- **Consultar Livros**: Verificar disponibilidade de livros na biblioteca

### 📝 Matrículas
- **Matricular em Disciplina**: Realizar matrícula com validação automática de:
  - Status acadêmico do discente (apenas ATIVO)
  - Compatibilidade curso-disciplina
  - Limite de 5 matrículas por discente
  - Disponibilidade de vagas
- **Cancelar Matrícula**: Remover matrícula usando código único
- **Minhas Matrículas**: Visualizar histórico completo de matrículas

### 📚 Biblioteca
- **Reservar Livro**: Fazer reserva de livros disponíveis
- **Cancelar Reserva**: Remover reserva de livro
- **Minhas Reservas**: Listar todos os livros reservados

---

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas bem definida:

```
┌─────────────────────────────────────────────────┐
│                   VIEW LAYER                    │
│          (ConsoleView - Interface CLI)          │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│                CONTROLLER LAYER                 │
│   (Gerenciamento de fluxo e validações)         │
│   - DiscenteController                          │
│   - DisciplinaController                        │
│   - BibliotecaController                        │
│   - MatriculaController                         │
│   - ReservaController                           │
└────────────────────┬────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────┐
│                 SERVICE LAYER                   │
│        (Lógica de negócio e orquestração)       │
│   - FacadeService (cache de microsserviços)     │
│   - GestaoAcademicaService (operações)          │
│   - DisponibilidadeService (validações)         │
│   - DiscenteService, DisciplinaService, etc     │
└─────────────┬──────────────────────┬────────────┘
              │                      │
    ┌─────────▼──────────┐   ┌──────▼──────────┐
    │ REPOSITORY LAYER   │   │  EXTERNAL APIs  │
    │  (Persistência)    │   │ (Microsserviços)│
    │  - MySQL Database  │   │  - HTTP Clients │
    └────────────────────┘   └─────────────────┘
```

### 📁 Estrutura de Diretórios

```
AcademicSystem/
│
├── src/
│   ├── Main.java                      # Ponto de entrada da aplicação
│   │
│   ├── controller/                    # Camada de controle
│   │   ├── ControllerFactory.java     # Factory pattern para controllers
│   │   ├── DiscenteController.java    # Controle de discentes
│   │   ├── DisciplinaController.java  # Controle de disciplinas
│   │   ├── BibliotecaController.java  # Controle de biblioteca
│   │   ├── MatriculaController.java   # Controle de matrículas
│   │   └── ReservaController.java     # Controle de reservas
│   │
│   ├── service/                       # Camada de serviço (lógica de negócio)
│   │   ├── FacadeService.java         # Facade para microsserviços
│   │   ├── GestaoAcademicaService.java # Gestão de operações acadêmicas
│   │   ├── DisponibilidadeService.java # Validações de disponibilidade
│   │   ├── DiscenteService.java       # Serviço de discentes
│   │   ├── DisciplinaService.java     # Serviço de disciplinas
│   │   └── BibliotecaService.java     # Serviço de biblioteca
│   │
│   ├── repository/                    # Camada de persistência
│   │   ├── IMatriculaRepository.java  # Interface do repositório
│   │   ├── MatriculaRepositoryImpl.java # Implementação MySQL
│   │   ├── IReservaRepository.java
│   │   └── ReservaRepositoryImpl.java
│   │
│   ├── model/                         # Modelos de domínio
│   │   ├── Discente.java
│   │   ├── Disciplina.java
│   │   ├── Livro.java
│   │   ├── Matricula.java
│   │   ├── ReservaLivro.java
│   │   ├── SituacaoAcademica.java     # Enum
│   │   └── StatusDisponibilidade.java # Enum
│   │
│   ├── exception/                     # Exceções customizadas
│   │   ├── DiscenteInativoException.java
│   │   ├── CursoIncompativelException.java
│   │   ├── LimiteMatriculasExcedidoException.java
│   │   ├── SemVagasException.java
│   │   └── LivroIndisponivelException.java
│   │
│   ├── view/                          # Interface do usuário
│   │   └── ConsoleView.java           # Interface CLI interativa
│   │
│   ├── util/                          # Utilitários
│   │   ├── DatabaseConnection.java    # Gerenciamento de conexão MySQL
│   │   ├── HttpClientImpl.java        # Cliente HTTP para APIs
│   │   ├── GsonParser.java            # Parser JSON
│   │   ├── GeradorMatricula.java      # Gerador de códigos únicos
│   │   ├── InputValidator.java        # Validação de entradas
│   │   ├── Logger.java                # Sistema de logs
│   │   └── TableFormatter.java        # Formatação de tabelas CLI
│   │
│   └── sql/
│       └── schema.sql                 # Script de criação do banco
│
├── lib/                               # Dependências externas
│   ├── mysql-connector-j-8.0.33.jar   # Driver MySQL
│   └── gson-2.10.1.jar                # Biblioteca JSON
│
├── bin/                               # Classes compiladas (.class)
│
└── README.md                          # Este arquivo
```

---

## 🛠️ Tecnologias Utilizadas

### **Core**
- **Java 21** - Linguagem de programação
- **MySQL 8.0** - Banco de dados relacional

### **Bibliotecas**
| Biblioteca | Versão | Propósito |
|------------|--------|-----------|
| `mysql-connector-j` | 8.0.33 | Driver JDBC para conexão com MySQL |
| `gson` | 2.10.1 | Serialização/deserialização JSON para APIs |

### **Padrões de Projeto**
- ✅ **Factory Pattern** - `ControllerFactory` para criação de instâncias
- ✅ **Facade Pattern** - `FacadeService` para simplificar acesso aos microsserviços
- ✅ **Repository Pattern** - Abstração da camada de persistência
- ✅ **Dependency Injection** - Injeção manual de dependências via construtores

### **APIs Externas** (Microsserviços)
O sistema integra-se com 3 microsserviços REST via HTTP:
- **API de Discentes** - Dados de alunos
- **API de Disciplinas** - Informações de cursos e disciplinas
- **API de Biblioteca** - Catálogo de livros

---

## 🚀 Instalação

### Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- ☕ **Java JDK 21** ou superior ([Download](https://adoptium.net/))
- 🐬 **MySQL 8.0** ou superior ([Download](https://dev.mysql.com/downloads/mysql/))
- 📝 Um editor de texto ou IDE (recomendado: IntelliJ IDEA, Eclipse, VS Code)

### Passo 1: Clonar o Repositório

```bash
git clone https://github.com/Shizuo0/AcademicSystem.git
cd AcademicSystem
```

### Passo 2: Configurar o Banco de Dados

1. **Iniciar o MySQL Server**:
```bash
# Windows (usando XAMPP, WAMP ou MySQL Workbench)
# Ou via linha de comando:
net start MySQL80
```

2. **Criar o banco de dados**:
```bash
mysql -u root -p < src/sql/schema.sql
```

Ou execute manualmente:
```sql
mysql -u root -p
```
Depois copie e cole o conteúdo de `src/sql/schema.sql`.

3. **Configurar credenciais** (se necessário):

Edite o arquivo `src/util/DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/sistema_academico";
private static final String USER = "root";           // Seu usuário MySQL
private static final String PASSWORD = "12345678";   // Sua senha MySQL
```

### Passo 3: Verificar Dependências

Certifique-se de que os arquivos JAR estão na pasta `lib/`:
- ✅ `mysql-connector-j-8.0.33.jar`
- ✅ `gson-2.10.1.jar`

### Passo 4: Compilar o Projeto

```bash
javac -d bin -cp "lib/*" src/**/*.java src/*.java
```

**Para Windows PowerShell:**
```powershell
javac -d bin -cp "lib/*" $(Get-ChildItem -Recurse -Filter *.java src/ | % FullName)
```

### Passo 5: Executar a Aplicação

```bash
# Linux/Mac
java -cp "bin:lib/*" Main

# Windows (CMD/PowerShell)
java -cp "bin;lib/*" Main
```

---

## 💻 Como Usar

### Interface Principal

Ao iniciar o sistema, você verá o menu principal:

```
┌────────────────────────────────────────────────┐
│               MENU PRINCIPAL                   │
├────────────────────────────────────────────────┤
│           CONSULTAS                            │
│  1. Consultar Discente                         │
│  2. Consultar Curso e Disciplina               │
│  3. Consultar Livro                            │
├────────────────────────────────────────────────┤
│           MATRÍCULAS                           │
│  4. Matricular em Disciplina                   │
│  5. Cancelar Matrícula                         │
│  6. Minhas Matrículas                          │
├────────────────────────────────────────────────┤
│           BIBLIOTECA                           │
│  7. Reservar Livro                             │
│  8. Cancelar Reserva                           │
│  9. Minhas Reservas                            │
├────────────────────────────────────────────────┤
│  0. Sair                                       │
└────────────────────────────────────────────────┘
```

### Exemplos de Uso

#### 📝 Realizar uma Matrícula

1. **Consultar discentes disponíveis** (opção 1)
   - Anote o ID do discente desejado (ex: `3`)

2. **Consultar disciplinas do curso** (opção 2)
   - Selecione o curso
   - Anote o ID da disciplina (ex: `8374`)

3. **Realizar matrícula** (opção 4)
   ```
   ID do Discente: 3
   ID da Disciplina: 8374
   ```

4. O sistema validará:
   - ✅ Status acadêmico (discente ATIVO?)
   - ✅ Compatibilidade curso-disciplina
   - ✅ Limite de matrículas (máximo 5)
   - ✅ Vagas disponíveis

5. Se aprovado, será gerado um **código de matrícula único** (ex: `2520001`)

#### 📚 Reservar um Livro

1. **Consultar livros disponíveis** (opção 3)
   - Anote o ID do livro (ex: `1748`)

2. **Verificar suas matrículas** (opção 6)
   - Anote seu código de matrícula (ex: `2520001`)

3. **Fazer reserva** (opção 7)
   ```
   Código de Matrícula: 2520001
   ID do Livro: 1748
   ```

---

## 📖 Documentação Técnica

### Camada de Controle (Controller)

Os **Controllers** gerenciam o fluxo entre a View e os Services:

#### `ControllerFactory`
```java
public class ControllerFactory {
    public static ControllerFactory criar() {
        // Inicializa todas as dependências
        // Configura conexão com banco de dados
        // Instancia controllers e services
    }
}
```

**Responsabilidades:**
- Criar todas as instâncias necessárias
- Configurar injeção de dependências
- Inicializar caches dos microsserviços

#### `MatriculaController`
```java
public boolean realizarMatricula(String discenteId, String disciplinaId)
public boolean cancelarMatriculaPorCodigo(String codigoMatricula)
public List<Map<String, Object>> consultarMatriculas(String discenteId)
```

---

### Camada de Serviço (Service)

Os **Services** contêm a lógica de negócio:

#### `GestaoAcademicaService`
```java
public boolean simularMatricula(String discenteId, String disciplinaId) {
    // 1. Valida regras de negócio via DisponibilidadeService
    // 2. Gera código único de matrícula
    // 3. Persiste no banco via Repository
    // 4. Retorna sucesso/falha
}
```

**Regras de Negócio Implementadas:**
- ❌ Discente INATIVO não pode se matricular
- ❌ Disciplina de curso incompatível
- ❌ Limite de 5 matrículas por discente
- ❌ Disciplina sem vagas disponíveis

#### `FacadeService`
```java
public void inicializarCaches() {
    // Carrega dados dos 3 microsserviços em paralelo
    // Utiliza CompletableFuture para execução assíncrona
}
```

**Otimização:**
- ⚡ Carregamento paralelo de dados
- 📦 Cache local para reduzir chamadas HTTP
- 🔄 Sincronização automática

---

### Camada de Repositório (Repository)

Os **Repositories** abstraem o acesso ao banco de dados:

#### Interface `IMatriculaRepository`
```java
public interface IMatriculaRepository {
    boolean adicionar(Matricula matricula);
    boolean removerPorCodigo(String codigoMatricula);
    Matricula buscarPorCodigo(String codigoMatricula);
    List<Matricula> listarPorDiscente(String discenteId);
    boolean existeMatricula(String discenteId, String disciplinaId);
}
```

#### Implementação MySQL
```java
public class MatriculaRepositoryImpl implements IMatriculaRepository {
    // Implementação usando JDBC
    // Prepared Statements para segurança
    // Connection pooling via DatabaseConnection
}
```

**Características:**
- ✅ Prepared Statements (previne SQL Injection)
- ✅ Gerenciamento automático de conexões
- ✅ Tratamento de exceções SQL
- ✅ Limpeza automática ao encerrar aplicação

---

### Camada de Modelo (Model)

Os **Models** representam as entidades do domínio:

#### `Matricula`
```java
public class Matricula {
    private String codigoMatricula;  // Formato: AAMMDDNNN (ano+mês+dia+ordem)
    private String discenteId;
    private String disciplinaId;
    private LocalDate dataMatricula;
}
```

#### `ReservaLivro`
```java
public class ReservaLivro {
    private String discenteId;
    private String livroId;
    private LocalDate dataReserva;
}
```

---

### Exceções Customizadas

```java
DiscenteInativoException          // Discente com status != ATIVO
CursoIncompativelException        // Disciplina de outro curso
LimiteMatriculasExcedidoException // Mais de 5 matrículas
SemVagasException                 // Disciplina lotada
LivroIndisponivelException        // Livro já emprestado
```

---

### Utilitários

#### `DatabaseConnection`
```java
public Connection getConnection()          // Obtém conexão MySQL
public void limparTodasTabelas()          // Limpa dados ao encerrar
public static void fecharRecursos(...)    // Fecha ResultSet, Statement, etc.
```

#### `Logger`
```java
Logger.sucesso("Operação realizada!");    // [OK] verde
Logger.erro("Falha na operação!");        // [ERRO] vermelho
Logger.info("Informação geral");           // [INFO] azul
Logger.dica("Sugestão para o usuário");    // [DICA] amarelo
```

#### `TableFormatter`
```java
TableFormatter.imprimirTopoTabela(larguras)
TableFormatter.imprimirLinhaTabela(larguras, valores...)
TableFormatter.imprimirRodapeTabela(larguras)
```

---

## 🗄️ Estrutura do Banco de Dados

### Tabela `matriculas`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | INT (PK, AI) | Identificador único |
| `codigo_matricula` | VARCHAR(20) UNIQUE | Código único da matrícula |
| `discente_id` | VARCHAR(50) | ID do discente (FK lógica) |
| `disciplina_id` | VARCHAR(50) | ID da disciplina (FK lógica) |
| `data_matricula` | DATE | Data da matrícula |
| `created_at` | TIMESTAMP | Data de criação do registro |

**Constraints:**
- ✅ `UNIQUE(discente_id, disciplina_id)` - Previne duplicatas
- ✅ Índices em `codigo_matricula`, `discente_id`, `disciplina_id`

### Tabela `reservas_livros`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | INT (PK, AI) | Identificador único |
| `discente_id` | VARCHAR(50) | ID do discente |
| `livro_id` | VARCHAR(50) | ID do livro |
| `data_reserva` | DATE | Data da reserva |
| `created_at` | TIMESTAMP | Data de criação |

**Constraints:**
- ✅ `UNIQUE(discente_id, livro_id)` - Um livro por discente
- ✅ Índices otimizados para consultas

---

## ⚠️ Comportamento Importante

### Limpeza Automática de Dados

O sistema implementa um **Shutdown Hook** que limpa automaticamente todas as tabelas ao encerrar:

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    controllerFactory.getDatabaseConnection().limparTodasTabelas();
}));
```

**Isso significa:**
- ✅ Dados são persistidos **durante** a execução
- ❌ Dados são **apagados** ao fechar o sistema
- 🎯 Ideal para testes e demonstrações

Para **desabilitar** esse comportamento, remova o shutdown hook em `ConsoleView.java`.

---

## 🐛 Troubleshooting

### Erro: "Driver MySQL não encontrado"

**Solução:**
```bash
# Verifique se o JAR está em lib/
ls lib/mysql-connector-j-8.0.33.jar

# Recompile com o classpath correto
javac -d bin -cp "lib/*" src/**/*.java src/*.java
```

### Erro: "Não foi possível conectar ao MySQL"

**Solução:**
1. Verifique se o MySQL está rodando:
```bash
# Windows
net start MySQL80

# Linux/Mac
sudo systemctl start mysql
```

2. Teste a conexão:
```bash
mysql -u root -p
```

3. Verifique as credenciais em `DatabaseConnection.java`

### Erro: "Tabela não existe"

**Solução:**
```bash
# Execute o script de criação
mysql -u root -p < src/sql/schema.sql
```

### Erro ao compilar: "package does not exist"

**Solução:**
```bash
# Certifique-se de compilar TODOS os arquivos
javac -d bin -cp "lib/*" src/**/*.java src/*.java
```

---

## 🧪 Testes

### Teste Manual - Fluxo Completo

1. **Iniciar sistema**
```bash
java -cp "bin;lib/*" Main
```

2. **Consultar discente** (opção 1)
   - Verificar se os dados são carregados do microsserviço

3. **Matricular discente** (opção 4)
   - Testar com ID válido: deve criar matrícula
   - Testar com discente inativo: deve rejeitar
   - Matricular 5x: deve aceitar
   - Matricular 6ª vez: deve rejeitar

4. **Consultar matrículas** (opção 6)
   - Verificar se os dados foram salvos no MySQL

5. **Cancelar matrícula** (opção 5)
   - Usar código gerado anteriormente

6. **Reservar livro** (opção 7)
   - Verificar validação de disponibilidade

### Consultas SQL para Verificação

```sql
-- Verificar matrículas salvas
SELECT * FROM matriculas ORDER BY created_at DESC;

-- Contar matrículas por discente
SELECT discente_id, COUNT(*) as total 
FROM matriculas 
GROUP BY discente_id;

-- Verificar reservas
SELECT * FROM reservas_livros;
```

---

## 📝 To-Do / Melhorias Futuras

- [ ] Implementar autenticação de usuários
- [ ] Adicionar API REST própria (Spring Boot)
- [ ] Migrar para arquitetura de microsserviços completa
- [ ] Implementar testes unitários (JUnit 5)
- [ ] Adicionar interface gráfica (JavaFX ou Swing)
- [ ] Implementar sistema de notificações
- [ ] Adicionar relatórios em PDF
- [ ] Dockerizar a aplicação
- [ ] Implementar CI/CD com GitHub Actions

---

## 👤 Autor

**Paulo Shizuo Vasconcelos Tatibana**

- GitHub: [@Shizuo0](https://github.com/Shizuo0)
- LinkedIn: [Paulo Shizuo](https://linkedin.com/in/seu-perfil)

---

<div align="center">

**Desenvolvido com ☕ e Java**

</div>
