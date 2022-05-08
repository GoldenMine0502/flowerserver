package kr.goldenmine.flowerserver.article;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    private final Gson gson = new Gson();
    private final File saveFile = new File("comments.json");

    private final List<Comment> comments = new ArrayList<>();
    private final Type commentsType = new TypeToken<List<Comment>>() {}.getType();
    private final Object commentsKey = new Object();

    public CommentService() {
        load();
    }

    public int writeComment(Article article, int parentIndex, Comment comment) {
        // 지금까지 쓰인 모든 글의 갯수가 곧 쓰일 글의 id

        int articleId;
        int index;

        synchronized (commentsKey) {
            int currentArticleId = comments.size();
            Comment idComment = new Comment(currentArticleId, comment.getAuthorId(), comment.getComment(), comment.getPlus(), comment.isInserted());

            comments.add(idComment);

            articleId = currentArticleId;

            index = parentIndex != -1 ? getLastInsertedCommentIndex(article, parentIndex) : article.getCommentIds().size();
        }

        article.addNewCommentId(index, articleId);

        return articleId;
    }

    public int getLastInsertedCommentIndex(Article article, int start) {
        List<Integer> ids = article.getCommentIds();
        for (int i = start + 1; i < ids.size(); i++) {
            if (!comments.get(ids.get(i)).isInserted()) return i - 1;
        }

        return ids.size();
    }

    public Comment getCommant(int id) {
        if(id < comments.size()) {
            return comments.get(id);
        } else {
            return null;
        }
    }

    public void load() {
        if(saveFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
                synchronized (commentsKey) {
                    List<Comment> articles = gson.fromJson(reader, commentsType);
                    this.comments.clear();
                    this.comments.addAll(articles);
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
            synchronized (commentsKey) {
                gson.toJson(comments, writer);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }


}
