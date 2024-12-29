package com.phil.antispam.defender;

import java.util.Arrays;
import java.util.List;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class AntiSpamBot extends TelegramLongPollingBot {

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

            System.out.println("Получено сообщение: " + messageText);

            try {
                if (isSpam(messageText)) {
                    deleteMessage(chatId, message.getMessageId());
                    sendMessage(chatId, "Сообщение удалено: обнаружен спам.");
                } else {
                    processCommand(chatId, messageText);
                }
            } catch (TelegramApiException e) {
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
        System.out.println("Удалено сообщение ID: " + messageId);
    }

    private void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        execute(sendMessage);
        System.out.println("Отправлено сообщение: " + text);
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
