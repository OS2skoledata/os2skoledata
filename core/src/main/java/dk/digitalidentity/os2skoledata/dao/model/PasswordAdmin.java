package dk.digitalidentity.os2skoledata.dao.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
@Entity
public class PasswordAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String username;

	@Column
	private boolean createdByClaim;

    @OneToMany
    @JoinTable(name = "password_admin_institutions", joinColumns = @JoinColumn(name = "password_admin_id"), inverseJoinColumns = @JoinColumn(name = "institution_id"))
    private List<DBInstitution> institutions;
}
