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
public class SetAnimalNameCommand extends ZooCommand{
    AnimalRequestDTOService animalRequestDTOService;

    @Autowired
    public SetAnimalNameCommand(AnimalRequestDTOService animalRequestDTOService) {
        super("set_animal_name", "Задать кличку питомца\n");
        this.animalRequestDTOService = animalRequestDTOService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());
        AnimalRequestDTO animalRequestDTO = animalRequestDTOService.findDTOByChatId(chat.getId());

        String displayedName = getName(arguments);
        if (displayedName == null) {
            message.setText("Введите название команды /set_animal_name, а затем кличку питомца\n Пример: \"/set_animal_name Пушок\"");
            execute(absSender, message, user);
            return;
        }
        animalRequestDTO.setAnimalName(displayedName);
        StringBuilder sb = new StringBuilder();
        if (animalRequestDTO.getAnimalName() != null) {
            sb.append("Кличка питомца установлена: '").append(displayedName)
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
