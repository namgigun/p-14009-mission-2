import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JSONController {
    final String filePath = "db/wideSaying/";

    public void save(Map<Integer, Words> list) {
        int lastId = 0;

        for (Map.Entry<Integer, Words> entry : list.entrySet()) {
            lastId = entry.getKey();

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{\n");

            jsonBuilder
                    .append("id")
                    .append(": ")
                    .append("\"" + entry.getKey() + "\"")
                    .append(",\n")

                    .append("content")
                    .append(": ")
                    .append("\"" + entry.getValue().wiseSay + "\"")
                    .append(",\n")

                    .append("author")
                    .append(": ")
                    .append("\"" + entry.getValue().writer + "\"")
                    .append("\n");

            jsonBuilder.append("}");

            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(filePath + entry.getKey() + ".json"))) {
                writer.write(jsonBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath + "lastId.txt"))) {
            writer.write(Integer.toString(lastId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Words> read() {
        Map<Integer, Words> list = new HashMap<>();
        int lastId = 0;

        // lastId 읽기
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath + "lastId.txt"))) {
            lastId = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // json 파일읽기
        for(int i = lastId; i >= 1; i--) {
            StringBuilder jsonReader = new StringBuilder();
            String line;

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath + i + ".json"))) {
                jsonReader.append(reader.readLine());
                while ((line = reader.readLine()) != null) {
                    jsonReader.append(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return list;
    }
}
