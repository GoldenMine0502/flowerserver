package kr.goldenmine.flowerserver.article;

import com.google.gson.JsonObject;
import kr.goldenmine.flowerserver.PrintUtil;
import kr.goldenmine.flowerserver.TimeUtil;
import kr.goldenmine.flowerserver.profile.Profile;
import kr.goldenmine.flowerserver.profile.ProfileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/article")
public class ArticleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;
    private final QuestionService questionService;

    public ArticleController(ArticleService articleService, QuestionService questionService) {
        this.articleService = articleService;
        this.questionService = questionService;
    }

    private IArticleService getService(String type) {
        switch (type) {
            case "article": return articleService;
            case "question": return questionService;
        }

        return null;
    }

    @PostMapping("/writearticle")
    public String writeArticle(String title, String context, Integer[] imageIds, String type, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        Profile profile = (Profile) session.getAttribute("profile");

        IArticleService service = getService(type);

        // 글 쓰기 결과 리턴
        JsonObject obj = new JsonObject();

        if(service != null) {
            if (profile != null) {
                Article article = new Article(0, profile.getId(), title, context, Arrays.asList(imageIds), new LinkedList<>());
                int id = articleService.writeArticle(profile, article);
                article = new Article(id, profile.getId(), title, context, Arrays.asList(imageIds), new LinkedList<>());

                obj.addProperty("write_succeed", true);
                obj.addProperty("fail_cause", "none");

                LOGGER.info("write article succeed: " + article);
            } else {
                obj.addProperty("write_succeed", false);
                obj.addProperty("fail_cause", "no session");
                LOGGER.info("write article failed cause: no session");
            }
        } else {
            String cause = "no service: " + type;

            obj.addProperty("write_succeed", false);
            obj.addProperty("fail_cause", cause);
            LOGGER.info(cause);
        }

        obj.addProperty("timestamp", TimeUtil.getTimeStamp());

        return obj.toString();
    }

    @PostMapping("/writecomment")
    public String writeComment(int id, int parentId, @RequestParam("comment") String commentStr, String type, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Profile profile = (Profile) session.getAttribute("profile");

        IArticleService service = getService(type);

        // 글 쓰기 결과 리턴
        JsonObject obj = new JsonObject();

        if(profile != null) {
            if(service != null) {
                Article article = service.getArticle(id);

                if(article != null) {
                    boolean isInserted = parentId != -1;
                    Comment comment = new Comment(parentId, profile.getId(), commentStr, 0, isInserted);
                    int insertId = isInserted ? article.getLastInsertedComment(parentId) : -1;

                    article.writeComment(insertId, comment);

                    obj.addProperty("write_succeed", true);
                    obj.addProperty("fail_cause", "none");
                }
            } else {
                obj.addProperty("write_succeed", true);
                obj.addProperty("fail_cause", "no service: " + type);
            }
        } else {
            obj.addProperty("write_succeed", false);
            obj.addProperty("fail_cause", "no session");
        }

        return obj.toString();
    }

    @PostMapping("/getarticle")
    public Article getArticle(int id, String type) throws IOException {
        // 모든 글을 누구나 열람 가능하다고 가정
        IArticleService service = getService(type);
        LOGGER.info("get article: " + id);

        if(service != null) {
            return service.getArticle(id);
        } else {
            return null;
        }
    }

    @PostMapping("/getarticles")
    public List<Article> getArticles(Integer[] ids, String type) throws IOException {
        LOGGER.info("get articles: " + PrintUtil.toStringArray(ids));

        IArticleService service = getService(type);

        if(service != null) {
            return Arrays.stream(ids).map(service::getArticle).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @PostMapping("/recentarticles")
    public List<Article> recentArticles(int page, int index, String type) throws IOException {
        IArticleService service = getService(type);
        if(service != null) {
            List<Article> articles = service.subArticle(page, index);

            return articles;
        } else {
            return Collections.emptyList();
        }
    }
}
