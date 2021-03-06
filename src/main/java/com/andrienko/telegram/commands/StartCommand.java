package com.andrienko.telegram.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

@Component
public class StartCommand extends ZooCommand {

    public StartCommand() {
        super("start", "Начать использование бота\n");
    }

    /**
     * реализованный метод класса BotCommand, в котором обрабатывается команда, введенная пользователем
     *
     * @param absSender - отправляет ответ пользователю
     * @param user      - пользователь, который выполнил команду
     * @param chat      - чат бота и пользователя
     * @param strings   - аргументы, переданные с командой
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        StringBuilder sb = new StringBuilder();
        sb.append("Добро пожаловать в бот зоогостиницы!\n");
        sb.append("Выбери что сделать дальше");
        SendMessage message = new SendMessage();
        message.setReplyMarkup(replyKeyboardMarkup);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Подать новую заявку");
        arrayList.add("Меню");
        arrayList.add("Отправить заявку");
        for (int i = 0; i < arrayList.size(); i++) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(arrayList.get(i));
            keyboardRows.add(keyboardRow);
        }
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        message.setChatId(chat.getId().toString());
        message.setText(sb.toString());
        execute(absSender, message, user);
    }



}
