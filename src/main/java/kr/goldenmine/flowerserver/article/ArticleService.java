package kr.goldenmine.flowerserver.article;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kr.goldenmine.flowerserver.profile.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService extends IArticleService {
    @Override
    public String getRoute() {
        return "articles.json";
    }
}

