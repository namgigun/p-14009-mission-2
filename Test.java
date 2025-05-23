import java.util.Map;

// Read 테스트
public class Test {
    static String filePath = "db/wideSaying/";
    public static void main(String[] args) {
        JSONController jsonController = new JSONController();

        Map<Integer, Words> testMap = jsonController.read();

        for(Map.Entry<Integer, Words> map : testMap.entrySet()) {
            System.out.println("id: " + map.getKey());
            System.out.println("명언: " + map.getValue().wiseSay);
            System.out.println("작가: " + map.getValue().writer);
        }
    }
}
