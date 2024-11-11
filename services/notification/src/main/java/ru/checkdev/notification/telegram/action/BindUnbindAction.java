package ru.checkdev.notification.telegram.action;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.telegram.config.TgConfig;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Calendar;

@AllArgsConstructor
@Slf4j
public class BindUnbindAction implements Action {
    private static final String URL_POST_BIND = "/profiles/bindAndUnbind/";
    private static final String ERROR_OBJECT = "error";
    private final TgConfig tgConfig = new TgConfig("tg/", 8);
    private final TgAuthCallWebClint authCallWebClint;
    private final String action;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Enter your email and password separated by a space to add your account to the system:";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var id = message.getFrom().getId();
        var loginAndPassword = message.getText().split(" ");
        var email = loginAndPassword[0];
        var password = loginAndPassword[1];
        var username = message.getFrom().getUserName();
        var text = "";
        var sl = System.lineSeparator();
        boolean bind = this.action.equals("unbind");

        var person = new PersonDTO(username, email, password, true, null,
                Calendar.getInstance(), id, bind);
        Object result;
        try {
            result = this.authCallWebClint.doPost(URL_POST_BIND, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Service not available try later" + sl
                    + "/start";
            return new SendMessage(chatId, text);
        }

        var mapObject = this.tgConfig.getObjectToMap(result);

        if (mapObject.containsKey(ERROR_OBJECT)) {
            text = "Registration error: " + mapObject.get(ERROR_OBJECT);
            return new SendMessage(chatId, text);
        }

        text = "Your account is " + (bind ? "unbind" : "bind") + ": " + sl
                + "UserName: " + username + sl
                + "Email: " + email;

        return new SendMessage(chatId, text);
    }
}
