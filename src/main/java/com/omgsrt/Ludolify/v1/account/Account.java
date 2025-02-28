package com.omgsrt.Ludolify.v1.account;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    @Field("_id")
    ObjectId id;
    String username;
    String handle;
    Date dateOfBirth;
    String bio;
    AccountStatus status;
    String email;
    String phone;
    String password;
    String profilePicture;
    String bannerPicture;
    Date createdAt;
    Date updatedAt;
    Date lastSignInAt;

    Set<ObjectId> roleIds;
}
