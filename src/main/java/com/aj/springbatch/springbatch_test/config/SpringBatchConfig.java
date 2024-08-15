package com.aj.springbatch.springbatch_test.config;

import com.aj.springbatch.springbatch_test.entity.Customer;
import com.aj.springbatch.springbatch_test.repository.CustomerRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.Executor;

@Configuration
@EnableBatchProcessing

public class SpringBatchConfig {


    private final CustomerRepository customerRepository;

    public SpringBatchConfig(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Bean
    public FlatFileItemReader<Customer> read() {

        FlatFileItemReader<Customer> itermReader = new FlatFileItemReader<>();
        itermReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
        itermReader.setName("CSV-READER");
        itermReader.setLinesToSkip(1);
        itermReader.setLineMapper(setLineManager());

        return itermReader;

    }

    private LineMapper<Customer> setLineManager() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "userId", "firstName", "lastName", "sex", "email", "phone", "dateOfBirth", "title");

        BeanWrapperFieldSetMapper<Customer> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(mapper);
        return lineMapper;
    }

    @Bean
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }


    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {

        return new StepBuilder("csv-reader", jobRepository)
                .<Customer, Customer>chunk(10, platformTransactionManager)
                .reader(read())
                .processor(processor())
                .writer(writer())
                .taskExecutor(asyncThreadProcessor())
                .build();


    }

    private TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }


    private TaskExecutor asyncThreadProcessor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(150);
        executor.setThreadNamePrefix("TestAJ");
        executor.initialize();
        return executor;
    }


    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new JobBuilder("csv-load-job", jobRepository).flow(step1(jobRepository, platformTransactionManager)).end().build();

    }
}
