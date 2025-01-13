package com.phil.antispam.defender;

import com.phil.antispam.repository.SpamKeywordRepository;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class AntiSpamBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(AntiSpamBot.class);

    private List<String> spamKeywords;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;


    @Autowired
    private SpamKeywordRepository spamKeywordRepository;


    @PostConstruct
    protected void loadSpamKeywords() {
        spamKeywords = spamKeywordRepository.findAllKeywords();
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
                    sendMessageHTML(chatId, "🚫 <b>Сообщение удалено:</b> 🔍 <i>обнаружен спам.</i>");
                } else {
                    processCommand(chatId, messageText);
                }
            } catch (TelegramApiException e) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    protected boolean isSpam(String message) {
        return spamKeywords.stream().anyMatch(message.toLowerCase()::contains);
    }

    protected void deleteMessage(Long chatId, Integer messageId) throws TelegramApiException {
        DeleteMessage deleteMessage =
            new DeleteMessage();
        deleteMessage.setChatId(chatId.toString());
        deleteMessage.setMessageId(messageId);
        execute(deleteMessage);
        LOGGER.info("Сообщение удалено: {}", messageId);
    }

    protected void sendMessageHTML(Long chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        sendMessage.setParseMode("HTML");
        execute(sendMessage);
        LOGGER.info("Сообщение отправлено: {}", text);
    }

    protected void processCommand(Long chatId, String messageText) throws TelegramApiException {
        switch (messageText) {
            case "/start":
                sendMessageHTML(chatId, "👋 Привет! Я антиспам-бот. \uD83D\uDEE1\uFE0F");
                break;
            case "/help":
                sendMessageHTML(chatId, "📜 *Доступные команды:*\n" +
                                        "✅ /start - Запуск бота\n" +
                                        "ℹ️ /help - Показать справку");
                break;
            default:
                break;
        }
    }
}
