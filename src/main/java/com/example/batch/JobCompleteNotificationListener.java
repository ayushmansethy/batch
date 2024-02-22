package com.example.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;
@Component
public class JobCompleteNotificationListener implements JobExecutionListener {

    

    @Override
    public void afterJob(JobExecution jobExecution) {
        
        JobExecutionListener.super.afterJob(jobExecution);
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
      
        JobExecutionListener.super.beforeJob(jobExecution);
    }
    
}
