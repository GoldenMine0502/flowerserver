package kr.goldenmine.flowerserver.article;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kr.goldenmine.flowerserver.profile.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    private Gson gson = new Gson();
    private File saveFile = new File("articles.json");

    private List<Article> articles = new ArrayList<>();
    private Type articlesType = new TypeToken<List<Article>>() {}.getType();
    private final Object articlesKey = new Object();

    public ArticleService() {
        load();
    }

    public int writeArticle(Profile profile, Article article) {
        // 지금까지 쓰인 모든 글의 갯수가 곧 쓰일 글의 id

        int articleId;

        synchronized (articlesKey) {
            int currentArticleId = articles.size();
            Article idArticle = new Article(currentArticleId, article.getAuthorId(), article.getTitle(), article.getContext(), article.getImageIds());

            articles.add(idArticle);

            articleId = currentArticleId;
        }

        profile.addNewArticleId(articleId);

        return articleId;
    }

    public Article getArticle(int id) {
        if(id < articles.size()) {
            return articles.get(id);
        } else {
            return null;
        }
    }

    public void load() {
        if(saveFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
                synchronized (articlesKey) {
                    articles = gson.fromJson(reader, articlesType);
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
            synchronized (articlesKey) {
                gson.toJson(articles, writer);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}

