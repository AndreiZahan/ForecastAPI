package com.yonder.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CityWeather implements Comparable<CityWeather> {
    private String name;
    private List<Forecast> forecast;
    private static final Logger logger = LoggerFactory.getLogger(CityWeather.class);

    public String getAverageTemperature() {
        if (forecast == null || forecast.isEmpty()) {
            logger.info("Forecast is null or empty for city of {} when calculating average temperature", name);
            return "";
        }

        double sum = 0.0;
        for (Forecast dailyForecast : forecast) {
            try {
                sum += Double.parseDouble(dailyForecast.getTemperature());
            } catch (NumberFormatException e) {
                logger.error("Invalid temperature value: {}", dailyForecast.getTemperature());
            }
        }

        if (forecast.size() > 0) {
            double average = sum / forecast.size();
            return String.valueOf(decimalFormat(average));
        } else {
            logger.warn("Empty forecast for average temperature calculation");
            return "";
        }
    }

    public String getAverageWind() {
        if (forecast == null || forecast.isEmpty()) {
            logger.info("Forecast is null or empty for city of {} when calculating average wind speed", name);
            return "";
        }

        double sum = 0.0;
        for (Forecast dailyForecast : forecast) {
            try {
                sum += Double.parseDouble(dailyForecast.getWind());
            } catch (NumberFormatException e) {
                logger.error("Invalid wind speed value: {}", dailyForecast.getWind());
            }
        }

        if (forecast.size() > 0) {
            double average = sum / forecast.size();
            return String.valueOf(decimalFormat(average));
        } else {
            logger.warn("Empty forecast for average wind speed calculation");
            return "";
        }
    }

    private String decimalFormat(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(value);
    }

    @Override
    public int compareTo(CityWeather other) {
        return this.getName().compareTo(other.getName());
    }
}
