package com.pankal;

import com.pankal.inventory.car.Type;
import com.pankal.inventory.car.TypeRepository;
import com.pankal.inventory.car.VehicleCategory;
import com.pankal.inventory.car.VehicleCategoryRepository;
import com.pankal.inventory.car.VehicleMake;
import com.pankal.inventory.car.VehicleMakeRepository;
import com.pankal.inventory.car.VehicleModel;
import com.pankal.inventory.car.VehicleModelRepository;
import com.pankal.task.TaskRepository;
import com.pankal.utilities.Csv2DB;
import com.pankal.utilities.DBUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@EnableScheduling
@SpringBootApplication
public class Application {


	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx, VehicleModelRepository rep) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			DBUtilities.connect(jdbcTemplate.getDataSource());

//			List<VehicleModel> res = Csv2DB.processInputFile("./assets/vehicle_models.csv");
//			for (VehicleModel type : res) {
//				System.out.println(type);
//				rep.save(type);
//
//			}

//			String[] beanNames = ctx.getBeanDefinitionNames();
//			Arrays.sort(beanNames);
//			for (String beanName : beanNames) {
//				System.out.println(beanName);
//			}

		};
	}
}
