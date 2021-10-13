
package ru.task.alfa.task.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ApplicationConfiguration {
    @Value("${gif_api_id}")
    private String gifApiId;
    @Value("${exchange_api_id}")
    private String exchangeApiId;

}
