package com.reservation.scheduler;

import com.reservation.service.JobService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
@RequiredArgsConstructor
public class JobScheduler{
    private final static Logger log = LoggerFactory.getLogger(JobScheduler.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final JobService service;

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleCancellationRefundReconciliationJob() throws JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        log.info("Initiating scheduled refund reconciliation job");
        service.executeCancellationRefundReconciliationJob();
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void scheduleReservationExpiryJob() throws JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        log.info("Initiating scheduled reservation expiry job");
        service.executeReservationExpiryJob();
    }

}