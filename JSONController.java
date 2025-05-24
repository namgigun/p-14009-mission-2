import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONController {
    final String filePath = "db/wideSaying/";

    // 파일에 현재 레포지토리 정보를 저장
    public void save(Map<Integer, Words> list) {
        int lastId = 0;

        for (Map.Entry<Integer, Words> entry : list.entrySet()) {
            lastId = Math.max(lastId, entry.getKey());

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

    // 파일 데이터 조회
    public Map<Integer, Words> read() {
        Map<Integer, Words> list = new HashMap<>();
        int lastId = 0;
        String regex = "id:\\s*\"([^\"]+)\"\\s*,\\s*content:\\s*\"([^\"]+)\"\\s*,\\s*author:\\s*\"([^\"]+)\"";

        // lastId 읽기
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath + "lastId.txt"))) {
            lastId = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            return list;
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
                continue;
            }

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(jsonReader.toString());

            if(matcher.find()) {
                int id = Integer.parseInt(matcher.group(1));
                String wiseSay = matcher.group(2);
                String writer = matcher.group(3);

                list.put(id, new Words(wiseSay, writer));
            }
        }

        return list;
    }

//    public void build(Map<Integer, Words> list) {
//
//    }
}
