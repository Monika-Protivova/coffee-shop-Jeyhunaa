package com.motycka.edu

import com.motycka.edu.config.AUTH_JWT
import com.motycka.edu.config.configureJWT
import com.motycka.edu.customer.CustomerRepositoryImpl
import com.motycka.edu.customer.InternalCustomerService
import com.motycka.edu.menu.MenuRepositoryImpl
import com.motycka.edu.menu.MenuService
import com.motycka.edu.menu.menuRoutes
import com.motycka.edu.security.AuthenticationService
import com.motycka.edu.security.JwtService
import com.motycka.edu.security.loginRoutes
import com.motycka.edu.user.UserRepositoryImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import com.motycka.edu.order.orderRoutes

private val logger = KotlinLogging.logger {}

const val BASE_API_PATH = "/api"

fun main() {
    // Load embedded server config from application.yaml
    val appConfig = io.ktor.server.config.ApplicationConfig("application.yaml")
    val deploymentConfig = appConfig.config("ktor.deployment")
    val serverPort = deploymentConfig.property("port").getString().toInt()
    val serverHost = deploymentConfig.propertyOrNull("host")?.getString() ?: "0.0.0.0"

    embeddedServer(Netty, port = serverPort, host = serverHost) {
        logger.info { "Application is starting with loaded configuration" }

        // Initialize database connections

        val menuRepo = MenuRepositoryImpl()
        val menuSrv = MenuService(menuRepository = menuRepo)
        val jwtService = JwtService(config = appConfig)
        val userRepo = UserRepositoryImpl()
        val authService = AuthenticationService(
            userRepository = userRepo,
            internalCustomerService = InternalCustomerService(CustomerRepositoryImpl()),
            jwtService = jwtService
        )

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }

        install(Authentication) {
            configureJWT(appConfig)
        }

        routing {
            loginRoutes(authService, BASE_API_PATH)

            authenticate(AUTH_JWT) {
                menuRoutes(menuSrv, BASE_API_PATH)
                orderRoutes(BASE_API_PATH, menuSrv)
            }
        }
    }.start(wait = true)
}
