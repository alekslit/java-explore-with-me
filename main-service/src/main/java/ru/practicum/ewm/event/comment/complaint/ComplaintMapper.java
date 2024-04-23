package ru.practicum.ewm.event.comment.complaint;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.comment.Comment;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ComplaintMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Complaint mapToComplaint(ComplaintDto complaintDto, User user, Comment comment) {
        return Complaint.builder()
                .reason(ComplaintReason.valueOf(complaintDto.getReason()))
                .user(user)
                .comment(comment)
                .creationDate(LocalDateTime.now())
                .build();
    }

    public static ComplaintDto mapToComplaintDto(Complaint complaint) {
        return ComplaintDto.builder()
                .id(complaint.getId())
                .reason(complaint.getReason().toString())
                .userId(complaint.getUser().getId())
                .commentId(complaint.getComment().getId())
                .creationDate(formatter.format(complaint.getCreationDate()))
                .build();
    }

    public static List<ComplaintDto> mapToComplaintDto(List<Complaint> complaints) {
        return complaints.stream()
                .map(ComplaintMapper::mapToComplaintDto)
                .collect(Collectors.toList());
    }
}