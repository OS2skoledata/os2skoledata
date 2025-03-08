package dk.digitalidentity.os2skoledata.controller.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import dk.digitalidentity.os2skoledata.dao.model.Ghost;
import dk.digitalidentity.os2skoledata.service.GhostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
public class GhostRestController {

	@Autowired
	private GhostService ghostService;

	record GhostRecord(long id, String username, @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") LocalDate activeUntil) {}

	@PostMapping("/rest/ghosts/create")
	public ResponseEntity<?> createGhost(@Valid @RequestBody GhostRecord body) {
		if (body.username == null || body.activeUntil == null) {
			return ResponseEntity.badRequest().body("Begge felter skal udfyldes");
		}

		Ghost ghost = ghostService.findByUsername(body.username);

		if (ghost != null) {
			return ResponseEntity.badRequest().body("Der findes allerede en bruger med forlænget levetid med dette brugernavn.");
		}

		ghost = new Ghost();
		ghost.setUsername(body.username);
		ghost.setActiveUntil(body.activeUntil);

		ghostService.save(ghost);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/rest/ghosts/delete")
	public ResponseEntity<?> deleteGhost(@Valid @RequestBody GhostRecord body) {
		Ghost ghost = ghostService.findById(body.id);

		if (ghost != null) {
			ghostService.delete(ghost);
		}
		else {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok().build();
	}
}
