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
public class BatchConfig {

    @Bean
    public JpaPagingItemReader<Reservation> reader(EntityManagerFactory emf) {
        JpaPagingItemReader<Reservation> reader = new JpaPagingItemReader<>(emf);
        reader.setQueryString(
                "SELECT r FROM Reservation r WHERE r.reservationStatus = 'PENDING'"
        );
        reader.setPageSize(50);
        return reader;
    }

    @Bean
    public ItemProcessor<Reservation, Reservation> processor() {
        return new ReservationItemProcessor();
    }

    @Bean
    public JpaItemWriter<Reservation> writer(EntityManagerFactory emf) {
        return new JpaItemWriter<>(emf);
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder(jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, JpaTransactionManager transactionManager,
                      JpaPagingItemReader<Reservation> reader, ReservationItemProcessor processor, JpaItemWriter<Reservation> writer) {
        return new StepBuilder(jobRepository)
                .<Reservation, Reservation>chunk(3)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}