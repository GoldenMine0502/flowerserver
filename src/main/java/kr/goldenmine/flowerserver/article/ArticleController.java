package kr.goldenmine.flowerserver.article;

import com.google.gson.JsonObject;
import kr.goldenmine.flowerserver.PrintUtil;
import kr.goldenmine.flowerserver.TimeUtil;
import kr.goldenmine.flowerserver.profile.Profile;
import kr.goldenmine.flowerserver.profile.ProfileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/article")
public class ArticleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/writearticle")
    public String writeArticle(String title, String context, ArrayList<Integer> imageIds, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        Profile profile = (Profile) session.getAttribute("profile");


        // 글 쓰기 결과 리턴
        JsonObject obj = new JsonObject();

        if(profile != null) {
            Article article = new Article(0, profile.getId(), title, context, imageIds);
            int id = articleService.writeArticle(profile, article);
            article = new Article(id, profile.getId(), title, context, imageIds);

            obj.addProperty("write_succeed", true);
            obj.addProperty("fail_cause", "none");

            LOGGER.info("write article succeed: " + article);
        } else {
            obj.addProperty("write_succeed", false);
            obj.addProperty("fail_cause", "no session");
            LOGGER.info("write article failed cause: no session");
        }

        obj.addProperty("timestamp", TimeUtil.getTimeStamp());


        return obj.toString();
    }

    @PostMapping("/getarticle")
    public Article getArticle(int id) throws IOException {
        // 모든 글을 누구나 열람 가능하다고 가정
        LOGGER.info("get article: " + id);
        return articleService.getArticle(id);
    }

    @PostMapping("/getarticles")
    public List<Article> getArticles(Integer[] ids) throws IOException {
        LOGGER.info("get articles: " + PrintUtil.toStringArray(ids));

        List<Article> articles = Arrays.stream(ids).map(articleService::getArticle).collect(Collectors.toList());

        return articles;
    }
}
