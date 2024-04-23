package ru.practicum.ewm.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder(toBuilder = true)
@Entity
@Table(name = "categories", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    // идентификатор категории:
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    // название категории:
    @Column(name = "name")
    private String name;
}