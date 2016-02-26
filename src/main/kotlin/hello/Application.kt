package hello

import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java)
}

@SpringBootApplication(exclude = arrayOf(SecurityAutoConfiguration::class, ManagementWebSecurityAutoConfiguration::class))
@ServletComponentScan
open class Application {

    @Bean
    open fun servletContainer(): EmbeddedServletContainerFactory {
        val containerFactory = UndertowEmbeddedServletContainerFactory()
        containerFactory.addBuilderCustomizers(UndertowBuilderCustomizer { builder ->
            val address = if (containerFactory.address == null) "0.0.0.0" else containerFactory.address.hostAddress
            builder.addHttpListener(8080, address)
        })
        return containerFactory
    }

}


@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
open class ApplicationSecurity : WebSecurityConfigurerAdapter() {

    private val MANAGEMENT_ENDPOINTS = arrayOf("/management/dump", "/management/health", "/management/metrics", "/management/trace")

    @Bean
    @ConditionalOnMissingBean
    open fun securityProperties(): SecurityProperties {
        return SecurityProperties()
    }

    override fun configure(http: HttpSecurity) {
        // general properties
        val props = securityProperties()
        if (props.isRequireSsl) http.requiresChannel().anyRequest().requiresSecure()
        if (!props.isEnableCsrf) http.csrf().disable()
        if (!props.headers.isFrame) http.headers().frameOptions().disable()
        if (!props.headers.isContentType) http.headers().contentTypeOptions().disable()
        if (!props.headers.isXss) http.headers().xssProtection().disable()
        if (props.headers.hsts != SecurityProperties.Headers.HSTS.NONE) http.headers().httpStrictTransportSecurity().includeSubDomains(props.headers.hsts == SecurityProperties.Headers.HSTS.ALL)
        http.sessionManagement().sessionCreationPolicy(props.sessions)
        // management access rules
        http.requiresChannel().antMatchers(*MANAGEMENT_ENDPOINTS).requiresSecure()
        http.authorizeRequests().antMatchers(*MANAGEMENT_ENDPOINTS).hasRole("ADMIN").and().httpBasic()
        // app access rules
        http.authorizeRequests().antMatchers("/secure/**").hasRole("USER").and().httpBasic()
        // default access rules
        http.authorizeRequests().antMatchers("/**").permitAll().and().httpBasic()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication().withUser("user").password("user").roles("USER")
                .and().withUser("admin").password("admin").roles("USER","ADMIN")
    }

}