package com.artgallery.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Loads environment variables from a `.env` file (using dotenv‑java) and configures a
 * {@link JavaMailSender} bean for SMTP email delivery.
 *
 * The `spring.mail.*` properties defined in {@code application.properties} reference
 * `${MAIL_USERNAME}` and `${MAIL_PASSWORD}`, which are populated from the `.env` file.
 */
@Configuration
public class EmailConfig {

    private static final Logger log = LoggerFactory.getLogger(EmailConfig.class);

    /** Load .env variables so that Spring's @Value placeholders can resolve them. */
    @Bean
    public Dotenv dotenv() {
        try {
            Dotenv env = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMissing()
                    .load();
            log.info("✅ .env loaded with {} entries", env.entries().size());
            return env;
        } catch (Exception e) {
            log.warn("⚠️ Failed to load .env: {}", e.getMessage());
            return null; // Spring will fall back to system env variables.
        }
    }

    /**
     * Create and configure the {@link JavaMailSender} bean using the properties resolved from the
     * Spring {@link Environment}. This bean is used by {@link com.artgallery.backend.service.EmailService}
     * to send OTP and notification emails.
     */
    @Bean
    public JavaMailSender javaMailSender(Environment env) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(env.getProperty("spring.mail.host"));
        mailSender.setPort(Integer.parseInt(env.getProperty("spring.mail.port", "587")));
        mailSender.setUsername(env.getProperty("spring.mail.username"));
        mailSender.setPassword(env.getProperty("spring.mail.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtp.starttls.enable", "true"));
        props.put("mail.debug", "true"); // Enable debug output for troubleshooting

        log.info("✅ JavaMailSender configured for {}:{}", mailSender.getHost(), mailSender.getPort());
        return mailSender;
    }
}
