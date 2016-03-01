package hello.model

import javax.persistence.*

/**
 * Created by hlopez3 on 01/03/2016.
 */

@Entity
@Table(name="messages")
@SequenceGenerator(name="jpa_seq",sequenceName="jpa_seq",allocationSize=1)
data class Message (

        @Id
        @GeneratedValue(strategy= javax.persistence.GenerationType.SEQUENCE,generator="jpa_seq")
        var id: Long? = null,

        var subject: String? = null,

        @Column(nullable = false)
        var text: String? = null

)