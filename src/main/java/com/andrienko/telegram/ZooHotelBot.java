package com.andrienko.telegram;

import com.andrienko.telegram.commands.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import dto.AnimalRequestDTO;
import enums.AnimalType;
import enums.RoomType;
import lombok.SneakyThrows;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;


@Component
public class ZooHotelBot extends TelegramLongPollingCommandBot {

    private final ReplyMessageService replyMessageService;
    private final AnimalRequestDTOService animalRequestDTOService;
    Map<String, String> messages = new HashMap<>();

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    public ZooHotelBot(SetNameCommand setNameCommand, SetSurnameCommand setSurnameCommand, SetPhoneCommand setPhoneCommand,
                       SetEmailCommand setEmailCommand, SetAnimalNameCommand setAnimalNameCommand,
                       SetAnimalTypeCommand setAnimalTypeCommand, SetRoomTypeCommand setRoomTypeCommand, StartCommand startCommand,
                       ReplyMessageService replyMessageService, AnimalRequestDTOService animalRequestDTOService,
                       SetBeginDateCommand setBeginDateCommand, SetEndDateCommand setEndDateCommand) {
        super();
        this.replyMessageService = replyMessageService;
        this.animalRequestDTOService = animalRequestDTOService;
        registerAll(setNameCommand, setSurnameCommand, setPhoneCommand, setEmailCommand,
                setAnimalNameCommand, setAnimalTypeCommand, setRoomTypeCommand, startCommand, setBeginDateCommand, setEndDateCommand);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @SneakyThrows
    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasMessage()) {
            sendMessage(update);
        } else if (update.hasCallbackQuery()) {

            String callBackData = update.getCallbackQuery().getData();
            String callBackId = update.getCallbackQuery().getId();
            AnimalRequestDTO animalRequestDTO = animalRequestDTOService.findDTOByChatId(update.getCallbackQuery().getFrom().getId());
            AnswerCallbackQuery answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип не установлен");

            for (int i = 0; i < AnimalType.values().length; i++) {
                if (callBackData.equals(AnimalType.values()[i].name)) {
                    animalRequestDTO.setAnimalType(AnimalType.values()[i]);
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип животного " + AnimalType.values()[i].russianName + " установлен");
                }
            }
            for (int i = 0; i < RoomType.values().length; i++) {
                if (callBackData.equals(RoomType.values()[i].name)) {
                    animalRequestDTO.setRoomType(RoomType.values()[i]);
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип комнаты " + RoomType.values()[i].russianName + " установлен");
                }
            }
            try {
                execute(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAnswer(Long chatId, String userName, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getUserName(Message msg) {
        User user = msg.getFrom();
        String userName = user.getUserName();
        return (userName != null) ? userName : String.format("%s %s", user.getLastName(), user.getFirstName());
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private void sendMessage(Update update) throws JsonProcessingException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setText("Не понимаю команду");
        messages.put("Подать новую заявку", "Для того чтобы выбрать тип животного нажмите /set_animal_type");
        messages.put("Меню", "/set_animal_type выбрать тип животного\n/set_room_type выбрать тип комнаты");
        messages.put("Отправить заявку", "Отправляю данные");
        for (Map.Entry<String, String> messagesMap : messages.entrySet()) {
            if (update.getMessage().getText().equals(messagesMap.getKey())) {
                sendMessage.setText(messagesMap.getValue());
                if(update.getMessage().getText().equals("Отправить заявку")) {
                    animalRequestDTOService.sendDTO(update.getMessage().getChatId());
                }
            }

        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
