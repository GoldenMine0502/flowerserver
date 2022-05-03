package kr.goldenmine.flowerserver.profile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequestMapping("/profile")
public class ProfileService {
    private Gson gson = new Gson();
    private File saveFile = new File("profile.json");

    private List<Profile> profiles = new ArrayList<>();
    private Type profilesType = new TypeToken<List<Profile>>() {}.getType();
    private final Object profilesKey = new Object();

    public ProfileService() {
        load();
    }

    public boolean addProfile(Profile profile) {

        synchronized (profilesKey) {
            // 해당하는 아이디 가입 내역이 없을때만 DB에 추가
            if(profiles.stream().noneMatch(it -> it.getId().equals(profile.getId()))) {
                profiles.add(profile);

                return true;
            } else {
                return false;
            }
        }
    }

    public Optional<Profile> getProfile(String id) {
        Optional<Profile> profile = profiles.stream()
                .filter(it -> it.getId().equals(id))
                .findFirst();

        if(profile.isPresent()) {
            try {
                Profile clone = profile.get().clone();
                clone.setPassword(null);
                return Optional.of(clone);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    public Optional<Profile> login(String id, String password) {
        return profiles.stream().filter(it -> it.getId().equals(id) && it.getPassword().equals(password)).findFirst();
    }

    public void load() {
        if(saveFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
                synchronized (profilesKey) {
                    profiles = gson.fromJson(reader, profilesType);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void save() {
        if(!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            synchronized (profilesKey) {
                gson.toJson(profiles, writer);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
