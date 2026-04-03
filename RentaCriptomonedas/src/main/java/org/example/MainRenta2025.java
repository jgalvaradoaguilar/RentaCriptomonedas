package org.example;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// Este corresponde a la declaración de abril 2025 (Campaña de Renta 2024)
public class MainRenta2025 {
    public static void main(String[] args) throws IOException {
        final String carpetaRenta = "C:/Users/j_gab/Documents/IRPF 2025 (abril 2026)/datos de exchanges/";
        Map<String, Double> currencyValuesMap = fillMaxYearValuesToCriptocurrencies();

        System.out.println("==============   PROCESANDO BINANCE    ================================");
        String binanceTransactionsFile = carpetaRenta + "Binance-Historial-de-transacciones-202604021143(UTC+2).csv";
        Map<String, List<Double>> currencyBinanceRewardsMap = calculateBinanceRewards(binanceTransactionsFile);
        sumEveryCurrencyInReward(currencyBinanceRewardsMap, currencyValuesMap);
        System.out.println("==============   FIN BINANCE    ================================");

        System.out.println("==============   PROCESANDO COINBASE   ================================");
        String coinbaseTransactionsFile = carpetaRenta + "coinbase_2025_transactions_history.csv";
        Map<String, List<Double>> currencyCoinbaseRewardsMap = calculateCoinbaseRewards(coinbaseTransactionsFile);
        sumEveryCurrencyRewardInEuros(currencyCoinbaseRewardsMap);
        System.out.println("==============   FIN COINBASE   ================================");

    }

    private static Map<String, Double> fillMaxYearValuesToCriptocurrencies() {
        Map<String, Double> currencyValuesMap = new HashMap<>();

        // Binance HUMA: 25 may 2025
        currencyValuesMap.put("HUMA", 0.05);
        // Binance C: 18 jul 2025
        currencyValuesMap.put("C", 0.30);
        // Binance TOWNS: 4 aug 2025
        currencyValuesMap.put("TOWNS", 0.029);
        // Binance BABY: 10 apr 2025
        currencyValuesMap.put("BABY", 0.001);
        // Binance WCT: 15 apr 2025
        currencyValuesMap.put("WCT", 0.35);
        // Binance GPS: 4 mar 2025
        currencyValuesMap.put("GPS", 0.13);
        // Binance SXT: 8 may 2025
        currencyValuesMap.put("SXT", 0.14);
        // Binance STO: 2 may 2025
        currencyValuesMap.put("STO", 0.142);
        // Binance SPK: 16 jun 2025
        currencyValuesMap.put("SPK", 0.056);
        // Binance POND: The whole year
        currencyValuesMap.put("POND", 0.02);
        // Binance KAITO: 19 feb 2025
        currencyValuesMap.put("KAITO", 1.12);
        // Binance HAEDAL: 21 may 2025
        currencyValuesMap.put("HAEDAL", 0.192);
        // Binance NXPC: 15 may 2025
        currencyValuesMap.put("NXPC", 3.58);
        // Binance SIGN: 28 apr 2025
        currencyValuesMap.put("SIGN", 0.081);
        // Binance GUN: 31 mar 2025
        currencyValuesMap.put("GUN", 0.098);
        // Binance SAHARA: 25 jun 2025
        currencyValuesMap.put("SAHARA", 0.129);
        // Binance KAVA: The whole year
        currencyValuesMap.put("KAVA", 0.49);
        // Binance SHELL: 27 feb 2025
        currencyValuesMap.put("SHELL", 0.659);
        // Binance BMT: 18 mar 2025
        currencyValuesMap.put("BMT", 0.285);
        // Binance BIO: 3 jan 2025
        currencyValuesMap.put("BIO", 0.863);
        // Binance POL: The whole year
        currencyValuesMap.put("POL", 0.4);
        // Binance ANIME: 23 jan 2025
        currencyValuesMap.put("ANIME", 0.092);
        // Binance HYPER: 22 apr 2025
        currencyValuesMap.put("HYPER", 0.248);
        // Binance BERA: 6 feb 2025
        currencyValuesMap.put("BERA", 12.0);
        // Binance VTHO: The whole year
        currencyValuesMap.put("VTHO", 0.005);
        // Binance RED: 28 feb 2025
        currencyValuesMap.put("RED", 0.726);
        // Binance NIL: 24 mar 2025
        currencyValuesMap.put("NIL", 0.71);
        // Binance PARTI: 25 mar 2025
        currencyValuesMap.put("PARTI", 0.356);
        // Binance NEWT: 24 jun 2025
        currencyValuesMap.put("NEWT", 0.637);
        // Binance PROVE: 5 aug 2025
        currencyValuesMap.put("PROVE", 0.97);
        // Binance INIT: 24 apr 2025
        currencyValuesMap.put("INIT", 0.684);
        // Binance SOPH: 28 may 2025
        currencyValuesMap.put("SOPH", 0.097);
        // Binance LA: 9 jul 2025
        currencyValuesMap.put("LA", 0.625);
        // Binance ERA: 16 jul 2025
        currencyValuesMap.put("ERA", 0.866);
        // Binance BNB: The whole year
        currencyValuesMap.put("BNB", 1000.0);
        // Binance RESOLV: 11 jun 2025
        currencyValuesMap.put("RESOLV", 0.359);
        // Binance TREE: 29 jul 2025
        currencyValuesMap.put("TREE", 0.377);
        // Binance USDC: 23 feb 2025
        currencyValuesMap.put("USDC", 0.96);
        // Binance LAYER: feb and jun 2025
        currencyValuesMap.put("LAYER", 1.0);
        // Binance HOME: 12 jun 2025
        currencyValuesMap.put("HOME", 0.031);

        return currencyValuesMap;
    }

    private static Map<String, List<Double>> calculateBinanceRewards(String binanceTransactionsFile) throws IOException {
        final char SEPARATOR=',';
        final char QUOTE='"';
        CSVReader reader = null;
        Map<String, List<Double>> currencyRewardsMap = new HashMap<>();
        try {
            reader = new CSVReader(new FileReader(binanceTransactionsFile),SEPARATOR,QUOTE);
            String[] nextLine;

            // Se unifican por moneda porque solo hay 20 casillas para esto en la Renta
            int counter = 0;
            while ((nextLine = reader.readNext()) != null) {
                if (counter == 0) {
                    System.out.println(Arrays.toString(nextLine));
                    System.out.println("Elements per line: " + nextLine.length);
                    System.out.println("Received Currency: " + nextLine[4]);
                    counter ++;
                    continue;
                }
                if (Arrays.stream(nextLine).anyMatch(s ->
                    s.contains("Rewards") || s.contains("Interest") || s.contains("Airdrop"))) {
                    //System.out.println("Cryptocurrency: " + nextLine[4]);
                    currencyRewardsMap.putIfAbsent(nextLine[4], new ArrayList<Double>());
                    currencyRewardsMap.get(nextLine[4]).add(Double.valueOf(nextLine[5]));
                } else if (Arrays.stream(nextLine).anyMatch("Withdraw"::equalsIgnoreCase)){
                    // Print lines corresponding to Withdraw (i.e. RTX coin purchased using BNB)
                    System.out.println("WITHDRAW: " + Arrays.toString(nextLine));
                } else if (Arrays.stream(nextLine).noneMatch(s ->
                    s.contains("Subscription") | s.contains("Redemption"))){
                    System.out.println("OTHER: " + Arrays.toString(nextLine));
                }
                counter++;
            }
            System.out.println("Número de Lineas: " + counter);
            System.out.println("currencyRewardsMap: " + currencyRewardsMap.toString());

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
        return currencyRewardsMap;
    }

    private static Map<String, List<Double>> calculateCoinbaseRewards(String coinbaseTransactionsFile) throws IOException {
        final char SEPARATOR=',';
        final char QUOTE='"';
        CSVReader reader = null;
        Map<String, List<Double>> currencyRewardsMap = new HashMap<>();
        try {
            reader = new CSVReader(new FileReader(coinbaseTransactionsFile),SEPARATOR,QUOTE);
            String[] nextLine;

            // Solo interesan los "Staking Income" porque:
            // 1. Los trade y learning rewards se calculan aparte
            // 2. Los SEND y RECEIVE son entre mis propias cuentas y no hay ganancias
            // 3. Se unifican por moneda porque solo hay 20 casillas para esto en la Renta
            int counter = 0;
            while ((nextLine = reader.readNext()) != null) {
                if (counter == 0) {
                    System.out.println(Arrays.toString(nextLine));
                    System.out.println("Elements per line: " + nextLine.length);
                    System.out.println("Received Currency (Asset): " + nextLine[3]);
                    System.out.println("Total: " + nextLine[8]);
                }
                if (Arrays.stream(nextLine).anyMatch("Staking Income"::equalsIgnoreCase)) {
                    String totalFiltered = nextLine[8].split("€")[1];
                    currencyRewardsMap.putIfAbsent(nextLine[3], new ArrayList<Double>());
                    currencyRewardsMap.get(nextLine[3]).add(Double.valueOf(totalFiltered));
                } else if (Arrays.stream(nextLine).anyMatch("Learning Reward"::equalsIgnoreCase)) {
                    String totalFiltered = nextLine[8].split("€")[1];
                    currencyRewardsMap.putIfAbsent(nextLine[3], new ArrayList<Double>());
                    currencyRewardsMap.get(nextLine[3]).add(Double.valueOf(totalFiltered));
                } else if (Arrays.stream(nextLine).anyMatch("Convert"::equalsIgnoreCase)) {
                    // Print lines corresponding to swaps (Convert)
                    System.out.println(Arrays.toString(nextLine));
                } else {
                    // No Print lines corresponding to Send - Receive
                }
                counter++;
            }
            System.out.println("Número de Lineas: " + counter);
            System.out.println("currencyRewardsMap: " + currencyRewardsMap.toString());

        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
        return currencyRewardsMap;
    }


    private static void sumEveryCurrencyInReward(Map<String, List<Double>> currencyRewardsMap,
                                                 Map<String, Double> currencyValuesMap) {
        // Sumamos los valores para cada moneda y acumulamos los euros
        double totalStakingEuros = 0.0;
        for(Map.Entry<String, List<Double>> entrada:currencyRewardsMap.entrySet()){
            String currency = entrada.getKey();
            System.out.print(currency + " ");
            System.out.println(entrada.getValue());
            List<Double> valuesList = entrada.getValue();
            Double sum = valuesList.stream().reduce((x, y) -> x + y).get();
            System.out.println("Sumatorio: " + sum + " " + currency);
            double currencyValue = currencyValuesMap.get(currency);
            System.out.println("currencyValue: " + currencyValue + " EUROS");
            double estimatedEuros = sum * currencyValue;
            System.out.println("estimatedEuros: " + estimatedEuros + " EUROS");
            totalStakingEuros += estimatedEuros;
        }
        System.out.println("totalStakingEuros: " + totalStakingEuros + " EUROS");
    }

    /**
     * Coinbase ya hace el cálculo de cada reward en euros,
     * por lo que solo hay que sumar
     * @param currencyRewardsMap
     */
    private static void sumEveryCurrencyRewardInEuros(Map<String, List<Double>> currencyRewardsMap) {
        // Sumamos los valores para cada moneda y acumulamos los euros
        double totalStakingEuros = 0.0;
        for(Map.Entry<String, List<Double>> entrada:currencyRewardsMap.entrySet()){
            String currency = entrada.getKey();
            System.out.print(currency + " ");
            System.out.println(entrada.getValue());
            List<Double> valuesList = entrada.getValue();
            Double sum = valuesList.stream().reduce((x, y) -> x + y).get();
            System.out.println("estimatedEuros: " + sum + " EUROS");
            totalStakingEuros += sum;
        }
        System.out.println("totalStakingEuros: " + totalStakingEuros + " EUROS");
    }

}