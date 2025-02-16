# Contabilidade Axies

Esta ferramenta foi desenvolvida para auxiliar usuários que possuem axies alugados na plataforma **LootRush** e desejam realizar a contagem manual dos aluguéis gerados. A aplicação processa os dados inseridos, extrai a quantidade de axies locados e realiza os seguintes cálculos:

- **Soma dos Valores de Aluguel (USD):** Soma os valores informados.
- **Desconto de 10%:** Aplica um desconto de 10% (taxa da plataforma) sobre o valor total.
- **Conversão para BRL:** Converte o valor final de USD para BRL utilizando a API [Frankfurter](https://www.frankfurter.app/) (com cache de 4 horas).
- **Projeções de Lucro:** Calcula projeções de lucro semanal (valor diário × 7) e mensal (valor diário × 30) em USD e BRL.
- **Estatísticas:** Calcula a média de lucro por axie.

## Funcionalidades

- **Contagem de Axies:** Processa os dados e extrai a quantidade de axies locados.
- **Cálculo de Lucro:** Soma os valores em USD e aplica o desconto de 10%.
- **Conversão de Moeda:** Converte o valor final (com desconto) de USD para BRL.
- **Projeções de Lucro:** Exibe projeções semanais e mensais.
- **Estatísticas Básicas:** Calcula a média de lucro por axie.

## Sobre o Projeto

Este é o meu primeiro projeto público e foi criado com o objetivo de praticar e aprimorar meus conhecimentos em programação e controle de versão. Estou aberto a sugestões e melhorias, e espero que a ferramenta seja útil para outros usuários que também desejam praticar ou utilizar a contabilidade dos aluguéis de axies.

## Estrutura do Projeto


## Como Usar

1. **Execução da Aplicação:**
   - Dê um duplo clique no arquivo `executarAxies.bat`.
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
   - Se desejar compilar a partir do código-fonte, é necessário o [Apache Maven](https://maven.apache.org/).

## Build (Opcional)

Para compilar o projeto a partir do código-fonte:

1. Abra um terminal na raiz do projeto.
2. Execute o comando:
   ```bash
   mvn clean package

## Distribuição

Para distribuir a ferramenta, basta copiar toda a pasta **Contabilidade Axies** para qualquer máquina que possua o JRE 17 instalado. A execução é feita pelo script `executarAxies.bat`.
