package hello

import io.undertow.Undertow
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.context.annotation.Bean

@SpringBootApplication(exclude = arrayOf(SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class))
@ServletComponentScan
class Application {

    fun main(args: Array<String>) {
        SpringApplication.run(Application::class)
    }

    @Bean
    fun servletContainer(): EmbeddedServletContainerFactory {
        val containerFactory = UndertowEmbeddedServletContainerFactory();
        containerFactory.addBuilderCustomizers(UndertowBuilderCustomizer { builder ->
            val address = if (containerFactory.address == null) "0.0.0.0" else containerFactory.address.hostAddress
            builder.addHttpListener(8080, address)
        })
        return containerFactory
    }

}