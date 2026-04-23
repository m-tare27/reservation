package com.reservation.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class JobScheduler{
    private final static Logger log = LoggerFactory.getLogger(JobScheduler.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final Job job1;
    private final Job job2;
    private final JobOperator jobOperator;

    public JobScheduler(@Qualifier("cancellationRefundReconciliationJob") Job job1, @Qualifier("reservationExpiryJob") Job job2, JobOperator jobOperator){

        this.job1 = job1;
        this.job2 = job2;
        this.jobOperator = jobOperator;
    }

    @Scheduled(cron = "*/10 * * * * ?")
    public void scheduleCancellationRefundReconciliationJob() throws JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("time",System.currentTimeMillis())
                        .toJobParameters();

        jobOperator.start(job1, parameters);
    }

}