package com.andrienko.telegram.commands;

import com.andrienko.telegram.AnimalRequestDTOService;
import dto.AnimalRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class SetBeginDateCommand extends ZooCommand{

    AnimalRequestDTOService animalRequestDTOService;

    @Autowired
    public SetBeginDateCommand(AnimalRequestDTOService animalRequestDTOService) {
        super("set_begin_date", "Задать дату начала передержки\n");
        this.animalRequestDTOService = animalRequestDTOService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());
        AnimalRequestDTO animalRequestDTO = animalRequestDTOService.findDTOByChatId(chat.getId());
//todo проверка на адекватность даты
        String displayedName = getName(arguments);
        if (displayedName == null) {
            message.setText("Введите название команды /set_begin_date, а затем начальную дату\n Пример: \"/set_begin_date 01.11.2021\"");
            execute(absSender, message, user);
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        animalRequestDTO.setBeginDate(LocalDate.parse(displayedName,formatter));
        StringBuilder sb = new StringBuilder();
        if (animalRequestDTO.getBeginDate() != null) {
            sb.append("Начальная дата установлена: '").append(displayedName)
                    .append("'\n Заполните дату окончания передержки");
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

