package kr.goldenmine.flowerserver.file;

import com.google.gson.JsonObject;
import kr.goldenmine.flowerserver.Response;
import kr.goldenmine.flowerserver.article.Article;
import kr.goldenmine.flowerserver.article.ArticleService;
import kr.goldenmine.flowerserver.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/images")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final ArticleService articleService;
    private final StorageService storageService;

    @Autowired
    public FileController(ArticleService articleService, StorageService storageService) {
        this.articleService = articleService;
        this.storageService = storageService;
    }


//    @PostMapping("/post/upload")
//    public String imageUpload(@RequestParam("files") MultipartFile[] files,
//                                                    @RequestParam("id") int articleId) throws IOException {
//        Article article = articleService.getArticle(articleId);
//
//        JsonObject obj = new JsonObject();
//
//        if(article != null) {
//            int count = files.length;
//
//            try {
//                // 파일 포맷: 글id-이미지번호.jpg
//                storageService.saveImages(files, articleId);
//                obj.addProperty("succeed", true);
//                obj.addProperty("image_counts", count);
//                obj.addProperty("fail_cause", "none");
//            } catch (Exception ex) {
//                obj.addProperty("succeed", false);
//                obj.addProperty("image_counts", count);
//                obj.addProperty("fail_cause", ex.getMessage());
//            }
//        } else {
//            obj.addProperty("succeed", false);
//            obj.addProperty("image_counts", -1);
//            obj.addProperty("fail_cause", "no article");
//        }
//
//        obj.addProperty("timestamp", TimeUtil.getTimeStamp());
//
//        return obj.toString();
//    }

    @GetMapping("/view/{fileName:.+}")
    public ResponseEntity<Resource> viewImage(@PathVariable String fileName,
                                                 HttpServletRequest request) {
        // Load file as Resource
        Resource resource = null;

        try {
            resource = storageService.loadFileAsResource(fileName);
        } catch (FileNotFoundException e) {
//            logger.info("cannot find the resource: " + fileName);
            e.printStackTrace();
        }

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
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