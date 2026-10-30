package mk.finki.ukim.lost_and_found.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, Instant>() {
            @Override
            public Instant convert(String source) {
                if (source == null || source.isBlank()) {
                    return null;
                }
                LocalDateTime localDateTime;
                if (source.length() == 16) {
                    localDateTime = LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                } else {
                    localDateTime = LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
                return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            }
        });
    }
}