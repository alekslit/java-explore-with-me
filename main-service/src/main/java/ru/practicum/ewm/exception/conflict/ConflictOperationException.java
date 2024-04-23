package ru.practicum.ewm.exception.conflict;

public class ConflictOperationException extends CustomConflictException {
    public static final String CONFLICT_CATEGORY_DELETE_MESSAGE = "Невозможно удалить категорию с таким id. id = ";
    public static final String CONFLICT_CATEGORY_DELETE_ADVICE = "Категорию можно удалить, только если с ней " +
            "не связано ни одного события.";
    public static final String CONFLICT_SAVE_PARTICIPATION_REQUEST_MESSAGE = "Невозможно создать запрос на " +
            "участие в событии.";
    public static final String USER_IS_OWNER_ADVICE = "Инициатор события не может добавить запрос на участие" +
            " в своём событии.";
    public static final String EVENT_IS_NOT_PUBLISHED_ADVICE = "Нельзя участвовать в неопубликованном событии.";
    public static final String MAX_EVENT_PARTICIPANT_COUNT_ADVICE = "Количество участников события достигло максимума.";
    public static final String CONFLICT_UPDATE_EVENT_MESSAGE = "Невозможно обновить событие. eventId = ";
    public static final String LOW_PARTICIPANT_LIMIT_ADVICE = "Новый лимит участников, меньше количества " +
            "уже подписавшихся участников.";
    public static final String PUBLISHED_CONFLICT_ADVICE = "Обратите внимание: " +
            "1. Дата начала изменяемого события должна быть не ранее чем за час от даты публикации;" +
            "2. Событие можно публиковать, только если оно в состоянии ожидания публикации;";
    public static final String CANCELED_CONFLICT_ADVICE = "Событие можно отклонить, только если оно " +
            "еще не опубликовано.";
    public static final String EVENT_STATUS_CONFLICT_ADVICE = "Изменить можно только отмененные события или " +
            "события в состоянии ожидания модерации";
    public static final String CONFLICT_UPDATE_PARTICIPATION_REQUEST_MESSAGE = "Невозможно изменить статус заявок " +
            "на участие. requestIds = ";
    public static final String REQUEST_STATUS_CONFLICT_ADVICE = "Статус можно изменить только у заявок, " +
            "находящихся в состоянии ожидания.";
    public static final String PARTICIPANT_COUNT_IS_MAX_ADVICE = "Нельзя подтвердить заявку, достигнут лимит " +
            "по заявкам на данное событие.";
    public static final String UPDATE_COMMENT_CONFLICT_MESSAGE = "Невозможно изменить комментарий. commentId = ";
    public static final String COMMENT_DATE_TIME_CONFLICT_ADVICE = "Комментарий можно изменять только в течении " +
            "24-ёх часов с момента его создания.";
    public static final String COMMENT_OWNER_CONFLICT_ADVICE = "Комментарий может изменять только его автор.";
    public static final String COMPLAINT_REASON_CONFLICT_MESSAGE = "Невозможно отправить жалобу на комментарий " +
            "с такой причиной. reason = ";
    public static final String COMPLAINT_REASON_CONFLICT_ADVICE = "Причина жалоба должна иметь одно из значений: " +
            "OBSCENE, ABUSE, SPAM, OTHER.";
    public static final String COMPLAINT_AUTHOR_CONFLICT_MESSAGE = "Невозможно отправить жалобу на комментарий " +
            "пользователю с таким id. userId = ";
    public static final String COMPLAINT_AUTHOR_CONFLICT_ADVICE = "Автор комментария не может отправить жалобу " +
            "на свой комментарий.";
    public static final String COMMENT_EVENT_STATUS_CONFLICT_MESSAGE = "Невозможно оставить комментарий этому " +
            "событию. eventId = ";
    public static final String COMMENT_EVENT_STATUS_CONFLICT_ADVICE = "Оставлять комментарии можно только " +
            "у опубликованных (PUBLISHED) событий.";

    public ConflictOperationException(String message, String adviceToUser) {
        super(message, adviceToUser);
    }
}