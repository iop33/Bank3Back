package rs.edu.raf.exchangeservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Option implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    private Long lastRefresh; //kada smo mi povukli podatke
    private String contractSymbol;
    private String stockListing; //povezujemo ga sa Stock
    private String optionType; //calls ili puts

    @JsonProperty("strike")
    private double strikePrice;
    @JsonProperty("impliedVolatility")
    private double impliedVolatility;

    private int openInterest;
    @JsonProperty("expiration")
    private Long settlementDate;

    @JsonProperty("lastPrice")
    private double price;
    @JsonProperty("ask")
    private double ask;
    @JsonProperty("bid")
    private double bid;
    @JsonProperty("change")
    private double change;
    @JsonProperty("volume")
    private double volume;
}
