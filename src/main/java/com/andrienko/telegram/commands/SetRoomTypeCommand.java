package com.andrienko.telegram.commands;

import enums.AnimalType;
import enums.RoomType;
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
public class SetRoomTypeCommand extends ZooCommand{

    public SetRoomTypeCommand() {
        super("set_room_type", "Выбрать тип комнаты\n");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        RoomType[] roomTypes = RoomType.values();
        for (int i = 0; i < roomTypes.length; i++) {
            List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(roomTypes[i].russianName);
            inlineKeyboardButton.setCallbackData(roomTypes[i].name);
            keyboardButtonRow.add(inlineKeyboardButton);
            rowList.add(keyboardButtonRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());
        message.setText("Выбери тип комнаты");
        message.setReplyMarkup(inlineKeyboardMarkup);
        execute(absSender, message, user);
    }
}
