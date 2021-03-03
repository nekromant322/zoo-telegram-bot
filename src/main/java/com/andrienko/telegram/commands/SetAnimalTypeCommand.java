package com.andrienko.telegram.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

@Component
public class SetAnimalTypeCommand extends ZooCommand {
    public SetAnimalTypeCommand() {
        super("set_animal_type", "Выбрать тип животного\n");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();


        List<InlineKeyboardButton> keyboardButtonRowFirst = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButtonFirst = new InlineKeyboardButton();
        inlineKeyboardButtonFirst.setText("Кошка");
        inlineKeyboardButtonFirst.setCallbackData("Кошка");
        keyboardButtonRowFirst.add(inlineKeyboardButtonFirst);

        List<InlineKeyboardButton> keyboardButtonRowSecond = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButtonSecond = new InlineKeyboardButton();
        inlineKeyboardButtonSecond.setText("Собака");
        inlineKeyboardButtonSecond.setCallbackData("Собака");
        keyboardButtonRowSecond.add(inlineKeyboardButtonSecond);

        List<InlineKeyboardButton> keyboardButtonRowThird = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButtonThird = new InlineKeyboardButton();
        inlineKeyboardButtonThird.setText("Грызун");
        inlineKeyboardButtonThird.setCallbackData("Грызун");
        keyboardButtonRowThird.add(inlineKeyboardButtonThird);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonRowFirst);
        rowList.add(keyboardButtonRowSecond);
        rowList.add(keyboardButtonRowThird);
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());
        message.setText("Выбери тип животного");
        message.setReplyMarkup(inlineKeyboardMarkup);
        execute(absSender, message, user);
    }
}
