package ru.chernomoretc.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.chernomoretc.telegrambot.handler.AdminMessageHandler;

@SpringBootApplication
public class TelegramBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
		//SpringApplication.run(AdminMessageHandler.class, args);
	}
}
