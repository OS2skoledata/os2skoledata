package dk.digitalidentity.os2skoledata.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.Setter;

@Audited
@Entity
@Table(name = "extern_groupid")
@Getter
@Setter
public class DBExternGroupId {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@BatchSize(size = 100)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "extern_id")
	private DBExtern extern;

	@Column
	private String groupId;
}
