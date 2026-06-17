package dk.digitalidentity.os2skoledata.dao.model;

import dk.digitalidentity.os2skoledata.dao.model.enums.RoleSettingType;
import dk.digitalidentity.os2skoledata.dao.model.enums.StudentPasswordChangerSTILRoles;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Getter
@Setter
@Entity
public class StudentPasswordChangeConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentPasswordChangerSTILRoles role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleSettingType type;

    @Column(nullable = true)
    private String filter;
}
