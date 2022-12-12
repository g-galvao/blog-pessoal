package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Tema;
import com.generation.blogpessoal.repository.TemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping ("temas")
@CrossOrigin (origins = "*", allowedHeaders = "*")
public class TemaController {

    @Autowired
    private TemaRepository temaRepository;

    @GetMapping
    public ResponseEntity<List<Tema>> getAll() {

        return ResponseEntity.ok(temaRepository.findAll());

    }

    @GetMapping ("/{id}")
    public ResponseEntity<Tema> getById(@PathVariable Long id) {

        return temaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    @GetMapping ("/descricao/{descricao}")
    public ResponseEntity<List<Tema>> getByTitle(@PathVariable String descricao) {
        return ResponseEntity.ok(temaRepository
                .findAllByDescricaoContainingIgnoreCase(descricao));
    }

    @PostMapping
    public ResponseEntity<Tema> post(@Valid @RequestBody Tema tema) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(temaRepository.save(tema));

    }

    @PutMapping
    public ResponseEntity<Tema> put(@Valid @RequestBody Tema tema) {

        return temaRepository.findById(tema.getId())
                .map(resposta -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(temaRepository.save(tema)))
                        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {

        Optional<Tema> tema = temaRepository.findById(id);

        if (tema.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        temaRepository.deleteById(id);

    }
}
