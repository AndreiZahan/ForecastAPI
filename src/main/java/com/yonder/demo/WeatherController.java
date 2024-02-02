package com.yonder.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
class WeatherController {

    @Value("${accepted.cities}")
    private List<String> acceptedCities;

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    private final RestTemplate restTemplate;

    @Autowired
    public WeatherController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        logger.info("WeatherController instantiated"); // TODO to be deleted after everything works fine
    }

    @GetMapping("/api/weather")
    public WeatherResponse getWeather(@RequestParam String city) {

        logger.info("Request received for city: {}", city);

        String[] cities = city.split(",");
        List<CityWeather> cityWeatherList = new ArrayList<>();
        Set<String> citiesSet = new HashSet<>(List.of(cities));

        for (String cityName : citiesSet) {
            if (acceptedCities.contains(cityName)) {
                try {
                    String apiUrl = "https://8a5b7f27-5ae0-4fe2-9f31-ed20df36afa4.mock.pstmn.io/" + cityName;
                    CityWeather cityWeather = restTemplate.getForObject(apiUrl, CityWeather.class);
                    if (cityWeather != null) {
                        cityWeather.setName(cityName); // hotfix for cityName, since all API calls return null name
                        cityWeatherList.add(cityWeather);
                    }
                } catch (Exception e) {
                    logger.error("No forecast data found for {}", cityName);
                    CityWeather cityWeather = CityWeather.builder().name(cityName).build();
                    cityWeatherList.add(cityWeather);
                }
            }
        }

        Collections.sort(cityWeatherList);

        writeToCSV(cityWeatherList);

        return new WeatherResponse(cityWeatherList);
    }

    private void writeToCSV(List<CityWeather> cityWeatherList) {
        try (FileWriter csvWriter = new FileWriter("weather.csv")) {
            csvWriter.append("Name, temperature, wind\n");

            for (CityWeather cityWeather : cityWeatherList) {
                csvWriter.append(cityWeather.getName())
                        .append(",")
                        .append(cityWeather.getAverageTemperature())
                        .append(",")
                        .append(cityWeather.getAverageWind())
                        .append("\n");
            }

        } catch (IOException e) {
            // intentionally left empty
        }
    }
}
