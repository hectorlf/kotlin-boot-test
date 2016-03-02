package hello.controller

import hello.model.Message
import hello.repository.MessageRepository

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping

object Loggers {
	val helloController = LoggerFactory.getLogger(HelloController::class.java)
}

@Controller
class HelloController @Autowired constructor (val messageRepository: MessageRepository) {

	@RequestMapping("/")
	fun index(model: ModelMap): String {
		val messages: List<Message> = messageRepository.findBySubject("Greet")
		Loggers.helloController.debug("Retrieved message list with {} elements", messages.size)
		model.addAttribute("messages", messages)
		return "index"
	}

	@RequestMapping("/secure/")
	fun secureIndex(): String {
		return "secure-index"
	}

}