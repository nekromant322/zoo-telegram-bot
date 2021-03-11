package com.andrienko.telegram.commands;

import com.andrienko.telegram.AnimalRequestDTOService;
import dto.AnimalRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
public class SetPhoneCommand extends ZooCommand{ AnimalRequestDTOService animalRequestDTOService;

    @Autowired
    public SetPhoneCommand(AnimalRequestDTOService animalRequestDTOService) {
        super("set_name", "Задать имя владельца\n");
        this.animalRequestDTOService = animalRequestDTOService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());
        AnimalRequestDTO animalRequestDTO = animalRequestDTOService.findDTOByChatId(chat.getId());

        String displayedName = getName(arguments);
        if (displayedName == null) {
            message.setText("Введите номер телефона /set_phone, а затем номер телефона\n Пример: \"/set_phone 89112451122\"");
            execute(absSender, message, user);
            return;
        }
        animalRequestDTO.setPhoneNumber(displayedName);
        StringBuilder sb = new StringBuilder();
        if (animalRequestDTO.getPhoneNumber()!= null) {
            sb.append("Номер установлен: '").append(displayedName)
                    .append("'\n Можно переходить к заполнению следующих данных");
        }
        message.setText(sb.toString());
        execute(absSender, message, user);
    }


    private String getName(String[] strings) {

        if (strings == null || strings.length == 0) {
            return null;
        }

        String name = String.join(" ", strings);
        return name.replaceAll(" ", "").isEmpty() ? null : name;
    }
}
