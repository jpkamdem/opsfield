package com.konnro.opsfield.teams;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
  private final TeamService teamService;

  public TeamController(TeamService teamService) {
    this.teamService = teamService;
  }

  @GetMapping("/")
  public ResponseEntity<?> index() {
    List<Team> teams = teamService.index();
    return new ResponseEntity<>(teams, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> show(@PathVariable UUID id) throws NoSuchElementException {
    Team team = teamService.show(id);
    return new ResponseEntity<>(team, HttpStatus.OK);
  }

  @PostMapping("/")
  public ResponseEntity<?> store(@RequestBody @Valid Team team) throws NoSuchElementException {
    Team newTeam = teamService.store(team);
    return new ResponseEntity<>(newTeam, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody @Valid Team team) throws NoSuchElementException {
    teamService.update(team, id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable UUID id) throws NoSuchElementException {
    teamService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}