package com.andrienko.telegram;

import com.andrienko.telegram.commands.*;
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

    private final ReplyMessageService replyMessageService;
    private AnimalRequestDTOService animalRequestDTOService;
    private SetNameCommand setNameCommand;
    private SetSurnameCommand setSurnameCommand;
    private SetPhoneCommand setPhoneCommand;
    private SetEmailCommand setEmailCommand;
    private SetAnimalNameCommand setAnimalNameCommand;
    private SetAnimalTypeCommand setAnimalTypeCommand;
    private SetRoomTypeCommand setRoomTypeCommand;
private StartCommand startCommand;
private SetBeginDateCommand setBeginDateCommand;
private SetEndDateCommand setEndDateCommand;
    Map<String, String> messages = new HashMap<>();
    AnimalType[] animalTypes = AnimalType.values();
    RoomType[] roomTypes = RoomType.values();

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    public ZooHotelBot(SetNameCommand setNameCommand, SetSurnameCommand setSurnameCommand, SetPhoneCommand setPhoneCommand,
                       SetEmailCommand setEmailCommand, SetAnimalNameCommand setAnimalNameCommand,
                       SetAnimalTypeCommand setAnimalTypeCommand, SetRoomTypeCommand setRoomTypeCommand,StartCommand startCommand,
                       ReplyMessageService replyMessageService, AnimalRequestDTOService animalRequestDTOService,
                       SetBeginDateCommand setBeginDateCommand,SetEndDateCommand setEndDateCommand) {
        super();
        this.setNameCommand = setNameCommand;
        this.setSurnameCommand = setSurnameCommand;
        this.setPhoneCommand = setPhoneCommand;
        this.setEmailCommand = setEmailCommand;
        this.setAnimalNameCommand = setAnimalNameCommand;
        this.setAnimalTypeCommand = setAnimalTypeCommand;
        this.setRoomTypeCommand = setRoomTypeCommand;
        this.replyMessageService = replyMessageService;
        this.animalRequestDTOService = animalRequestDTOService;
        this.startCommand = startCommand;
        this.setBeginDateCommand = setBeginDateCommand;
        this.setEndDateCommand = setEndDateCommand;
        registerAll(setNameCommand, setSurnameCommand, setPhoneCommand, setEmailCommand,
                setAnimalNameCommand, setAnimalTypeCommand, setRoomTypeCommand,startCommand,setBeginDateCommand,setEndDateCommand);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void processNonCommandUpdate(Update update) {

        if (update.hasMessage()) {
//            AnimalRequestDTO animalRequestDTO = animalRequestDTOService.findDTOByChatId(update.getMessage().getChatId())
            sendMessage(update);
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            String callBackId = update.getCallbackQuery().getId();
            AnimalRequestDTO animalRequestDTO = animalRequestDTOService.findDTOByChatId(update.getUpdateId().longValue());
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
        messages.put("Отправить заявку","Отправляю данные");
        for (Map.Entry<String, String> messagesMap : messages.entrySet()) {
            if (update.getMessage().getText().equals(messagesMap.getKey())) {
                sendMessage.setText(messagesMap.getValue());
                if(update.getMessage().equals("Отправить заявку")){
                    try{
                        animalRequestDTOService.sendDTO(update.getUpdateId().longValue());
                    }catch (Exception e){
                        sendMessage.setText("Заявка не отправлена, возникли проблемы");
                    }
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
