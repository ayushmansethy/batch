package com.example.batch;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.autoconfigure.web.ServerProperties.Tomcat.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class ConfigOfBatch {
   @Value("${file.input}")
   String fileName;

   // @Bean
   // public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer explorer,
   //       JobRepository repo, Job firstJob, Job secondJob) {

   //    JobLauncherApplicationRunner jobLauncherApplicationRunner = new JobLauncherApplicationRunner(jobLauncher,
   //          explorer, repo);
   //    jobLauncherApplicationRunner.setJobName("customer");

   //    return jobLauncherApplicationRunner;

   // }

   @Bean
   // @Order(1)
   public Job firstJob(JobRepository repo, JobCompleteNotificationListener listener, Step step1) {
      return new JobBuilder("customer", repo)
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .start(step1)
            .build();

   }

   // @Bean
   // // @Order(2)
   // public Job secondJob(JobRepository repo, JobCompleteNotificationListener listener, Step step1) {
   //    System.out.println("second job runned");
   //    return new JobBuilder("customers", repo)
   //          .incrementer(new RunIdIncrementer())
   //          .listener(listener)
   //          .start(step1)
   //          .build();

   // }

   // ------------------------------------------------------------

   @Bean
   public JpaItemWriter<Customer> writer(EntityManagerFactory factory) {
      return new JpaItemWriterBuilder<Customer>()
            .entityManagerFactory(factory)
            .build();
   }

   // --------------------------------------------------------------------

   // @Bean
   // public JdbcBatchItemWriter<Customer> writer(DataSource datasource) {
   // // System.err.println(fileName);
   // return new JdbcBatchItemWriterBuilder<Customer>()
   // .itemSqlParameterSourceProvider(new
   // BeanPropertyItemSqlParameterSourceProvider<Customer>())
   // .sql("INSERT INTO customer (id,name,age,email,phone) VALUES
   // (:id,:name,:age,:email,:phone)")
   // .dataSource(datasource)
   // .beanMapped()
   // .build();

   // }

   // ----------------------------------------------------------------------------

   // @Bean
   // public FlatFileItemReader<Customer> reader() {
   // return new FlatFileItemReaderBuilder<Customer>().name("customerReader")
   // .resource(new ClassPathResource(fileName))
   // .delimited()
   // .delimiter(",")
   // .names("id", "name", "age", "email", "phone")
   // .linesToSkip(1)
   // .targetType(Customer.class)
   // .build();
   // }

   // ------------------------------------------------

   // @Bean
   // public StaxEventItemReader<Customer> reader(){
   // Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
   // marshaller.setClassesToBeBound(Customer.class);

   // return new StaxEventItemReaderBuilder<Customer>()
   // .name("cutomerReader")
   // .resource(new ClassPathResource(fileName))
   // .addFragmentRootElements("customer")
   // .unmarshaller(marshaller)
   // .build();
   // }

   // ----------------------------------------------

   @Bean
   public JsonItemReader<Customer> reader() throws MalformedURLException {

      return new JsonItemReaderBuilder<Customer>()
            .jsonObjectReader(new JacksonJsonObjectReader<>(Customer.class))
            // if you want absolute path then use new
            // UrlResource("file:///C:\\\\Users\\\\ayush\\\\OneDrive\\\\Desktop\\\\batch\\\\src\\\\main\\\\resources\\\\customer_details.json")
            .resource(new ClassPathResource(fileName))
            .name("customerReader")
            .build();
   }

   @Bean
   public ItemProcessor<Customer, Customer> processor() {

      return Customer -> Customer;
   }

   @Bean
   public Step step1(JobRepository repo, PlatformTransactionManager transactionManager,
         JpaItemWriter<Customer> writer) throws MalformedURLException {
      return new StepBuilder("step1", repo)
            .<Customer, Customer>chunk(10, transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build();

   }

}
