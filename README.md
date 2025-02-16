# Contabilidade Axies

Esta ferramenta foi desenvolvida para auxiliar usuários que possuem axies alugados na plataforma **LootRush** e desejam realizar a contagem manual dos aluguéis gerados. A aplicação processa os dados colados pelo usuário, extrai o número de axies locados e realiza os seguintes cálculos:

- **Soma dos Valores de Aluguel (USD):** Soma os valores informados.
- **Desconto de 10%:** Aplica desconto de 10% (taxa da plataforma) sobre o valor total.
- **Conversão para BRL:** Converte o valor final de USD para BRL utilizando a API [Frankfurter](https://www.frankfurter.app/) (com cache de 4 horas).
- **Projeções:** Exibe projeções de lucro semanal (valor diário × 7) e mensal (valor diário × 30) em USD e BRL.
- **Estatísticas:** Calcula a média de lucro por axie.

## Funcionalidades

- **Contagem de Axies:** Processa os dados e extrai a quantidade de axies locados.
- **Cálculo de Lucro:** Soma os valores em USD e aplica o desconto de 10%.
- **Conversão de Moeda:** Converte o valor descontado de USD para BRL.
- **Projeções de Lucro:** Exibe projeções semanais e mensais.
- **Estatísticas Básicas:** Calcula a média de lucro por axie.

## Estrutura do Projeto


## Como Usar

1. **Executar a Aplicação:**
   - Clique duas vezes no arquivo `executarAxies.bat`.
   - Uma janela de terminal será aberta.
   - Cole os dados de aluguel no terminal. Cada bloco de dados deve ser finalizado com a palavra `FIM` em uma nova linha.
   - Ao finalizar, o programa exibirá:
     - O total de axies locados.
     - A soma total dos valores (em USD) antes e depois do desconto.
     - A conversão do valor final para BRL.
     - Projeções de lucro semanal e mensal.
     - A média de lucro por axie.

2. **Requisitos:**
   - [Java Runtime Environment (JRE) 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) ou superior.
   - Se desejar compilar a partir do código-fonte, [Apache Maven](https://maven.apache.org/).

## Build (Opcional)

Para compilar o projeto a partir do código-fonte, abra o terminal na pasta do projeto e execute:

```bash
mvn clean package
