package com.matsim;

import jakarta.servlet.MultipartConfigElement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jboss.logging.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.unit.DataSize;

import javax.sql.DataSource;

@Configuration
@SpringBootApplication
@MapperScan("com.matsim.*")
//@EnableWebMvc
@ComponentScan("com.matsim.*")

public class MatsimApplication {


	public static void main(String[] args) {
		SpringApplication.run(MatsimApplication.class, args);
	}

//
	/**
	 * 文件上传大小配置
	 * @return
	 */
	private static Logger logger = Logger.getLogger(MatsimApplication.class);

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return new org.apache.tomcat.jdbc.pool.DataSource();
	}

	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/com/matsim/user/*.xml"));
		return sqlSessionFactoryBean.getObject();
	}


	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}
	@Bean
	public MultipartConfigElement multipartConfigElement(){
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(DataSize.ofMegabytes(1024));
		factory.setMaxRequestSize(DataSize.ofMegabytes(1024));
		return factory.createMultipartConfig();
	}
}
