package ru.dageev.schedule.payload.response;

import lombok.Value;

import java.util.List;

@Value
public class JwtResponse {
  String token;
  String type = "Bearer";
  Long id;
  String username;
  String email;
  List<String> roles;
}
