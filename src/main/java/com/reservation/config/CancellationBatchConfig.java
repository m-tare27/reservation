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
    public JpaPagingItemReader<Cancellation> reader(EntityManagerFactory emf){
        JpaPagingItemReader<Cancellation> reader = new JpaPagingItemReader<>(emf);
        reader.setQueryString(
                "SELECT c FROM Cancellation c WHERE c.refundStatus = 'PENDING'"
        );
        reader.setPageSize(50);
        return reader;
    }

    @Bean
    public ItemProcessor<Cancellation,Cancellation> processor(){
        return new CancellationItemProcessor();
    }

    @Bean
    public JpaItemWriter<Cancellation> writer(EntityManagerFactory emf){
        return new JpaItemWriter<>(emf);
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public Job refundReconciliation(JobRepository jobRepository , Step step1){
        return new JobBuilder(jobRepository)
                .start(step1)
                .build();
    }


    @Bean
    public Step step1(JobRepository jobRepository, JpaTransactionManager transactionManager,
                      JpaPagingItemReader<Cancellation> reader, CancellationItemProcessor processor, JpaItemWriter<Cancellation> writer) {
        return new StepBuilder(jobRepository)
                .<Cancellation, Cancellation>chunk(3)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
