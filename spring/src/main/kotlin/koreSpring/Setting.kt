package koreSpring

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import org.apache.coyote.ProtocolHandler
import org.jetbrains.annotations.BlockingExecutor
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.support.TaskExecutorAdapter
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.File
import java.util.concurrent.Executors
import javax.sql.DataSource

val LOOM: @BlockingExecutor CoroutineDispatcher get() = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
@Configuration
@EnableAsync
class Setting(e: Environment): WebMvcConfigurer {

    @Bean
    fun applicationTaskExecutor():AsyncTaskExecutor = TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())
    @Bean
    fun protocolHandlerVirtualThreadExecutorCustomizer():TomcatProtocolHandlerCustomizer<*>{
        return TomcatProtocolHandlerCustomizer { protocolHandler:ProtocolHandler->
            protocolHandler.executor = Executors.newVirtualThreadPerTaskExecutor()
        }
    }
    @Bean(name = ["coroutineDispatcher"])
    fun coroutineDispatcher() = LOOM
//    @PostConstruct
//    fun afterStart(){
//
//    }

//    @Bean
//    fun httpSecurityConfig(http:HttpSecurity): SecurityFilterChain {
//        http.csrf{it.disable()}
//        return http.authorizeHttpRequests{authorize->
//            authorize.anyRequest().permitAll()
//        }.build()
//    }
//    override fun addCorsMappings(registry: CorsRegistry){
//        registry.addMapping("/**")
//            //.allowedOrigins(FORWARDER_API_QA, FORWARDER_URL_QA, FORWARDER_URL_DEV, FORWARDER_URL_PRODUCT, "http://localhost:8080", "http://localhost:8081")
//            .allowedMethods("*")
//            .exposedHeaders("Set-Cookie")
//            .allowCredentials(true)
//            .maxAge(3600)
//    }
//    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//        super.addResourceHandlers(registry)
//        val path = File(".").absoluteFile.parentFile.absolutePath
//        val location = "file:///$path//build//front//forwarder//"
//        registry.addResourceHandler("/static/**")
//            .addResourceLocations(location)
//            .setCachePeriod(0)
//        registry.addResourceHandler("/index.html")
//            .addResourceLocations(location + "index.html")
//            .setCachePeriod(0)
//        registry.addResourceHandler("/robots.txt")
//            .addResourceLocations(location + "robots.txt")
//            .setCachePeriod(0)
//        registry.addResourceHandler("/css/**")
//            .addResourceLocations(location + "css//")
//            .setCachePeriod(0)
//        registry.addResourceHandler("/js/**")
//            .addResourceLocations(location + "js//")
//            .setCachePeriod(0)
//        registry.addResourceHandler("/image/**")
//            .addResourceLocations(location + "image//")
//            .setCachePeriod(0)
//        registry.addResourceHandler("/template/**")
//            .addResourceLocations(location + "template//")
//            .setCachePeriod(0)
}
