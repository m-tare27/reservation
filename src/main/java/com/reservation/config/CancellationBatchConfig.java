package com.reservation.config;

import com.reservation.entity.Cancellation;
import com.reservation.processor.CancellationItemProcessor;
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
public class CancellationBatchConfig {

    @Bean
    public JpaPagingItemReader<Cancellation> cancellationReader(EntityManagerFactory emf){
        JpaPagingItemReader<Cancellation> reader = new JpaPagingItemReader<>(emf);
        reader.setQueryString(
                "SELECT c FROM Cancellation c WHERE c.refundStatus = 'PENDING'"
        );
        reader.setPageSize(50);
        return reader;
    }

    @Bean
    public ItemProcessor<Cancellation, Cancellation> cancellationProcessor(){
        return new CancellationItemProcessor();
    }

    @Bean
    public JpaItemWriter<Cancellation> cancellationWriter(EntityManagerFactory emf){
        return new JpaItemWriter<>(emf);
    }

    @Bean
    public JpaTransactionManager cancellationTransactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public Job cancellationRefundReconciliationJob(JobRepository jobRepository,
                                                   Step cancellationStep){
        return new JobBuilder("cancellationRefundReconciliationJob", jobRepository)
                .start(cancellationStep)
                .build();
    }

    @Bean
    public Step cancellationStep(JobRepository jobRepository,
                                 JpaTransactionManager cancellationTransactionManager,
                                 JpaPagingItemReader<Cancellation> cancellationReader,
                                 CancellationItemProcessor cancellationProcessor,
                                 JpaItemWriter<Cancellation> cancellationWriter) {

        return new StepBuilder("cancellationStep", jobRepository)
                .<Cancellation, Cancellation>chunk(3)
                .transactionManager(cancellationTransactionManager)
                .reader(cancellationReader)
                .processor(cancellationProcessor)
                .writer(cancellationWriter)
                .build();
    }
}