package ru.task.alfa.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.task.alfa.task.configuration.ApplicationConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@RestController
public class TaskController {
    private ApplicationConfiguration configuration;
    private String str;

    @Autowired
    public TaskController(ApplicationConfiguration conf, @Qualifier(value = "test_bean") String str){
        this.configuration = conf;
        this.str = str;
    }

    @GetMapping(path = "task")
    public ResponseEntity<?> getHello() throws IOException {
        System.out.println(str);
        System.out.println(configuration.getExchangeApiId());
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.now().minusDays(1);
        Double exchangeRub = getExchangeRUB("historical/" + date.format(formatters)+ ".json");
        Double exchangeRubCur = getExchangeRUB("latest.json");
        TmpGetResponse responseGif;
        if (exchangeRub < exchangeRubCur) {
            responseGif = getGif("rich");
        } else {
            responseGif = getGif("broke");
        }

        return ResponseEntity.status(200)
                .body(String.format("<img src=\"%s\" width=\"%s\" height=\"%s\"/>",
                        responseGif.getUrl(), responseGif.getWidth(), responseGif.getHeight())
                );
    }

    //"<img src=\"broke.gif\" width=\"200\" height=\"200\"/>"

    private Double getExchangeRUB(String date) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://openexchangerates.org/api/" + date + "?app_id=8b6da92c98564446a6ab0dfe49d6e680&base=USD");
        CloseableHttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        StringBuilder json = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            json.append(line);
        }
        return objectMapper.readTree(json.toString()).get("rates").get("RUB").asDouble();
    }

    private TmpGetResponse getGif(String state) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://api.giphy.com/v1/gifs/random?api_key=dsr2J0Tt2Kxl47QlKQDM9dReBrTqCIu4&tag=" + state);
        CloseableHttpResponse response = client.execute(request);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String line;
        StringBuilder json = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            json.append(line);
        }
        return TmpGetResponse.builder()
                .url(objectMapper.readTree(json.toString()).get("data").get("images").get("downsized_large").get("url").asText())
                .width(objectMapper.readTree(json.toString()).get("data").get("images").get("downsized_large").get("width").asText())
                .height(objectMapper.readTree(json.toString()).get("data").get("images").get("downsized_large").get("height").asText())
                .build();
    }


}
