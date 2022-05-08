package kr.goldenmine.flowerserver.article;

public class Comment {
    int id;
    private String authorId;
    private String comment;
    private int plus;
    private boolean inserted;

    public Comment(int id, String authorId, String comment, int plus, boolean inserted) {
        this.id = id;
        this.authorId = authorId;
        this.comment = comment;
        this.plus = plus;
        this.inserted = inserted;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getComment() {
        return comment;
    }

    public int getPlus() {
        return plus;
    }

    public boolean isInserted() {
        return inserted;
    }
}
