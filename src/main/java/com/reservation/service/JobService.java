package com.reservation.service;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class JobService {

    private final Job job1;
    private final Job job2;
    private final JobOperator jobOperator;

    public JobService(@Qualifier("cancellationRefundReconciliationJob") Job job1, @Qualifier("reservationExpiryJob") Job job2, JobOperator jobOperator){
        this.job1 = job1;
        this.job2 = job2;
        this.jobOperator = jobOperator;
    }

    public void executeReservationExpiryJob() throws JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("time",System.currentTimeMillis())
                .toJobParameters();

        jobOperator.start(job2, parameters);
    }

    public void executeCancellationRefundReconciliationJob() throws JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("time",System.currentTimeMillis())
                .toJobParameters();

        jobOperator.start(job1, parameters);
    }
}
