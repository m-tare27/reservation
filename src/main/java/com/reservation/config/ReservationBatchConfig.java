package com.reservation.config;

import com.reservation.entity.Reservation;
import com.reservation.processor.ReservationItemProcessor;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class ReservationBatchConfig {

    @Bean
    public JpaPagingItemReader<Reservation> reservationReader(EntityManagerFactory emf) {
        JpaPagingItemReader<Reservation> reader = new JpaPagingItemReader<>(emf);
        reader.setQueryString(
                "SELECT r FROM Reservation r WHERE r.reservationStatus = 'PENDING'"
        );
        reader.setPageSize(50);
        return reader;
    }

    @Bean
    public ItemProcessor<Reservation, Reservation> reservationProcessor() {
        return new ReservationItemProcessor();
    }

    @Bean
    public JpaItemWriter<Reservation> reservationWriter(EntityManagerFactory emf) {
        return new JpaItemWriter<>(emf);
    }

    @Bean
    public JpaTransactionManager reservationTransactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public Job reservationExpiryJob(JobRepository jobRepository,
                                    Step reservationStep) {
        return new JobBuilder("reservationExpiryJob", jobRepository)
                .start(reservationStep)
                .build();
    }

    @Bean
    public Step reservationStep(JobRepository jobRepository,
                                JpaTransactionManager reservationTransactionManager,
                                JpaPagingItemReader<Reservation> reservationReader,
                                ReservationItemProcessor reservationProcessor,
                                JpaItemWriter<Reservation> reservationWriter) {

        return new StepBuilder("reservationStep", jobRepository)
                .<Reservation, Reservation>chunk(3)
                .transactionManager(reservationTransactionManager)
                .reader(reservationReader)
                .processor(reservationProcessor)
                .writer(reservationWriter)
                .build();
    }
}