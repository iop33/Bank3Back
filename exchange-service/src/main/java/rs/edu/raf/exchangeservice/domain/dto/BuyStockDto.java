package rs.edu.raf.exchangeservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyStockDto {
    private Long employeeId;    //mora da stigne
    private String ticker;  //mora da stigne
    private Integer amount; //mora da stigne
    private Double limitValue;
    private Double stopValue;
    private boolean aon;
    private boolean margine;
}
