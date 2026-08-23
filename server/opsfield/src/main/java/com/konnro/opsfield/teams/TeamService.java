package com.konnro.opsfield.teams;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.konnro.opsfield.errors.TeamNotFoundException;

@Service
public class TeamService {

  private final TeamRepository teamRepository;

  public TeamService(TeamRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  public List<Team> index() {
    List<Team> teams = teamRepository.findAll();
    return teams;
  }

  public Team show(UUID id) {
    Team team = teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
    return team;
  }

  public Team store(Team newTeam) {
    Team team = new Team();
    team.setName(newTeam.getName());
    teamRepository.save(team);
    return team;
  }

  public void update(Team team, UUID id) {
    Team foundTeam = teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
    foundTeam.setName(team.getName());
    teamRepository.save(foundTeam);
  }

  public void delete(UUID id) {
    teamRepository.deleteById(id);
  }
}