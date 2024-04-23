package ru.practicum.ewm.event.comment.complaint;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@ToString
@Builder
public class ComplaintDto {
    // идентификатор:
    private Long id;

    @NotBlank(message = "Причина жалобы (reason) не может быть пустой.")
    @Size(min = 4, max = 10, message = "Слишком длинная или слишком короткая причина жалобы. " +
            "Длинна поля причины жалобы должна быть от {min} до {max} символов.")
    // причина жалобы:
    private String reason;

    // id автора жалобы:
    private Long userId;

    // id комментария на который пожаловались:
    private Long commentId;

    // дата создания жалобы:
    private String creationDate;
}