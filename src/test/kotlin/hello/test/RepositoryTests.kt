package hello.test

import hello.Application
import hello.model.Message
import hello.repository.MessageRepository
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * Created by hlopez3 on 04/03/2016.
 */

@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(Application::class)
class JpaRepositoryTests {

    @Autowired
    private val messageRepository: MessageRepository? = null

    @Test
    fun testInsertion() {
        val m: Message = Message(subject="Hi!", text="I'm not spam!")
        messageRepository?.saveAndFlush(m)
    }

    @Test(expected= DataIntegrityViolationException::class)
    fun testConstraintViolation1() {
        val m: Message = Message()
        messageRepository?.saveAndFlush(m)
    }

    @Test
    fun testFindBySubjectOk() {
        val messages: List<Message>? = messageRepository?.findBySubject("Greet")
        Assert.assertEquals(1, messages?.size)
    }

}