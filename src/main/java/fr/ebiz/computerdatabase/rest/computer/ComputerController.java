package fr.ebiz.computerdatabase.rest.computer;

import fr.ebiz.computerdatabase.dto.ComputerDto;
import fr.ebiz.computerdatabase.dto.GetAllComputersRequest;
import fr.ebiz.computerdatabase.dto.paging.Page;
import fr.ebiz.computerdatabase.service.ComputerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/computers")
public class ComputerController {

    private ComputerService computerService;

    /**
     * Constructor.
     *
     * @param computerService The computer service
     */
    @Autowired
    public ComputerController(ComputerService computerService) {
        this.computerService = computerService;
    }

    /**
     * GET all computers.
     *
     * @param request The request to get computers
     * @return a page of computers
     */
    @GetMapping
    public Page<ComputerDto> readComputers(GetAllComputersRequest request) {
        return this.computerService.getAll(request);
    }

    /**
     * GET a computer by it's id.
     *
     * @param computerId The id of the computer to retrieve
     * @return The computer if it exists
     */
    @GetMapping("/{computerId}")
    public ResponseEntity<ComputerDto> readComputer(@PathVariable int computerId) {
        return this.computerService.get(computerId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST a computer.
     *
     * @param computer The computer to create
     * @return The http response
     */
    @PostMapping
    public ResponseEntity<?> postComputer(@RequestBody ComputerDto computer) {
        computerService.insert(computer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(computer.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * PUT a computer.
     *
     * @param computerId The id of the computer to update
     * @param computer   The computer to update
     * @return The http response
     */
    @PutMapping("/{computerId}")
    public ResponseEntity<?> putComputer(@PathVariable int computerId, @RequestBody ComputerDto computer) {
        return computerService.get(computerId).map(c -> {
            computerService.update(computer);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * DELETE a computer.
     *
     * @param computerId The id of the computer to delete
     * @return The http response
     */
    @DeleteMapping("/{computerId}")
    public ResponseEntity<ComputerDto> deleteComputer(@PathVariable int computerId) {
        return computerService.get(computerId).map(c -> {
            computerService.delete(computerId);
            return ResponseEntity.ok(c);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
