package com.phil.antispam.defender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.phil.antispam.repository.SpamKeywordRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
public class AntiSpamBotTest {

    @Mock
    private SpamKeywordRepository spamKeywordRepository;

    @Spy
    @InjectMocks
    private AntiSpamBot antiSpamBot;

    @BeforeEach
    public void setup() {
        List<String> keywords = List.of("spam", "advertisement");
        when(spamKeywordRepository.findAllKeywords()).thenReturn(keywords);
        antiSpamBot.loadSpamKeywords();
    }

    @Test
    public void testOnUpdateReceived_WhenSpamMessage_ShouldDeleteMessage() throws TelegramApiException {
        doAnswer(invocation -> null).when(antiSpamBot).execute(any(SendMessage.class));
        doAnswer(invocation -> null).when(antiSpamBot).execute(any(DeleteMessage.class));

        Update update = new Update();
        Message message = new Message();
        message.setText("spam message");
        message.setChat(new Chat(12345L, "type"));
        message.setMessageId(1);
        update.setMessage(message);

        // Вызываем тестируемый метод
        antiSpamBot.onUpdateReceived(update);

        // Проверяем, что сообщение было удалено
        ArgumentCaptor<DeleteMessage> deleteCaptor = ArgumentCaptor.forClass(DeleteMessage.class);
        verify(antiSpamBot).execute(deleteCaptor.capture());
        DeleteMessage capturedDelete = deleteCaptor.getValue();

        assertEquals("12345", capturedDelete.getChatId());
        assertEquals(1, capturedDelete.getMessageId());

        ArgumentCaptor<SendMessage> sendCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(antiSpamBot).execute(sendCaptor.capture());
        SendMessage capturedSend = sendCaptor.getValue();

        assertEquals("12345", capturedSend.getChatId());
        assertTrue(capturedSend.getText().contains("обнаружен спам"));
        assertEquals("HTML", capturedSend.getParseMode());
    }

    @Test
    public void testOnUpdateReceived_WhenNonSpamCommand_ShouldProcessCommand() throws TelegramApiException {
        // Мокаем метод execute()
        doAnswer(invocation -> null).when(antiSpamBot).execute(any(SendMessage.class));

        Update update = new Update();
        Message message = new Message();
        message.setText("/start");
        message.setChat(new Chat(12345L, "type"));
        update.setMessage(message);

        antiSpamBot.onUpdateReceived(update);

        ArgumentCaptor<SendMessage> sendCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(antiSpamBot).execute(sendCaptor.capture());
        SendMessage capturedSend = sendCaptor.getValue();

        assertEquals("12345", capturedSend.getChatId());
        assertTrue(capturedSend.getText().contains("Привет! Я антиспам-бот"));
    }

    @Test
    public void testOnUpdateReceived_WhenNonSpamText_ShouldNotDeleteOrNotify() throws TelegramApiException {
        Update update = new Update();
        Message message = new Message();
        message.setText("Hello, how are you?");
        message.setChat(new Chat(12345L, "type"));
        update.setMessage(message);

        antiSpamBot.onUpdateReceived(update);

        verify(antiSpamBot, never()).execute(any(DeleteMessage.class));
        verify(antiSpamBot, never()).execute(any(SendMessage.class));
    }
}

