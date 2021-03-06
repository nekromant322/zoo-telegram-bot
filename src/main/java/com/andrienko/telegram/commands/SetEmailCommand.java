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
public class SetEmailCommand extends ZooCommand{

    AnimalRequestDTOService animalRequestDTOService;

    @Autowired
    public SetEmailCommand(AnimalRequestDTOService animalRequestDTOService) {
        super("set_email", "Задать email\n");
        this.animalRequestDTOService = animalRequestDTOService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());
        AnimalRequestDTO animalRequestDTO = animalRequestDTOService.findDTOByChatId(chat.getId());
//todo проверка на адекватность почты
        String displayedName = getName(arguments);
        if (displayedName == null) {
            message.setText("Введите название команды /set_email, а затем email\n Пример: \"/set_email beauty_95@mail.ru\"");
            execute(absSender, message, user);
            return;
        }
        animalRequestDTO.setEmail(displayedName);
        StringBuilder sb = new StringBuilder();
        if (animalRequestDTO.getEmail() != null) {
            sb.append("Email установлен: '").append(displayedName)
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

