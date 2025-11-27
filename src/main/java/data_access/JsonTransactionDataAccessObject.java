package data_access;

import entity.Transaction;
import entity.Label;

import use_case.add_transaction.TransactionDataAccessInterface;


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

        try {
            String content = readFile();
            if (content.trim().isEmpty()) return list;

            content = content.trim();
            if (content.startsWith("[")) content = content.substring(1);
            if (content.endsWith("]")) content = content.substring(0, content.length() - 1);

            String[] objects = content.split("(?<=}),");

            for (String obj : objects) {
                obj = obj.trim();
                if (obj.endsWith(",")) obj = obj.substring(0, obj.length() - 1);

                Map<String, String> map = parseJsonObject(obj);

                int id = Integer.parseInt(map.get("id"));
                float amount = Float.parseFloat(map.get("amount"));
                String type = map.get("type");
                String note = map.get("note");
                long dateLong = Long.parseLong(map.get("date"));
                Date date = new Date(dateLong);

                list.add(new Transaction(id, amount, new ArrayList<Label>(), note, date, type));
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private String readFile() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private Map<String, String> parseJsonObject(String json) {
        Map<String, String> map = new HashMap<>();

        json = json.replace("{", "").replace("}", "").trim();
        String[] pairs = json.split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            map.put(key, value);
        }

        return map;
    }

    private void writeToFile(List<Transaction> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {

            pw.println("[");
            for (int i = 0; i < list.size(); i++) {
                Transaction transaction = list.get(i);
                String json =
                        "  {" +
                                "\"id\":" + transaction.getId() + "," +
                                "\"amount\":" + transaction.getAmount() + "," +
                                "\"type\":\"" + transaction.getType() + "\"," +
                                "\"note\":\"" + transaction.getNote() + "\"," +
                                "\"date\":\"" + transaction.getDate().getTime() + "\"" +
                                "}";
                if (i < list.size() - 1) json += ",";
                pw.println(json);
            }
            pw.println("]");

        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON", e);
        }
    }

    // we do not need this method.
    @Override
    public List<Transaction> getTransactions() {
        return getAll();
    }
}