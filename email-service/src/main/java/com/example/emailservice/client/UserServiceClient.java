package com.example.emailservice.client;

import com.example.emailservice.dto.ResetPasswordDto;
import com.example.emailservice.dto.ResetUserPasswordDto;
import com.example.emailservice.dto.SetPasswordDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/***
 *Salje se post zahtev na URL, doda se /setPassword na kraj, prima i salje json.
 *Kada se pozove metoda i posalje objekat, u pozadini se izvrsava http zahtev
 *
 */


@FeignClient(name = "userServiceClient", url = "${userServiceLocation}")
public interface UserServiceClient {

    @PostMapping(value = "/user/setPassword",
            produces = "application/json",
            consumes = "application/json")
    ResponseEntity<String> setUserPassword(@RequestBody SetPasswordDto passwordDTO);

    @PostMapping(value = "/employee/setPassword",
            produces = "application/json",
            consumes = "application/json")
    ResponseEntity<String> setPassword(@RequestBody SetPasswordDto passwordDTO);




    /**
     * Meotda za komunikaciju sa user servisom, prosledjuje se mejl i nova sifra zaposlenog
     */
    @PostMapping(value = "/employee/resetPassword",
            produces = "application/json"
            , consumes = "application/json"
    )
    ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto passwordDTO);

    @PostMapping(value = "/user/resetPassword",
            produces = "application/json"
            , consumes = "application/json"
    )
    ResponseEntity<String> resetUserPassword(@RequestBody ResetUserPasswordDto passwordDTO);
}
