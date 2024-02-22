package com.example.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
@Configuration
public class ConfigOfBatch {
   @Value("${file.input}")
   String fileName;

  

   @Bean
   public Job firstJob(JobRepository repo, JobCompleteNotificationListener listener, Step step1) {
      return new JobBuilder("customer", repo)
      .incrementer(new RunIdIncrementer())
            .listener(listener)
            .start(step1)
            .build();

   }

   @Bean
   public JdbcBatchItemWriter<Customer> writer(DataSource datasource) {
      // System.err.println(fileName);
      return new JdbcBatchItemWriterBuilder<Customer>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Customer>())
            .sql("INSERT INTO customer (id,name,age,email,phone) VALUES (:id,:name,:age,:email,:phone)")
            .dataSource(datasource)
            .beanMapped()
            .build();

   }

   // @Bean
   // public FlatFileItemReader<Customer> reader() {
   //    return new FlatFileItemReaderBuilder<Customer>().name("customerReader")
   //          .resource(new ClassPathResource(fileName))
   //          .delimited()
   //          .delimiter(",")
   //          .names("id", "name", "age", "email", "phone")
   //          .linesToSkip(1)
   //          .targetType(Customer.class)
   //          .build();
   // }

   //------------------------------------------------


   //    @Bean
   // public StaxEventItemReader<Customer> reader(){
   //    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
   //    marshaller.setClassesToBeBound(Customer.class);
      
   //    return new StaxEventItemReaderBuilder<Customer>()
   //    .name("cutomerReader")
   //    .resource(new ClassPathResource(fileName))
   //    .addFragmentRootElements("customer")
   //    .unmarshaller(marshaller)
   //    .build();
   // }



   //----------------------------------------------


   @Bean
   public JsonItemReader<Customer> reader(){
      return new JsonItemReaderBuilder<Customer>()
               .jsonObjectReader(new JacksonJsonObjectReader<>(Customer.class))
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
         JdbcBatchItemWriter<Customer> writer) {
      return new StepBuilder("step1", repo)
            .<Customer, Customer>chunk(10, transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build();

   }

}
