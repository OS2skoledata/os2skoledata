package dk.digitalidentity.os2skoledata.api;

import dk.digitalidentity.os2skoledata.dao.model.DBPerson;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import dk.digitalidentity.os2skoledata.service.GroupService;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public record ExamClassRecord(String name, String classNumber, String level, String institutionNumber) {}
	public record ExamCourseRecord(String name, String courseNumber, String level, String institutionNumber) {}
	public record ExamStudentRecord(String name, String studentNumber, String studentClassNumber, String level, List<String> courseNumber) {}
	public record LevelRequest(List<String> levels) {}

	@PostMapping("/api/examSync")
	public ResponseEntity<?> examSync(@RequestBody LevelRequest requestedLevels) {
		List<ExamInstitutionRecord> institutions = institutionService.findAll().stream()
				.filter(e -> e.getType().equals(InstitutionType.SCHOOL))
				.filter(e -> e.getGroups().stream().anyMatch(g -> requestedLevels.levels.contains(g.getGroupLevel())))
				.map(e -> new ExamInstitutionRecord(
						e.getInstitutionName(),
						e.getInstitutionNumber()
				)).toList();
		List<ExamClassRecord> studentClasses = groupService.findAll().stream()
				.filter(g -> g.getGroupType().equals(DBImportGroupType.HOVEDGRUPPE))
				.filter(g -> requestedLevels.levels.contains(g.getGroupLevel()))
				.map(e -> new ExamClassRecord(
						e.getGroupName(),
						e.getGroupId(),
						e.getGroupLevel(),
						e.getInstitution().getInstitutionNumber()
				)).toList();
		List<ExamCourseRecord> courses = groupService.findAll().stream()
				.filter(g -> !g.getGroupType().equals(DBImportGroupType.HOVEDGRUPPE))
				.filter(g -> requestedLevels.levels.contains(g.getGroupLevel()))
				.map(e -> new ExamCourseRecord(
						e.getGroupName(),
						e.getGroupId(),
						e.getGroupLevel(),
						e.getInstitution().getInstitutionNumber()
				)).toList();
		List<ExamStudentRecord> students = institutionPersonService.findAllNotDeleted().stream()
				.filter(e -> e.getStudent() != null)
				.filter(e -> requestedLevels.levels.contains(e.getStudent().getLevel()))
				.map(e -> new ExamStudentRecord(
						DBPerson.getName(e.getPerson()),
						e.getStudent().getStudentNumber(),
						e.getStudent().getMainGroupId(),
						e.getStudent().getLevel(),
						e.getStudent().getGroupIds().stream().map(g -> g.getGroupId()).toList()
				)).toList();
		return ResponseEntity.ok(new ExamCompletePackage(institutions, studentClasses, courses, students));
	}
}
