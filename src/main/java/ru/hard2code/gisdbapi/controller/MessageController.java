package ru.hard2code.gisdbapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.hard2code.gisdbapi.domain.dto.MessageDto;
import ru.hard2code.gisdbapi.domain.entity.Message;
import ru.hard2code.gisdbapi.domain.mapper.MessageMapper;
import ru.hard2code.gisdbapi.service.message.MessageService;
import ru.hard2code.gisdbapi.system.Constants;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Constants.Route.MESSAGES)
@Tag(name = "MessageController", description = "User questions management " +
        "API")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;


    @GetMapping
    public List<MessageDto> getAllMessages() {
        return messageService.getAllMessages()
                .stream().map(messageMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public MessageDto getMessageById(@PathVariable("id") long id) {
        return messageMapper.toDto(messageService.getMessageById(id));
    }

    @GetMapping("user/{chatId}")
    public List<MessageDto> getMessagesByChatId(@PathVariable("chatId") String chatId) {
        return messageService.findMessageByChatId(chatId)
                .stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public MessageDto createMessage(@RequestBody Message msg) {
        return messageMapper.toDto(messageService.createMessage(msg));
    }

    @PutMapping("{id}")
    public MessageDto updateMessage(@PathVariable("id") long id,
                                    @RequestBody Message msg) {
        return messageMapper.toDto(messageService.updateMessage(id, msg));
    }

    @PatchMapping("{id}")
    public MessageDto partialUpdateMessage(@PathVariable("id") long id,
                                           @RequestBody Message msg) {
        return messageMapper.toDto(messageService.partialUpdateMessage(id, msg));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable("id") long id) {
        messageService.deleteById(id);
    }


}
