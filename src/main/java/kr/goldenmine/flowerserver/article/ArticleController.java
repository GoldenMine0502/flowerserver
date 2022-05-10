package kr.goldenmine.flowerserver.article;

import com.google.gson.JsonObject;
import kr.goldenmine.flowerserver.file.StorageService;
import kr.goldenmine.flowerserver.profile.Profile;
import kr.goldenmine.flowerserver.profile.ProfileController;
import kr.goldenmine.flowerserver.utils.PrintUtil;
import kr.goldenmine.flowerserver.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/article")
public class ArticleController {
    public static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;
    private final QuestionService questionService;
    private final CommentService commentService;
    private final StorageService storageService;

    public ArticleController(ArticleService articleService, QuestionService questionService, CommentService commentService, StorageService storageService) {
        this.articleService = articleService;
        this.questionService = questionService;
        this.commentService = commentService;
        this.storageService = storageService;
    }

    private IArticleService getService(String type) {
        switch (type) {
            case "article": return articleService;
            case "question": return questionService;
        }

        return null;
    }

    @PostMapping("/writearticle")
    public String writeArticle(String title, String context, String type, MultipartFile[] images, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession();
        Profile profile = (Profile) session.getAttribute("profile");

        IArticleService service = getService(type);

        // 글 쓰기 결과 리턴
        JsonObject obj = new JsonObject();

        if(service != null) {
            if (profile != null) {

                Article article = new Article(0, profile.getId(), title, context, images.length, new LinkedList<>());
                int id = articleService.writeArticle(profile, article);

                articleService.save();

                try {
                    storageService.saveImages(type, id, images);
                    article = new Article(id, profile.getId(), title, context, images.length, new LinkedList<>());

                    obj.addProperty("write_succeed", true);
                    obj.addProperty("fail_cause", "none");

                    LOGGER.info("write article succeed: " + article);
                } catch(IOException ex) {
                    ex.printStackTrace();
                    obj.addProperty("write_succeed", false);
                    obj.addProperty("fail_cause", "fail_image_upload");
                    LOGGER.info("write article fail cause: fail_image_upload");
                }
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
    public String writeComment(int articleId, int parentIndex, @RequestParam("comment") String commentStr, String type, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Profile profile = (Profile) session.getAttribute("profile");

        IArticleService service = getService(type);

        // 글 쓰기 결과 리턴
        JsonObject obj = new JsonObject();

        if(profile != null) {
            if(service != null) {
                Article article = service.getArticle(articleId);

                if(article != null) {
                    boolean isInserted = parentIndex != -1;
                    Comment comment = new Comment(0, profile.getId(), commentStr, 0, isInserted);

                    commentService.writeComment(article, parentIndex, comment);

                    commentService.save();

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

        obj.addProperty("timestamp", TimeUtil.getTimeStamp());

        return obj.toString();
    }

    //    public int getRootParentComment(int start) {
//        for(int i = start; i >= 0; i--) {
//            if(!comments.get(i).isInserted()) return i;
//        }
//
//        return -1;
//    }
//
//    public int getLastInsertedComment(int start) {
//        for(int i = start + 1; i < comments.size(); i++) {
//            if(!comments.get(i).isInserted()) return i - 1;
//        }
//
//        return comments.size();
//    }

    @PostMapping("/getarticle")
    public Article getArticle(int id, String type) throws IOException {
        // 모든 글을 누구나 열람 가능하다고 가정
        IArticleService service = getService(type);
        LOGGER.info("get article: " + id);

        if(service != null) {
            Article article = service.getArticle(id);
            LOGGER.info("article: " + article);
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

        LOGGER.info("recent articles: (" + page * index + " ~ " + (page * index + page) + ")");
        if(service != null) {
            List<Article> articles = service.subArticle(page, index);

            return articles;
        } else {
            return Collections.emptyList();
        }
    }

    @PostMapping("/getcomment")
    public Comment getComment(int id) throws IOException {
        // 모든 글을 누구나 열람 가능하다고 가정

        LOGGER.info("get comment: " + id);

        return commentService.getCommant(id);
    }

    @PostMapping("/getcomments")
    public List<Comment> getComments(Integer[] ids) throws IOException {
        // 모든 글을 누구나 열람 가능하다고 가정

        LOGGER.info("get comment: " + PrintUtil.toStringArray(ids));

        return Arrays.stream(ids).map(commentService::getCommant).collect(Collectors.toList());
    }
}