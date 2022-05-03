package kr.goldenmine.flowerserver.profile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    private Gson gson = new Gson();
    private File saveFile = new File("profile.json");

    private List<Profile> profiles = new ArrayList<>();
    private Type profilesType = new TypeToken<List<Profile>>() {}.getType();

    public ProfileService() {
        load();
    }

    public void addProfile(Profile profile) {
        profiles.add(profile);
    }

    public Optional<Profile> getProfile(String id) {
        Optional<Profile> profile = profiles.stream()
                .filter(it -> it.id.equals(id))
                // 비밀번호 제거
//                .map(it -> {
//                    it.password = null;
//                    return it;
//                })
                .findFirst();

        return profile;
    }

    public Optional<Profile> login(String id, String password) {
        return profiles.stream().filter(it -> it.id.equals(id) && it.password.equals(password)).findFirst();
    }

    public void load() {
        try(BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            profiles = gson.fromJson(reader, profilesType);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
            gson.toJson(profiles, writer);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
