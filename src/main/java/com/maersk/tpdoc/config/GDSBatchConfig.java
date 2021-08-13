package com.maersk.tpdoc.config;

import com.maersk.tpdoc.entity.GDSMessage;
import com.maersk.tpdoc.entity.GDSUpdate;
import com.maersk.tpdoc.listener.*;
import com.maersk.tpdoc.mapper.GDSMessageMapper;
import com.maersk.tpdoc.processor.GDSMessageProcessor;
import com.maersk.tpdoc.processor.exception.EmptyGdsMessageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.OraclePagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableBatchProcessing
@Slf4j
public class GDSBatchConfig extends DefaultBatchConfigurer {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(null);
    }

    @Bean
    public ItemReader<GDSMessage> gdsMessageReader(final DataSource dataSource) {
        final JdbcPagingItemReader<GDSMessage> result = new JdbcPagingItemReader<>();
        result.setName("GDSMessageReader");
        result.setDataSource(dataSource);
        result.setPageSize(1000);
        result.setRowMapper(new GDSMessageMapper());

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("INSTANCEID", Order.ASCENDING);
        final OraclePagingQueryProvider provider = new OraclePagingQueryProvider();
        provider.setSelectClause("SELECT INSTANCEID, CONTENT");
        provider.setFromClause("FROM GEMS.GEMSEVTGDS");
        provider.setWhereClause("WHERE FULLORPARTIAL = 'F' AND GDSINSTANCEID IS NULL");
        provider.setSortKeys(sortKeys);
        result.setSaveState(false);
        result.setQueryProvider(provider);
        return result;
    }

    @Bean
    public GDSMessageProcessor gdsMessageProcessor() {
        return new GDSMessageProcessor();
    }

    @Bean
    public Job gdsInstanceIdUpdateJob(final Step step) {
        return this.jobBuilderFactory.get("gdsInstanceIdUpdateJob")
                .listener(new GDSStepExecutionListener())
                .incrementer(new RunIdIncrementer())
                .start(step).build();
    }

    @Bean
    public Step step(final ItemReader<GDSMessage> gdsMessageReader, final GDSMessageProcessor gdsMessageProcessor,
                     final ItemWriter<GDSUpdate> recordWriter) {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setThreadNamePrefix("GDSStep");
        taskExecutor.afterPropertiesSet();

        return this.stepBuilderFactory.get("gdsStep")
                .<GDSMessage, GDSUpdate>chunk(150)
                .listener(new GDSChunkListener())
                .reader(gdsMessageReader)
                .processor(gdsMessageProcessor)
                .faultTolerant()
                .skip(EmptyGdsMessageException.class)
                .writer(recordWriter)
                .listener(new ReaderListener())
                .listener(new UpdateListener())
                .listener(new WriterListener())
                .taskExecutor(taskExecutor)
                .throttleLimit(10)
                .allowStartIfComplete(true)
                .build();
    }

//    @Bean
//    public ItemWriter<GDSUpdate> recordWriter() {
//        return items -> {
//            for (GDSUpdate c : items) {
//                System.out.println(c.toString());
//            }
//        };
//    }

    @Bean
    public ItemWriter<GDSUpdate> recordWriter(final DataSource dataSource) {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        JdbcBatchItemWriter<GDSUpdate> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("UPDATE GEMS.GEMSEVTGDS SET GDSINSTANCEID = ? WHERE INSTANCEID = ?");
        writer.setJdbcTemplate(jdbcTemplate);
        writer.setItemPreparedStatementSetter((item, ps) -> {
            ps.setString(1, item.getContentInstanceId());
            ps.setLong(2, item.getInstanceId());
        });
        return writer;
    }
}
