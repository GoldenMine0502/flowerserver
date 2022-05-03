package kr.goldenmine.flowerserver;

import com.google.gson.JsonObject;
import kr.goldenmine.flowerserver.profile.Profile;
import kr.goldenmine.flowerserver.profile.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
public class ProfileController {

    private ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/login")
    public String login(String id, String password, HttpServletRequest request) throws IOException {
        Optional<Profile> profile = profileService.login(id, password);

        // 로그인 성공시 세션에 현재 로그인 정보 저장
        HttpSession session = request.getSession();
        profile.ifPresent(value -> session.setAttribute("profile", value));

        // 로그인 결과 리턴(성공여부, 로그인시간)
        JsonObject obj = new JsonObject();

        obj.addProperty("login_succeed", profile.isPresent());
        obj.addProperty("timestamp", getTimeStamp());

        return obj.toString();
    }

    @PostMapping("/logout")
    public String login(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();

        Profile profile = (Profile) session.getAttribute("profile");

        // 세션에 저장된 프로필 제거
        if(profile != null) {
            session.removeAttribute("profile");
        }

        // 로그아웃 결과 리턴(성공여부, 로그인시간)
        JsonObject obj = new JsonObject();

        obj.addProperty("logout_succeed", profile != null);
        obj.addProperty("timestamp", getTimeStamp());

        return obj.toString();
    }

//    public String

    public static String getTimeStamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        System.out.println(timestamp); // 생성한 timestamp 출력

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        System.out.println(sdf.format(timestamp)); // format을 사용해 출력

        return sdf.format(timestamp);
    }

}
