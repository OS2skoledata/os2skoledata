package dk.digitalidentity.os2skoledata.dao.model;

import dk.digitalidentity.os2skoledata.dao.model.enums.ClassroomAction;
import dk.digitalidentity.os2skoledata.dao.model.enums.ClassroomActionStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ClassroomActionQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime created;

    @Column
    private LocalDateTime performed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassroomAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassroomActionStatus status = ClassroomActionStatus.WAITING;

    @Column
    private String username;

    @Column(nullable = false)
    private String courseId;

    @Column
    private String errorMessage;

    @Column(nullable = false)
    private String requestedByUsername;
}
