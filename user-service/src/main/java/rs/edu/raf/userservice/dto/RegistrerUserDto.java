package rs.edu.raf.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RegistrerUserDto {

    private String firstName;
    private String lastName;
    private String username;
    private String jmbg;
    private Long dateOfBirth;
    private String gender;
    private String phoneNumber;
    private String address;
    private String email;
    private String password;
}
