package org.example;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

    // Variáveis para cache da taxa de conversão (cache por 4 horas)
    private static double cachedRate = -1;
    private static long cachedRateTimestamp = 0;
    private static final long CACHE_TTL = 4 * 60 * 60 * 1000; // 4 horas em milissegundos

    // Classe interna para armazenar os dados de cada registro
    public static class Registro {
        String axieId;
        BigDecimal valor; // valor extraído (em USD)

        public Registro(String axieId, BigDecimal valor) {
            this.axieId = axieId;
            this.valor = valor;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BigDecimal totalUSD = BigDecimal.ZERO;
        int totalRegistros = 0;

        // Loop para permitir a colagem de múltiplos blocos de dados
        while (true) {
            System.out.println("Cole os dados (finalize o bloco digitando uma linha com 'FIM'):");
            StringBuilder bloco = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).equals("FIM")) {
                bloco.append(line).append("\n");
            }

            String dados = bloco.toString().trim();
            if (dados.isEmpty()) {
                System.out.println("Nenhum dado foi inserido. Encerrando.");
                break;
            }

            // Processa o bloco e extrai os registros
            List<Registro> registros = processarBloco(dados);
            BigDecimal somaUSD = BigDecimal.ZERO;
            for (Registro reg : registros) {
                somaUSD = somaUSD.add(reg.valor);
            }
            totalUSD = totalUSD.add(somaUSD);
            totalRegistros += registros.size();

            System.out.println("Bloco processado:");
            System.out.println(" - Registros neste bloco: " + registros.size());
            System.out.println(" - Soma dos valores (USD): " + somaUSD);

            System.out.println("Deseja colar outro bloco? (S/N)");
            String resposta = scanner.nextLine().trim();
            if (!resposta.equalsIgnoreCase("S")) {
                break;
            }
        }

        System.out.println("Resumo final:");
        System.out.println("Total de Axies registrados: " + totalRegistros);
        System.out.println("Soma total dos valores (USD): " + totalUSD);

        // Desconto de 10% (a plataforma cobra 10%)
        BigDecimal totalDescontadoUSD = totalUSD.multiply(BigDecimal.valueOf(0.90))
                .setScale(4, RoundingMode.HALF_UP);
        System.out.println("Soma total dos valores após desconto de 10% (USD): " + totalDescontadoUSD);

        try {
            // Conversão de USD para BRL utilizando a API Frankfurter (com cache)
            double taxaConversao = getConversionRate("USD", "BRL");
            BigDecimal taxaConv = BigDecimal.valueOf(taxaConversao);
            BigDecimal totalBRL = totalDescontadoUSD.multiply(taxaConv)
                    .setScale(4, RoundingMode.HALF_UP);
            System.out.println("Taxa de conversão (USD para BRL): " + taxaConv);
            System.out.println("Soma total convertida (BRL): " + totalBRL);

            // Projeção de lucro semanal e mensal (em USD e BRL)
            BigDecimal lucroSemanalUSD = totalDescontadoUSD.multiply(BigDecimal.valueOf(7)).setScale(4, RoundingMode.HALF_UP);
            BigDecimal lucroMensalUSD = totalDescontadoUSD.multiply(BigDecimal.valueOf(30)).setScale(4, RoundingMode.HALF_UP);
            BigDecimal lucroSemanalBRL = lucroSemanalUSD.multiply(taxaConv).setScale(4, RoundingMode.HALF_UP);
            BigDecimal lucroMensalBRL = lucroMensalUSD.multiply(taxaConv).setScale(4, RoundingMode.HALF_UP);

            System.out.println("Projeção semanal (USD): " + lucroSemanalUSD);
            System.out.println("Projeção mensal (USD): " + lucroMensalUSD);
            System.out.println("Projeção semanal (BRL): " + lucroSemanalBRL);
            System.out.println("Projeção mensal (BRL): " + lucroMensalBRL);

            // Estatísticas adicionais: média de lucro por axie
            if (totalRegistros > 0) {
                BigDecimal mediaUSD = totalDescontadoUSD.divide(BigDecimal.valueOf(totalRegistros), 4, RoundingMode.HALF_UP);
                BigDecimal mediaBRL = mediaUSD.multiply(taxaConv).setScale(4, RoundingMode.HALF_UP);
                System.out.println("Média de lucro por axie (USD): " + mediaUSD);
                System.out.println("Média de lucro por axie (BRL): " + mediaBRL);
            }
        } catch (Exception e) {
            System.err.println("Erro na conversão de moeda: " + e.getMessage());
        }

        scanner.close();
    }

    /**
     * Processa um bloco de dados e extrai os registros.
     * Cada registro contém o ID do Axie e o valor (após o número 1) em USD.
     */
    private static List<Registro> processarBloco(String dados) {
        List<Registro> registros = new ArrayList<>();
        // Padrões para extrair o valor e o ID do Axie
        Pattern patternValor = Pattern.compile("\\$\\s*(\\d+\\.\\d+)");
        Pattern patternAxie = Pattern.compile("Axie\\s*#(\\d+)");

        String currentAxie = null;
        BigDecimal currentValor = null;
        String lastFinalizedAxie = null;
        boolean skipNextAxieDuplicate = false;

        String[] linhas = dados.split("\\r?\\n");
        for (String linha : linhas) {
            linha = linha.trim();
            if (linha.isEmpty()) {
                continue;
            }
            // Ignora linhas com valor acumulativo (que contêm "+$")
            if (linha.contains("+$")) {
                continue;
            }
            // Se a linha contém "$" (mas não "+$"), extrai o valor desejado
            if (linha.contains("$")) {
                Matcher mValor = patternValor.matcher(linha);
                if (mValor.find()) {
                    if (currentValor == null) {
                        try {
                            currentValor = new BigDecimal(mValor.group(1));
                        } catch (NumberFormatException e) {
                            System.err.println("Erro ao converter valor: " + mValor.group(1));
                        }
                    }
                }
            }
            // Se a linha contém "Axie #", extrai o ID
            else if (linha.contains("Axie #")) {
                Matcher mAxie = patternAxie.matcher(linha);
                if (mAxie.find()) {
                    String axieId = mAxie.group(1);
                    // Ignora duplicata imediata
                    if (skipNextAxieDuplicate && axieId.equals(lastFinalizedAxie)) {
                        skipNextAxieDuplicate = false;
                        continue;
                    }
                    if (currentAxie == null) {
                        currentAxie = axieId;
                    } else {
                        // Se já temos um ID e o novo é diferente (caso raro), finaliza o registro se o valor já foi capturado
                        if (!currentAxie.equals(axieId) && currentValor != null) {
                            registros.add(new Registro(currentAxie, currentValor));
                            lastFinalizedAxie = currentAxie;
                            skipNextAxieDuplicate = true;
                            currentAxie = axieId;
                            currentValor = null;
                        }
                    }
                }
            }
            // Se ambos os dados foram capturados, finaliza o registro
            if (currentAxie != null && currentValor != null) {
                registros.add(new Registro(currentAxie, currentValor));
                lastFinalizedAxie = currentAxie;
                skipNextAxieDuplicate = true;
                currentAxie = null;
                currentValor = null;
            }
        }
        // Se houver um registro pendente ao final, adiciona-o
        if (currentAxie != null && currentValor != null) {
            registros.add(new Registro(currentAxie, currentValor));
        }
        return registros;
    }

    /**
     * Busca a taxa de conversão de uma moeda para outra utilizando a API Frankfurter.
     * URL de exemplo: https://api.frankfurter.app/latest?from=USD&to=BRL
     * Implementa cache para salvar o valor por algumas horas.
     */
    private static double getConversionRate(String base, String target) throws Exception {
        long now = System.currentTimeMillis();
        if (cachedRate != -1 && (now - cachedRateTimestamp) < CACHE_TTL) {
            return cachedRate;
        }

        String url = "https://api.frankfurter.app/latest?from=" + base + "&to=" + target;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse do JSON retornado
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!jsonObject.has("rates")) {
            throw new Exception("A resposta da API não contém 'rates'. Resposta completa: " + response.body());
        }
        JsonObject rates = jsonObject.getAsJsonObject("rates");
        if (!rates.has(target)) {
            throw new Exception("A resposta da API não contém a taxa para " + target + ". Resposta completa: " + response.body());
        }
        double rate = rates.get(target).getAsDouble();
        cachedRate = rate;
        cachedRateTimestamp = now;
        return rate;
    }
}
