package dk.digitalidentity.os2skoledata.dao.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
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
