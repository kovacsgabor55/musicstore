package org.training360.musicstore;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/instruments")
public class MusicController {

    private final MusicService musicService;

    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping
    public List<InstrumentDTO> searchInstrument(@RequestParam Optional<String> brand, @RequestParam Optional<Integer> price) {
        return musicService.searchInstrument(brand, price);
    }

    @PostMapping
    public InstrumentDTO addInstrument(@Valid @RequestBody CreateInstrumentCommand command) {
        return musicService.addInstrument(command);
    }

    @DeleteMapping
    public void deleteAllInstrument() {
        musicService.deleteAllInstrument();
    }

    @GetMapping("/{id}")
    public InstrumentDTO searchInstrumentById(@PathVariable long id) {
        return musicService.findInstrumentById(id);
    }

    @PutMapping("/{id}")
    public InstrumentDTO updateInstrumentPrice(@PathVariable long id, @Valid @RequestBody UpdatePriceCommand command) {
        return musicService.updateInstrumentPrice(id, command);
    }

    @DeleteMapping("/{id}")
    public void deleteInstrumentById(@PathVariable long id) {
        musicService.deleteInstrumentById(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Problem> handleNotFound(IllegalArgumentException iae) {
        Problem problem =
                Problem.builder()
                        .withType(URI.create("instruments/not-found"))
                        .withTitle("Not found")
                        .withStatus(Status.NOT_FOUND)
                        .withDetail(iae.getMessage())
                        .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
