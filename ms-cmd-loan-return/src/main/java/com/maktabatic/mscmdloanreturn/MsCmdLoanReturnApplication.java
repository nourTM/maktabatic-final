package com.maktabatic.mscmdloanreturn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsCmdLoanReturnApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCmdLoanReturnApplication.class, args);
	}

}
