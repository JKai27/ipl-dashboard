package io.javabrains.ipldashboard.service;

import io.javabrains.ipldashboard.model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class TeamService {
    private static final Logger log = LoggerFactory.getLogger(TeamService.class);
    private final JdbcTemplate jdbcTemplate;

    public TeamService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void saveTeams(Collection<Team> teams) {
        log.info("🔵 Saving teams to database...");

        String checkSql = "SELECT COUNT(*) FROM team WHERE team_name = ?";
        String insertSql = "INSERT INTO team (team_name, total_matches, total_wins) VALUES (?, ?, ?)";
        String updateSql = "UPDATE team SET total_matches = ?, total_wins = ? WHERE team_name = ?";

        teams.forEach(team -> {
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, team.getTeamName());
            count = count == null ? 0 : count;

            if (count > 0) {
                // Team already exists → UPDATE
                log.info("📌 Updating Team: {}, Matches: {}, Wins: {}",
                        team.getTeamName(), team.getTotalMatches(), team.getTotalWins());

                jdbcTemplate.update(updateSql, team.getTotalMatches(), team.getTotalWins(), team.getTeamName());

            } else {
                // New team → INSERT
                log.info("📌 Inserting New Team: {}, Matches: {}, Wins: {}",
                        team.getTeamName(), team.getTotalMatches(), team.getTotalWins());

                jdbcTemplate.update(insertSql, team.getTeamName(), team.getTotalMatches(), team.getTotalWins());
            }
        });

        log.info("✅ Teams saved successfully");
    }

}