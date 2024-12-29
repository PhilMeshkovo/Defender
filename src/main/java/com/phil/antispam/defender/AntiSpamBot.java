package com.phil.antispam.defender;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class AntiSpamBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(AntiSpamBot.class);


    //TODO продумать откуда лучше брать слова определяющие спам
    private static final List<String> SPAM_KEYWORDS = Arrays.asList("spam", "buy now", "discount", "дурак", "дебил");

    private final String botUsername;
    private final String botToken;

    public AntiSpamBot(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            Long chatId = message.getChatId();

            LOGGER.info("Получено сообщение: {}", message);

            try {
                if (isSpam(messageText)) {
                    deleteMessage(chatId, message.getMessageId());
                    sendMessage(chatId, "Сообщение удалено: обнаружен спам.");
                } else {
                    processCommand(chatId, messageText);
                }
            } catch (TelegramApiException e) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private boolean isSpam(String message) {
        return SPAM_KEYWORDS.stream().anyMatch(message.toLowerCase()::contains)
               || message.contains("http://") || message.contains("https://");
    }

    private void deleteMessage(Long chatId, Integer messageId) throws TelegramApiException {
        org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage deleteMessage =
            new org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage();
        deleteMessage.setChatId(chatId.toString());
        deleteMessage.setMessageId(messageId);
        execute(deleteMessage);
        LOGGER.info("Сообщение удалено: {}", messageId);
    }

    private void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        execute(sendMessage);
        LOGGER.info("Сообщение отправлено: {}", text);
    }

    private void processCommand(Long chatId, String messageText) throws TelegramApiException {
        switch (messageText) {
            case "/start":
                sendMessage(chatId, "Привет! Я антиспам-бот.");
                break;
            case "/help":
                sendMessage(chatId, "Доступные команды:\n/start - Запуск бота\n/help - Показать справку");
                break;
            default:
                break;
        }
    }
}
