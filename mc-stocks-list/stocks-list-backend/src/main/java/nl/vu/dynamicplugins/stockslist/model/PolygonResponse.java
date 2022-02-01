package nl.vu.dynamicplugins.stockslist.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolygonResponse {
    String ticker;
    List<Map<String, Object>> results;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public List<Map<String, Object>> getResults() {
        return results;
    }

    public void setResults(List<Map<String, Object>> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "PolygonResponse{" +
                "ticker='" + ticker + '\'' +
                ", results=" + results +
                '}';
    }
}
