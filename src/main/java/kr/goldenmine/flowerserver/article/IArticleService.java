package kr.goldenmine.flowerserver.article;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kr.goldenmine.flowerserver.profile.Profile;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class IArticleService {
    private final Gson gson = new Gson();
    private final File saveFile = new File(getRoute());

    private final List<Article> articles = new ArrayList<>();
    private final Type articlesType = new TypeToken<List<Article>>() {}.getType();
    private final Object articlesKey = new Object();

    public IArticleService() {
        load();
    }

    public abstract String getRoute();

    public int writeArticle(Profile profile, Article article) {
        // 지금까지 쓰인 모든 글의 갯수가 곧 쓰일 글의 id

        int articleId;

        synchronized (articlesKey) {
            int currentArticleId = articles.size();
            Article idArticle = new Article(currentArticleId, article.getAuthorId(), article.getTitle(), article.getContext(), article.getImageIds(), new LinkedList<>());

            articles.add(idArticle);

            articleId = currentArticleId;
        }

        profile.addNewArticleId(articleId);

        return articleId;
    }


    public List<Article> subArticle(int page, int index) {
        // 글이 0,1,2,3,4,5,6,7,8,9 인덱스
        // page = 2, index = 1 하면
        // size = 10
        // startIndex = 2
        // finishIndex = 4
        // 6,7
        int startIndex = page * index;
        int finishIndex = startIndex + page;

        int recentStartIndex = articles.size() - finishIndex - 1;
        int recentFinishIndex = articles.size() - startIndex - 1;

        // 범위 밖의 경우 빈 리스트 리턴
        if(!(recentStartIndex >= 0 && recentStartIndex < articles.size()) ||
                !(recentFinishIndex >= 0 && recentFinishIndex < articles.size())) {
            return Collections.emptyList();
        }
        List<Article> result = articles.subList(recentStartIndex, recentFinishIndex);

        Collections.reverse(result);

        return result;
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
                    List<Article> articles = gson.fromJson(reader, articlesType);
                    this.articles.clear();
                    this.articles.addAll(articles);
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
