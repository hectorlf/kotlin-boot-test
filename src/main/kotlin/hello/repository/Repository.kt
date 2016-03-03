package hello.repository

import hello.model.Message

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

interface MessageRepository: JpaRepository<Message, Long> {

	@Query("select m from Message m where m.subject like ?1%")
	fun findBySubject(subject: String): List<Message>

}