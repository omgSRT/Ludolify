package com.omgsrt.Ludolify.v1.account;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document(collection = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    @Id
    @Builder.Default
    UUID accountId = UUID.randomUUID();
    String username;
    String handle;
    Date dateOfBirth;
    String bio;
    String status;
    String email;
    String phone;
    String password;
    String profilePicture;
    String bannerPicture;
    Date createdAt;
    Date updatedAt;
    Date lastLoginAt;

    Set<UUID> roleId = new HashSet<>();
}
