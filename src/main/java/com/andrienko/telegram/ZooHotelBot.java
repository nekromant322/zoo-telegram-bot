package com.andrienko.telegram;

import com.andrienko.telegram.commands.SetAnimalTypeCommand;
import com.andrienko.telegram.commands.SetRoomTypeCommand;
import com.andrienko.telegram.commands.StartCommand;
import dto.AnimalRequestDTO;
import enums.AnimalType;
import enums.RoomType;
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
    @Autowired
    private ReplyMessageService replyMessageService;

    Map<Long, AnimalRequestDTO> cachedAnimalRequestData = new HashMap<>();
    Map<String, String> messages = new HashMap<>();
    AnimalType[] animalTypes = AnimalType.values();
    RoomType[] roomTypes = RoomType.values();

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    public ZooHotelBot() {
        super();
        register(new StartCommand());
        register(new SetAnimalTypeCommand());
        register(new SetRoomTypeCommand());
    }


    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        AnimalRequestDTO animalRequestDTO = new AnimalRequestDTO();
        if (update.hasMessage()) {
            sendMessage(update);
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            String callBackId = update.getCallbackQuery().getId();
            AnswerCallbackQuery answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип не установлен");

            for (int i = 0; i < animalTypes.length; i++) {
                if (callBackData.equals(animalTypes[i].name)) {
                    animalRequestDTO.setAnimalType(animalTypes[i]);
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип животного " + animalTypes[i].russianName + " установлен");
                }
            }
            for (int i = 0; i < roomTypes.length; i++) {
                if (callBackData.equals(roomTypes[i].name)) {
                    animalRequestDTO.setRoomType(roomTypes[i]);
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип комнаты " + roomTypes[i].russianName + " установлен");
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
            //логируем сбой Telegram Bot API, используя userName
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

    private void sendMessage(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        messages.put("Подать новую заявку", "Для того чтобы выбрать тип животного нажмите /set_animal_type");
        messages.put("Меню", "/set_animal_type выбрать тип животного\n/set_room_type выбрать тип комнаты");
        for (Map.Entry<String, String> messagesMap : messages.entrySet()) {
            if (update.getMessage().getText().equals(messagesMap.getKey())) {
                sendMessage.setText(messagesMap.getValue());
            }
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
