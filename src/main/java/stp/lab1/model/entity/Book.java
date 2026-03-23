package stp.lab1.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import stp.lab1.model.enums.BookGenre;
import stp.lab1.model.enums.BookStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "publish_year")
    private Integer publishYear;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "genre")
    private BookGenre genre;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private BookStatus status = BookStatus.Available;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
}