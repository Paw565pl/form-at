package format.backend.comment.datafactory.datafactory;

import format.backend.comment.entity.CommentEntity;

public abstract class CommentTestDataFactory {

    public static CommentEntity create(String formId) {
        return create(formId, null);
    }

    public static CommentEntity create(String formId, String authorId) {
        var comment = new CommentEntity(formId, "comment");
        comment.setAuthorId(authorId);

        return comment;
    }
}
