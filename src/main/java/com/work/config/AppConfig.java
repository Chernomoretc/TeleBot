package com.work.config;

import com.work.botconfig.TelegramBotConfig;
import com.work.model.TelegramBot;
import com.work.model.TelegramFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;


@Configuration
public class AppConfig {
    private final TelegramBotConfig botConfig;

    public AppConfig(TelegramBotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(botConfig.getWebHookPath()).build();
    }

    @Bean
    public TelegramBot springWebhookBot(SetWebhook setWebhook, TelegramFacade telegramFacade) {
        TelegramBot bot = new TelegramBot(telegramFacade, setWebhook);
        bot.setBotToken(botConfig.getBotToken());
        bot.setBotUsername(botConfig.getUserName());
        bot.setBotPath(botConfig.getWebHookPath());

        return bot;
    }
}
