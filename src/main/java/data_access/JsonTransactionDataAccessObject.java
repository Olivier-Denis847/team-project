package data_access;

import entity.Transaction;
import entity.Label;
import use_case.addtransaction.TransactionDataAccessInterface;

import java.io.*;
import java.util.*;

public class JsonTransactionDataAccessObject implements TransactionDataAccessInterface {
    private final File file;

    public JsonTransactionDataAccessObject(String file) {
        this.file = new File(file);
    }

    @Override
    public void save(Transaction transaction) {
        List<Transaction> transactions = getAll();
        transactions.add(transaction);
        writeToFile(transactions);
    }

    @Override
    public List<Transaction> getAll() {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        List<Transaction> list = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;
                if (!line.trim().startsWith("{")) continue;
                Map<String, String> map = parseJsonLine(line);

                int id = Integer.parseInt(map.get("id"));
                float amount = Float.parseFloat(map.get("amount"));
                String type = map.get("type");
                String note = map.get("note");
                Date date = new Date(Long.parseLong(map.get("date")));
                list.add(new Transaction(id, amount, new ArrayList<Label>(), note, date, type));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private Map<String, String> parseJsonLine(String line) {
        Map<String, String> map = new HashMap<>();

        line = line.replace("{", "").replace("}", "");
        String[] split = line.split(",");

        for (String item : split) {
            String[] keyValue = item.split(":");
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            map.put(key, value);
        }
        return map;
    }

    private void writeToFile(List<Transaction> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Transaction transction : list) {
                pw.println(transction.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Transaction> getTransactions() {
    List<Transaction> list = new ArrayList<>();
    return list;
    }
}