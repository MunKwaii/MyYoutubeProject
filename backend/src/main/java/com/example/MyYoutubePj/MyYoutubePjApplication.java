package com.example.MyYoutubePj;

import com.example.MyYoutubePj.config.KeywordFileProperties;
import com.example.MyYoutubePj.scheduler.YoutubeMetadataScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@SpringBootApplication
@EnableScheduling
@Slf4j
@EnableConfigurationProperties(KeywordFileProperties.class)
public class MyYoutubePjApplication {

	private static String initialKeyword = null;

	public static void main(String[] args) {
		// Nhập keyword trước khi start Spring
//		Scanner scanner = new Scanner(System.in);
//		System.out.print("Nhập từ khóa tìm kiếm keyword: ");
//		initialKeyword = scanner.nextLine().trim();

		// Khởi động Spring
		ConfigurableApplicationContext context = SpringApplication.run(MyYoutubePjApplication.class, args);

		// Set keyword cho scheduler
		YoutubeMetadataScheduler scheduler = context.getBean(YoutubeMetadataScheduler.class);
		scheduler.setKeyword(initialKeyword);
		scheduler.startScheduler();
//
//		log.info("Đã nhận từ khóa: {}", initialKeyword);
	}
}
