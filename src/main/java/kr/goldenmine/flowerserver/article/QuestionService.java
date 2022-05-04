package kr.goldenmine.flowerserver.article;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kr.goldenmine.flowerserver.profile.Profile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionService extends IArticleService{
    @Override
    public String getRoute() {
        return "questions.json";
    }
}

