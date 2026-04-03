package org.example;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// Este corresponde a la declaración de abril 2025 (Campaña de Renta 2024)
public class MainRenta2024 {
    public static void main(String[] args) throws IOException {
        final String carpetaRenta = "C:/Users/<your_user>/Documents/IRPF 2024 (abril 2025)/datos de exchanges/";
        Map<String, Double> currencyValuesMap = fillMaxYearValuesToCriptocurrencies();

        System.out.println("==============   PROCESANDO BINANCE    ================================");
        String binanceTransactionsFile = carpetaRenta + "Transactions_2025_03_08_18_50.csv";
        Map<String, List<Double>> currencyBinanceRewardsMap = calculateBinanceRewards(binanceTransactionsFile);
        sumEveryCurrencyInReward(currencyBinanceRewardsMap, currencyValuesMap);
        System.out.println("==============   FIN BINANCE    ================================");

        System.out.println("==============   PROCESANDO COINBASE   ================================");
        String coinbaseTransactionsFile = carpetaRenta + "coinbase_2024_transactions_history.csv";
        Map<String, List<Double>> currencyCoinbaseRewardsMap = calculateCoinbaseRewards(coinbaseTransactionsFile);
        sumEveryCurrencyRewardInEuros(currencyCoinbaseRewardsMap);
        System.out.println("==============   FIN COINBASE   ================================");

    }

    private static Map<String, Double> fillMaxYearValuesToCriptocurrencies() {
        // Precios maximos segun investing.com
        // MATIC = 0.50   o 1 (maximo)
        // KAVA = 1 (maximo) o 0.60 media
        // BNB = 674 maximo
        // USDT = 0,96 euros (maximo)
        // VTHO = 0,004 usd
        Map<String, Double> currencyValuesMap = new HashMap<>();
        // Binance MATIC: Todo el año
        currencyValuesMap.put("MATIC", 1.0);
        currencyValuesMap.put("POL", currencyValuesMap.get("MATIC"));
        // Binance KAVA: Todo el año
        currencyValuesMap.put("KAVA", 1.0);
        // Binance BNB: Todo el año
        currencyValuesMap.put("BNB", 674.0);
        // Binance USDT: 1 enero 2024 - 12 marzo 2024
        currencyValuesMap.put("USDT", 0.96);
        // Binance VTHO: 14 feb 2024 - 14 feb 2024
        currencyValuesMap.put("VTHO", 0.004);
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

            // Solo interesan los rewards porque:
            // 1. Los trade se reflejan en otro fichero
            // 2. Los SEND y RECEIVE son entre mis propias cuentas y no hay ganancias
            // 3. Se unifican por moneda porque solo hay 20 casillas para esto en la Renta
            int counter = 0;
            while ((nextLine = reader.readNext()) != null) {
                if (counter == 0) {
                    System.out.println(Arrays.toString(nextLine));
                    System.out.println("Elements per line: " + nextLine.length);
                    System.out.println("Received Currency: " + nextLine[8]);
                }
                if (Arrays.stream(nextLine).anyMatch("Reward"::equalsIgnoreCase)) {
                    //System.out.println("Cryptocurrency: " + nextLine[8]);
                    currencyRewardsMap.putIfAbsent(nextLine[8], new ArrayList<Double>());
                    currencyRewardsMap.get(nextLine[8]).add(Double.valueOf(nextLine[7]));
                } else if (Arrays.stream(nextLine).anyMatch("Trade"::equalsIgnoreCase)){
                    // Print lines corresponding to swaps (Trade)
                    System.out.println(Arrays.toString(nextLine));
                } else {
                    // No print lines Send or Receive between my accounts
                }
                //if (counter > 5) break;
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
