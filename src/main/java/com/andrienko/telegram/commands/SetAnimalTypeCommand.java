package com.andrienko.telegram.commands;


import enums.AnimalType;
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
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        AnimalType[] animalTypes = AnimalType.values();
        for (int i = 0; i < animalTypes.length; i++) {
            List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(animalTypes[i].russianName);
            inlineKeyboardButton.setCallbackData(animalTypes[i].name);
            keyboardButtonRow.add(inlineKeyboardButton);
            rowList.add(keyboardButtonRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());
        message.setText("Выбери тип животного");
        message.setReplyMarkup(inlineKeyboardMarkup);
        execute(absSender, message, user);
    }
}
