package com.tribia.application;

import org.springframework.boot.SpringApplication;

public class TestInspectionReminderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(InspectionReminderServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
