package dk.digitalidentity.os2skoledata.dao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.Setter;

@Audited
@Entity
@Table(name = "student_groupid")
@Getter
@Setter
public class DBStudentGroupId {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id")
	private DBStudent student;

	@Column
	private String groupId;
}
