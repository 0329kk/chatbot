package com.example.chatbot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.example.chatbot.entity.IncomingMessage;
import com.example.chatbot.entity.MessagingError;
import com.example.chatbot.entity.OutMessage;



//@Controller
//
//public class chatController {
//    @MessageMapping("/hello")
//    @SendTo("/topic/greetings")
//    public Greeting greeting(Message message) throws Exception {
//        Thread.sleep(1000); // simulated delay
//        return new Greeting(HtmlUtils.htmlEscape(message.getName())
//                + " : "
//                + HtmlUtils.htmlEscape(message.getMessage()) );
//    }
//    
//    
//    @GetMapping("/")
//    public String chatapp() {
//    	return "chat";
//    }
//}
@Controller
public class chatController {

    private static final Logger logger = LoggerFactory.getLogger(chatController.class);

    @MessageMapping("/chat.send/{topic}")
	@SendTo("/topic/messages")
	public OutMessage sendMessage(@DestinationVariable("topic") String topic, @Payload IncomingMessage message) {
		logger.info("sendMessage() {content: " +  message.getContent() + ", sender: " + message.getSender() + ", timestamp: " + message.getTimestamp() + "}");
		OutMessage out = new OutMessage(message.getType(), message.getContent(), message.getSender(), topic);
		return out;
	}
	
	@MessageMapping("/chat.adduser")
	@SendTo("/topic/messages")
	public OutMessage addUser(@Payload IncomingMessage message, SimpMessageHeaderAccessor headerAccessor) {
		logger.info("addUser : " +  message.getType() + " (" + message.getSender());
		headerAccessor.getSessionAttributes().put("username", message.getSender());
		OutMessage out = new OutMessage(message.getType(), message.getContent(), message.getSender(), null);
		return out;
	}
	
	@MessageExceptionHandler
	@SendToUser("/queue/errors")
	public MessagingError handleException(Throwable exception) {
		MessagingError error = new MessagingError();
		error.setMessage(exception.getMessage());
		return error;
	}

}
