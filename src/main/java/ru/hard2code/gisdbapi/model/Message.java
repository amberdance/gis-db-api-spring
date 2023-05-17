package ru.hard2code.gisdbapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.hard2code.gisdbapi.model.user.User;

import java.util.Objects;

@Entity
@Table(name = "messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Message extends AbstractEntity {

    @Column(name = "label", nullable = false, length = 999)
    @NotNull
    private String label;

    @Column(name = "answer")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String answer;


    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH,
            CascadeType.MERGE})
    @NotNull
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Message message = (Message) o;
        return getId() != null && Objects.equals(getId(), message.getId());
    }


}
