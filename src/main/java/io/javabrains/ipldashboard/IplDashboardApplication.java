package io.javabrains.ipldashboard;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class IplDashboardApplication implements CommandLineRunner {

	private final JobLauncher jobLauncher;
	private final Job importUserJob;

	public IplDashboardApplication(JobLauncher jobLauncher, Job importUserJob) {
		this.jobLauncher = jobLauncher;
		this.importUserJob = importUserJob;
	}

	public static void main(String[] args) {
		SpringApplication.run(IplDashboardApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(">>> Starting Batch Job <<<");
		JobExecution execution = jobLauncher.run(importUserJob, new JobParameters());
		System.out.println(">>> Job Status: " + execution.getStatus());
	}
}