package br.com.uri.mscheckout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsCheckoutApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCheckoutApplication.class, args);
	}

}
