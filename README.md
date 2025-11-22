# Mercado FiadoPay - Cliente POOA

Aplicação cliente (Console/CLI) desenvolvida para a disciplina de Programação Orientada a Objetos Avançada (POOA). O sistema simula um Ponto de Venda (PDV) que consome a API `FiadoPay`, utilizando recursos avançados do Java 21+.

## 👥 Equipe
* **Aluno 1:** [Carlos Eduardo Martins Fortunato]
* **Aluno 2:** [Caique Ramos da Silva]
* **Aluno 3:** [Felipe Fornazeli Rocha]
* **Aluno 4:** [Marcos Yago Rocha Vieira]
* **Aluno 5:** [Matheus Reis Machado]
* **Aluno 6:** [Vitor Joaquin Caldeiras Santos]

---

## Tecnologias Utilizadas
* **Java 21** (LTS)
* **Maven** (Gerenciamento de dependências e Build)
* **H2 Database** (Persistência local para reconciliação) 
* **Java HTTP Client** (Comunicação nativa com API REST)
* **Jackson** (Processamento JSON)

---

## Decisões de Design e Arquitetura

O projeto foi estruturado para atender aos requisitos de baixo acoplamento e alta coesão, utilizando padrões como **Strategy** (para os pagamentos) e **Singleton/Service** (para autenticação).

### 1. Anotações e Reflexão (Plugins)
Para garantir a extensibilidade do sistema sem modificar o código principal, implementamos um carregador dinâmico de plugins:
* **`@PaymentMethod(value)`**: Anotação de metadado que identifica uma classe como estratégia de pagamento (ex: `"PIX"`, `"CARD"`).
* **`@AntiFraud(limit)`**: Define regras de negócio (limite de valor) diretamente na classe do plugin.
* **`PluginLoader`**: Classe que escaneia o classpath, identifica classes anotadas e as instancia em tempo de execução usando Java Reflection API.

### 2. Threads e Concorrência
Implementamos o processamento assíncrono para garantir que a interface do usuário não trave durante operações de rede ou manutenção de sessão.
* **`AuthService`**: Utiliza `ScheduledExecutorService` para renovar o Token de Acesso automaticamente em background a cada 4 minutos.
* Isso permite que o token esteja sempre válido quando o usuário decide realizar uma compra, sem pausa perceptível.

### 3. Persistência e Reconciliação 
Para atender ao requisito de reconciliação de vendas, todas as transações (aprovadas ou não) são persistidas em um banco de dados local **H2** (`jdbc:h2:./mercado_db`). Utilizamos JDBC puro para demonstrar o controle transacional e manipulação SQL.

---

## Como Rodar o Projeto

### Pré-requisitos
1. Certifique-se de que o servidor **FiadoPay** (JAR do professor) está rodando na porta `8080`.
   ``` java -jar fiadopay-sim.jar

### Executar Via Maven
 * | mvn clean compile exec:java -Dexec.mainClass="com.mercado.Main"

### Cenários de Teste:
Pagamento com Sucesso (Integração API)

No menu, digite: CARD
Valor: 100
Resultado Esperado: O sistema envia a requisição para a API (Status 200 OK) e salva o registro no banco local.

Teste de Antifraude (Reflexão)
No menu, digite: PIX 
Valor: 600
Resultado Esperado: O sistema bloqueia a operação localmente antes de chamar a API, pois a classe PixPayment possui a anotação @AntiFraud(limit=500).

Automação de Token (Threads)
Observe o console por alguns minutos.
Resultado Esperado: Logs como Renovando token... aparecerão periodicamente, indicando que a thread de background está ativa.

Reconciliação (Banco de Dados)
No menu, digite: RELATORIO
Resultado Esperado: Lista todas as operações realizadas que foram salvas no H2.