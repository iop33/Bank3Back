package rs.edu.raf.exchangeservice.client;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rs.edu.raf.exchangeservice.domain.dto.StockTransactionDto;

@FeignClient(name = "bankServiceClient", url = "${bankServiceLocation}")
public interface BankServiceClient {

    @PostMapping(value = "/transaction/stockBuyTransaction",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "kada banka kupuje nesto od Exchange")
    ResponseEntity<?> stockBuyTransaction (@RequestBody StockTransactionDto stockTransactionDto);

    @PostMapping(value = "/transaction/stockSellTransaction",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "kada banka prodaje nesto Exchange-u")
    ResponseEntity<?> stockSellTransaction (@RequestBody StockTransactionDto stockTransactionDto);
}
