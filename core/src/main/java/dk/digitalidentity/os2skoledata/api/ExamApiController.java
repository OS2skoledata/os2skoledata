package dk.digitalidentity.os2skoledata.api;

import dk.digitalidentity.os2skoledata.dao.model.DBPerson;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import dk.digitalidentity.os2skoledata.service.GroupService;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class ExamApiController {

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private GroupService groupService;

	public record ExamCompletePackage(List<ExamInstitutionRecord> institutions, List<ExamClassRecord> studentClasses, List<ExamCourseRecord> courses, List<ExamStudentRecord> students) {}
	public record ExamInstitutionRecord(String name, String institutionNumber) {}
	public record ExamClassRecord(String name, String classNumber, String institutionNumber) {}
	public record ExamCourseRecord(String name, String courseNumber, String institutionNumber) {}
	public record ExamStudentRecord(String name, String studentNumber, String studentClassNumber, List<String> courseNumber) {}

	@GetMapping("/api/examSync")
	public ResponseEntity<?> examSync() {
		List<ExamInstitutionRecord> institutions = institutionService.findAll().stream()
				.map(e -> new ExamInstitutionRecord(
						e.getInstitutionName(),
						e.getInstitutionNumber()
				)).toList();
		List<ExamClassRecord> studentClasses = groupService.findAll().stream()
				.filter(g -> g.getGroupType().equals(DBImportGroupType.HOVEDGRUPPE))
				.map(e -> new ExamClassRecord(
						e.getGroupName(),
						e.getGroupId(),
						e.getInstitution().getInstitutionNumber()
				)).toList();
		List<ExamCourseRecord> courses = groupService.findAll().stream()
				.filter(g -> !g.getGroupType().equals(DBImportGroupType.HOVEDGRUPPE))
				.map(e -> new ExamCourseRecord(
						e.getGroupName(),
						e.getGroupId(),
						e.getInstitution().getInstitutionNumber()
				)).toList();
		List<ExamStudentRecord> students = institutionPersonService.findAllNotDeleted().stream()
				.filter(e -> e.getStudent() != null)
				.map(e -> new ExamStudentRecord(
						DBPerson.getName(e.getPerson()),
						e.getStudent().getStudentNumber(),
						e.getStudent().getMainGroupId(),
						e.getStudent().getGroupIds().stream().map(g -> g.getGroupId()).toList()
				)).toList();
		return ResponseEntity.ok(new ExamCompletePackage(institutions, studentClasses, courses, students));
	}
}
