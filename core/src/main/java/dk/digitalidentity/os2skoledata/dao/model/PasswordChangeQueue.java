package dk.digitalidentity.os2skoledata.dao.model;

import dk.digitalidentity.os2skoledata.dao.model.enums.ReplicationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "password_change_queue")
@Getter
@Setter
@NoArgsConstructor
public class PasswordChangeQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    @CreationTimestamp
    private LocalDateTime tts;

    @Column
    @Enumerated(EnumType.STRING)
    private ReplicationStatus status;

    @Column
    private String message;

    public PasswordChangeQueue(String username, String newPassword) {
    	this.password = newPassword;
    	this.username = username;
    	this.status = ReplicationStatus.WAITING_FOR_REPLICATION;
    }
}
