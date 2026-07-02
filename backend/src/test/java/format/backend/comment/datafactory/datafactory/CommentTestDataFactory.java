package format.backend.comment.datafactory.datafactory;

import format.backend.comment.entity.CommentEntity;

public abstract class CommentTestDataFactory {

    public static CommentEntity create(String formId, String content) {
        return create(formId, null, content);
    }

    public static CommentEntity create(String formId, String authorId, String content) {
        var comment = new CommentEntity(formId, content);
        comment.setAuthorId(authorId);

        return comment;
    }
}
