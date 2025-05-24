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

        // {id}.json 파일 저장
        for (Map.Entry<Integer, Words> entry : list.entrySet()) {
            lastId = Math.max(lastId, entry.getKey());
            /*
              다음과 같은 형식으로 StringBuilder 를 형성
              {
              	id: "1",
              	content: "현재",
              	author: "홍길동"
              }
             */
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{\n");

            jsonBuilder
                    .append("\tid")
                    .append(": ")
                    .append("\"" + entry.getKey() + "\"")
                    .append(",\n")

                    .append("\tcontent")
                    .append(": ")
                    .append("\"" + entry.getValue().wiseSay + "\"")
                    .append(",\n")

                    .append("\tauthor")
                    .append(": ")
                    .append("\"" + entry.getValue().writer + "\"")
                    .append("\n");

            jsonBuilder.append("}");

            /*
            {id}.json 에 {id}에 해당하는 정보 저장
             */
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(filePath + entry.getKey() + ".json"))) {
                writer.write(jsonBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
        lastId.txt 파일에 가장 최신으로 사용한 lastId를 저장.
         */
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

        // lastId 읽기 (만약 파일이 없거나 읽을 lastId가 없다면 빈 list 를 return 한다.
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath + "lastId.txt"))) {
            lastId = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            return list;
        }

        // json 파일읽기
        for(int i = lastId; i >= 1; i--) {
            StringBuilder jsonReader = new StringBuilder();
            String line;

            // {id}.json 으로 부터 데이터를 받아온 후, StringBuilder 에 모든 정보를 저장한다.
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath + i + ".json"))) {
                jsonReader.append(reader.readLine());
                while ((line = reader.readLine()) != null) {
                    jsonReader.append(line.trim());
                }
            } catch (IOException e) {
                continue;
            }

            // StringBuilder 에 저장한 모든 정보를 추출하여 list 에 정보를 추가한다.
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(jsonReader.toString());

            if(matcher.find()) {
                int id = Integer.parseInt(matcher.group(1));
                String wiseSay = matcher.group(2);
                String writer = matcher.group(3);

                list.put(id, new Words(wiseSay, writer));
            }
        }

        // 정보를 추가한 list 를 return
        return list;
    }

    // 빌드
    public void build(Map<Integer, Words> list) {
        StringBuilder jsonBuilder = new StringBuilder();

        // list 에 저장할 데이터를 체크 후
        if(!list.isEmpty()) {
            /*
            StringBuilder 안에 다음과 같은 형식으로 list 의 데이터를 저장한다.
            [
                {
                    "id": 1,
                    "content": "명언 1",
                    "author": "작가 1"
                  },
                  {
                    "id": 2,
                    "content": "명언 2",
                    "author": "작가 2"
                  }
              ]
             */
            jsonBuilder.append("[\n");
            int count = list.size();

            for (Map.Entry<Integer, Words> entry : list.entrySet()) {
                count--;
                jsonBuilder.append("\t{\n");

                jsonBuilder
                        .append("\t\tid")
                        .append(": ")
                        .append("\"" + entry.getKey() + "\"")
                        .append(",\n")

                        .append("\t\tcontent")
                        .append(": ")
                        .append("\"" + entry.getValue().wiseSay + "\"")
                        .append(",\n")

                        .append("\t\tauthor")
                        .append(": ")
                        .append("\"" + entry.getValue().writer + "\"")
                        .append("\n");

                if(count == 0) {
                    jsonBuilder.append("\t}\n");
                    break;
                }

                jsonBuilder.append("\t},");
            }

            jsonBuilder.append("]");
        }

        // data.json 에 StringBuilder 에 기록한 데이터를 저장
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + "data.json"))) {
            writer.write(jsonBuilder.toString());
            System.out.println("data.json 파일의 내용이 갱신되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
