package ru.practicum.ewm.event.comment.complaint;

// причина жалобы на комментарий:
public enum ComplaintReason {
    // obscene - нецензурная лексика;
    OBSCENE,
    // abuse - оскорбление других участников;
    ABUSE,
    // spam - спам, сторонние ссылки, фейковые аккаунты;
    SPAM,
    // other - другое;
    OTHER
}