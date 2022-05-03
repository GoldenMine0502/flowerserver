package kr.goldenmine.flowerserver.profile;

import com.google.gson.JsonObject;
import kr.goldenmine.flowerserver.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileService profileService;

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
        obj.addProperty("timestamp", TimeUtil.getTimeStamp());

        return obj.toString();
    }

    @PostMapping("/checksession")
    public String checkSession(HttpServletRequest request) throws IOException {


        HttpSession session = request.getSession();
        Profile profile = (Profile) session.getAttribute("profile");


        LOGGER.info("check session: " + (profile != null ? profile.getId() : null));

        // 로그인 결과 리턴(성공여부, 로그인시간)
        JsonObject obj = new JsonObject();

        obj.addProperty("session_exists", profile != null);
        obj.addProperty("session_id", profile != null ? profile.getId() : null);
        obj.addProperty("timestamp", TimeUtil.getTimeStamp());

        return obj.toString();
    }

    @PostMapping("/register")
    public String register(String id, String password) throws IOException {
        boolean isRegistered = profileService.addProfile(new Profile(id, password, null, null, null, new ArrayList<>()));

        LOGGER.info("new account registered: id: " + id + ", password: " + password + ", isRegistered: " + isRegistered);

        // 로그아웃 결과 리턴(성공여부, 로그인시간)
        JsonObject obj = new JsonObject();

        obj.addProperty("register_succeed", isRegistered);
        obj.addProperty("timestamp", TimeUtil.getTimeStamp());

        return obj.toString();
    }

    @PostMapping("/getprofile")
    public Profile getProfile(String id) throws IOException {
        Optional<Profile> profile = profileService.getProfile(id);

        LOGGER.info("account request received: " + id + ", " + profile.orElse(null));

        return profile.orElse(null);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();

        Profile profile = (Profile) session.getAttribute("profile");

        // 세션에 저장된 프로필 제거
        if(profile != null) {
            session.removeAttribute("profile");
        }

        // 로그아웃 결과 리턴(성공여부, 로그인시간)
        JsonObject obj = new JsonObject();

        obj.addProperty("logout_succeed", profile != null);
        obj.addProperty("timestamp", TimeUtil.getTimeStamp());

        LOGGER.info("logout request succeed: " + (profile != null) + " id: " + (profile != null ? profile.getId() : null));

        return obj.toString();
    }

//    public String


}
