package kr.goldenmine.flowerserver.article;

import com.google.gson.JsonObject;
import kr.goldenmine.flowerserver.file.StorageService;
import kr.goldenmine.flowerserver.utils.PrintUtil;
import kr.goldenmine.flowerserver.utils.TimeUtil;
import kr.goldenmine.flowerserver.profile.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/article")
public class ArticleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;
    private final QuestionService questionService;
    private final StorageService storageService;

    public ArticleController(ArticleService articleService, QuestionService questionService, StorageService storageService) {
        this.articleService = articleService;
        this.questionService = questionService;
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

                try {
                    storageService.saveImages(images, id);
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

    @GetMapping("/display/{fileName}")
    public ResponseEntity<Resource> displayImage(@PathVariable String fileName,
                                                 HttpServletRequest request) {
        // Load file as Resource
        Resource resource = null;

        try {
            resource = storageService.loadFileAsResource(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            LOGGER.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
