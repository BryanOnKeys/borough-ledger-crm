package com.boroughledger.crm.customers;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class CustomerDto {
    private CustomerDto() { }
    public record Request(
        @NotBlank @Size(max = 180) String name,
        @Email @Size(max = 190) String email,
        @Size(max = 40) String phone,
        @Size(max = 100) String industry
    ) { }
    public record Response(UUID id, String name, String email, String phone,
                           String industry, String status, LocalDateTime createdAt, long version) {
        public static Response from(Customer c) {
            return new Response(c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getIndustry(),
                    c.getStatus(), c.getCreatedAt(), c.getVersion());
        }
    }
}
