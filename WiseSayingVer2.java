import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class WiseSayingVer2 {
    static Scanner sc = new Scanner(System.in);
    static String filePath = "db/wideSaying/";
    static int lastId = 0;

    static String regex = ".*[!@#$%^&*()\\-_=+\\[\\]{}|\\\\;:'\",<>/?].*";

    static JSONController jsonController = new JSONController();
    static Map<Integer, Words> list = new HashMap<>(); // 명언 저장 레포지토리 <id, 명언정보>
    public static void main(String[] args) {
        System.out.println("== 명언 앱 ==");
        list = jsonController.read();

        // Controller로 부터 데이터를 불러온 경우
        if(!list.isEmpty()) {
            getLastId();
        }

        // 명령입력
        label:
        while (true) {
            System.out.print("명령) ");
            String cmd = sc.nextLine().trim();

            switch (cmd) {
                case "종료":
                    break label;
                case "등록":
                    enroll();
                    break;
                case "목록":
                    showList();
                    break;
                case"빌드":
                    jsonController.build(list);
                    break;
                default:
                    if (cmd.startsWith("삭제")) {
                        delete(cmd);
                    } else if (cmd.startsWith("수정")) {
                        revise(cmd);
                    }
                    break;
            }
        }

        // 레포지토리 정보를 파일에 저장
        jsonController.save(list);
        sc.close();
    }

    // 특수문자 체크
    private static boolean isAsterisk(String input) {
        if (input.matches(regex)) {
            System.out.println("특수문자가 포함되었습니다. 다시 입력해주세요.");
            return true;
        }

        return false;
    }

    // 삭제
    private static void delete(String cmd) {
        // 명령어로부터 id 값을 추출
        StringTokenizer st = new StringTokenizer(cmd,"삭제?id=");
        int deleteId = Integer.parseInt(st.nextToken());

        // id 값에 해당하는 정보가 없는 경우
        if(list.get(deleteId) == null) {
            System.out.println(deleteId + "번 명언은 존재하지 않습니다.");
            return;
        }

        // id 값에 해당하는 레포지토리로부터 명언정보를 삭제한다.
        list.remove(deleteId);
        System.out.println(deleteId + "번 명언이 삭제되었습니다.");
    }

    // 수정
    private static void revise(String cmd) {
        // 명령어로부터 id 값을 추출
        StringTokenizer st = new StringTokenizer(cmd,"수정?id=");
        int reviseId = Integer.parseInt(st.nextToken());

        // id 값에 해당하는 정보가 없는 경우
        if(list.get(reviseId) == null) {
            System.out.println(reviseId + "번 명언은 존재하지 않습니다.");
            return;
        }

        // 수정할 정보를 입력
        System.out.println("명언(기존) : " + list.get(reviseId).wiseSay);

        String wiseSay;
        do {
            System.out.print("명언 : ");
            wiseSay = sc.nextLine().trim();
        }  while(isAsterisk(wiseSay));

        System.out.println("작가(기존) : " + list.get(reviseId).writer);

        String writer;
        do {
            System.out.print("작가 : ");
            writer = sc.nextLine().trim();
        } while(isAsterisk(writer));

        list.replace(reviseId, new Words(wiseSay, writer));
    }

    // 목록
    private static void showList() {
        System.out.println("번호 / 작가 / 명언");
        System.out.println("----------------------");

        // 리스트 출력
        for(int i = lastId; i >= 1; i--) {
            Words one = list.get(i);

            // 레포지토리에 존재하지 않는 정보는 출력하지 않는다.
            if(one != null) {
                System.out.println(i + " / "+ one.writer + " / "
                        +  one.wiseSay);
            }
        }
    }

    // 등록
    private static void enroll() {
        // 삽입할 명언 정보를 입력
        String wiseSay;
        do {
            System.out.print("명언 : ");
            wiseSay = sc.nextLine().trim();
        }  while(isAsterisk(wiseSay));

        String writer;
        do {
            System.out.print("작가 : ");
            writer = sc.nextLine().trim();
        } while(isAsterisk(writer));

        // 명언 레포지토리에 입력 정보를 삽입
        lastId++;
        list.put(lastId, new Words(wiseSay, writer));
        System.out.println(lastId + "번 명언이 등록되었습니다.");
    }

    // 파일로부터 최신Id를 출력
    private static void getLastId() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath + "lastId.txt"))) {
            lastId = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
