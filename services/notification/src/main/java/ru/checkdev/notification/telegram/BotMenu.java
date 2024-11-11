package ru.checkdev.notification.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.checkdev.notification.telegram.action.Action;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 3. Мидл
 * Реализация меню телеграм бота.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
public class BotMenu extends TelegramLongPollingBot {
    private final Map<String, String> bindingBy = new ConcurrentHashMap<>();
    private final Map<String, Action> actions;
    private final String username;
    private final String token;


    public BotMenu(Map<String, Action> actions, String username, String token) throws TelegramApiException {
        this.actions = actions;
        this.username = username;
        this.token = token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var key = update.getMessage().getText();
            var chatId = update.getMessage().getChatId().toString();
            if (actions.containsKey(key)) {
                var msg = actions.get(key).handle(update.getMessage());
                bindingBy.put(chatId, key);
                send(msg);
            } else if (bindingBy.containsKey(chatId)) {
                var msg = actions.get(bindingBy.get(chatId)).callback(update.getMessage());
                bindingBy.remove(chatId);
                send(msg);
            } else {
                var unsupportedCommandMsg = new SendMessage(chatId,
                        "The command is not supported!\n\n" + getAvailableCommands());
                send(unsupportedCommandMsg);
            }
        }
    }

    private String getAvailableCommands() {
        return """
                Selects an action:
                /start - Show the available commands
                /new - Register a new user
                /check - Show the full name and email address for this account
                /bind - Link Telegram to Mock using your username and password
                /unbind - Unlink Telegram from Mock using your credentials
                """;
    }

    private void send(BotApiMethod msg) {
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
