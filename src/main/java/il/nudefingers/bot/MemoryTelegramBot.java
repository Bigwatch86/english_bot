package il.nudefingers.bot;

import il.nudefingers.bot.components.Button;
import il.nudefingers.bot.config.BotConfig;
import il.nudefingers.bot.database.Word;
import il.nudefingers.bot.database.WordsRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Stream;

import static il.nudefingers.bot.components.CommandMenu.HELP_TEXT;
import static il.nudefingers.bot.components.CommandMenu.LIST_OF_COMMANDS;

@Slf4j
@Component
@AllArgsConstructor
public class MemoryTelegramBot extends TelegramLongPollingBot {
    //@Autowired
    private final WordsRepository wordsRepository;
    //@Autowired
    final BotConfig config;
    boolean isAdding = false;

    /*
    @Autowired
    public MemoryTelegramBot(WordsRepository wordsRepository, BotConfig botConfig) {
        this.wordsRepository = wordsRepository;
        this.config = botConfig;
    }*/

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
    @Override
    public String getBotToken() {
        return config.getToken();
    }
    @Override
    public void onUpdateReceived(@NotNull Update update) {
        long chatId = 0;
        long userId = 0; //это нам вроде не надо
        String userName = null;
        String receivedMessage;

        //если получено сообщение текстом
        if(update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            userId = update.getMessage().getFrom().getId();
            userName = update.getMessage().getFrom().getFirstName();

            if (update.getMessage().hasText()) {
                receivedMessage = update.getMessage().getText();
                botAnswerUtils(receivedMessage, chatId, userName);
            }

            //если нажата одна из кнопок бота
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            receivedMessage = update.getCallbackQuery().getData();

            botAnswerUtils(receivedMessage, chatId, userName);
        }
    }

    private void botAnswerUtils(String receivedMessage, long chatId, String userName) {
        switch (receivedMessage){
            case "/start":
                startBot(chatId, userName);
                break;
            case "/help":
                sendHelpText(chatId, HELP_TEXT);
                break;
            case "/add":
                startAdding(chatId);
                break;
            default:
                if (isAdding) {
                    addNewWords(chatId, receivedMessage);
                }
                break;
        }
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hi, " + userName + "! I'm a Telegram bot.'");
        message.setReplyMarkup(Button.inlineMarkup());

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void sendHelpText(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void startAdding(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Тут будет механизм загрузки слов пачкой");
        isAdding = !isAdding;

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void addNewWords(long chatId, String text){
        // механизм загрузки слов в базу
        List<String> vocabulary = Stream.of(text.split("\n"))
                .toList();

        for (String word : vocabulary) {
            Word newWord = Word.builder()
                    .meaning(word.split(" - ")[0])
                    .translation(word.split(" - ")[1])
                    .build();
            wordsRepository.save(newWord);
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Новые слова загружены!");
        isAdding = !isAdding;

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }
}
