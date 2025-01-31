package io.javabrains.ipldashboard.data;

import io.javabrains.ipldashboard.model.Team;
import io.javabrains.ipldashboard.service.TeamService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private final EntityManager em;
    private final TeamService teamService;

    public JobCompletionNotificationListener(EntityManager em, TeamService teamService) {
        this.em = em;
        this.teamService = teamService;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("**** Job completed **** It's time to verify the results!");

            Map<String, Team> teamData = new HashMap<>();

            // Count matches played by each team
            em.createQuery("select m.team1, count(*) from Match m group by m.team1", Object[].class).getResultList().stream().map(e -> new Team((String) e[0], (long) e[1])).forEach(team -> teamData.put(team.getTeamName(), team));

            em.createQuery("select m.team2, count(*) from Match m group by m.team2", Object[].class).getResultList().forEach(e -> {
                Team team = teamData.get((String) e[0]);
                if (team != null) {
                    team.setTotalMatches(team.getTotalMatches() + (long) e[1]);
                }
            });

            // Count wins for each team
            em.createQuery("select m.matchWinner, count(*) from Match m group by m.matchWinner", Object[].class)
                    .getResultList()
                    .forEach(e -> {
                        Team team = teamData.get((String) e[0]);
                        if (team != null) {
                            team.setTotalWins((long) e[1]);
                        }
                    });

            // ✅ Save teams using `TeamService`
            log.info("Saving teams to database...");

            log.info("🔵 Teams prepared for persistence: {}", teamData.size());
            teamData.values().forEach(team -> log.info("Persisting Team: {}, Matches: {}, Wins: {}", team.getTeamName(), team.getTotalMatches(), team.getTotalWins()));
            teamService.saveTeams(teamData.values());
            log.info("✅ Successfully persisted all teams!");
        }
    }
}