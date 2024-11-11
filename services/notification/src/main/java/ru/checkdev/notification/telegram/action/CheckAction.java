package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.List;


@AllArgsConstructor
@Slf4j
public class CheckAction implements Action {
    private static final String URL_GET_PROFILE = "/profiles/chat/";
    private final TgAuthCallWebClint authCallWebClint;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var userChatId = message.getFrom().getId();
        var text = "";

        List<PersonDTO> personDTO;
        try {
            personDTO = this.authCallWebClint.doGet(URL_GET_PROFILE + userChatId).block();
        } catch (Exception e) {
            log.error("WebClient doGet error: {}", e.getMessage());
            text = "Сервис не доступен, попробуйте позже";
            return new SendMessage(chatId, text);
        }

        StringBuilder textBuilder = new StringBuilder();
        if (personDTO != null && !personDTO.isEmpty()) {
            if (personDTO.size() == 1) {
                textBuilder.append("Ваш аккаунт, найденные в системе:\n\n");
            } else {
                textBuilder.append("Ваши аккаунты, найденные в системе:\n\n");
            }
            for (PersonDTO dto : personDTO) {
                textBuilder.append("Username: ").append(dto.getUsername()).append("\n")
                        .append("Email: ").append(dto.getEmail()).append("\n\n");
            }
        } else {
            textBuilder.append("Аккаунт не найден");
        }

        return new SendMessage(chatId, textBuilder.toString());
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}
