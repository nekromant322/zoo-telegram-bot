package com.andrienko.telegram;

import com.andrienko.telegram.commands.SetAnimalTypeCommand;
import com.andrienko.telegram.commands.SetRoomTypeCommand;
import com.andrienko.telegram.commands.StartCommand;
import com.andrienko.telegram.enums.AnimalType;
import com.andrienko.telegram.enums.RoomType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;


@Component
public class ZoohotelBot extends TelegramLongPollingCommandBot {
    @Autowired
    private ReplyMessageService replyMessageService;

    @Autowired
    AnimalRequest animalRequest;

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    public ZoohotelBot() {
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

        //обработка меню(текстовых сообщений)
        if (update.hasMessage()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));

            if (update.getMessage().getText().equals("Подать новую заявку")) {
                sendMessage.setText("Для того чтобы выбрать тип животного нажмите /set_animal_type");
            } else if (update.getMessage().getText().equals("Подробнее о функциях бота")) {
                StringBuilder sb = new StringBuilder();
                sb.append("/set_animal_type выбрать тип животного\n");
                sb.append("/set_room_type выбрать тип комнаты\n");
                sendMessage.setText(sb.toString());
            } else {
                sendMessage.setText("Не понимаю команду");
            }
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            //обработка кнопок сообщений
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callBackData = callbackQuery.getData();
            String callBackId = callbackQuery.getId();

            if (callBackData.equals("Кошка") || callBackData.equals("Собака") || callBackData.equals("Грызун")) {
                AnswerCallbackQuery answerCallbackQuery;

                if (callBackData.equals("Кошка")) {
                    animalRequest.setAnimalType(AnimalType.CAT);
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип животного кошка установлен");
                } else if (callBackData.equals("Собака")) {
                    animalRequest.setAnimalType(AnimalType.DOG);
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип животного собака установлен");
                } else if (callBackData.equals("Грызун")) {
                    animalRequest.setAnimalType(AnimalType.RAT);
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип животного грызун установлен");
                } else {
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип животного не установлен");
                }
                try {
                    execute(answerCallbackQuery);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (callBackData.equals("Common") || callBackData.equals("Large") || callBackData.equals("VIP")) {
                AnswerCallbackQuery answerCallbackQuery;
                if (callBackData.equals("Common")) {
                    animalRequest.setRoomType(RoomType.COMMON);
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип комнаты common установлен");
                } else if (callBackData.equals("Large")) {
                    animalRequest.setRoomType(RoomType.LARGE);
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип комнаты large установлен");
                } else if (callBackData.equals("VIP")) {
                    animalRequest.setRoomType(RoomType.VIP);
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип комнаты vip установлен");
                } else {
                    answerCallbackQuery = replyMessageService.getPopUpAnswer(callBackId, "Тип комнаты не установлен");
                }
                try {
                    execute(answerCallbackQuery);

                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Что-то идёт не так");
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

}
