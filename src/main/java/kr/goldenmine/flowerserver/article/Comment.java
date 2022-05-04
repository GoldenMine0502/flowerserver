package kr.goldenmine.flowerserver.article;

public class Comment {
    private int parentId;
    private String authorId;
    private String comment;
    private int plus;
    private boolean inserted;

    public Comment(int parentId, String authorId, String comment, int plus, boolean inserted) {
        this.parentId = parentId;
        this.authorId = authorId;
        this.comment = comment;
        this.plus = plus;
        this.inserted = inserted;
    }

    public int getParentId() {
        return parentId;
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
