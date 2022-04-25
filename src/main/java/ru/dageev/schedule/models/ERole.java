package ru.dageev.schedule.models;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ERole {
  ROLE_STAFF("staff"),
  ROLE_ADMIN("admin");

  private final String name;

  ERole(String name) {
    this.name = name;
  }

  private final static Map<String, ERole> roleByName;

  static {
    roleByName = Arrays.stream(ERole.values())
            .collect(Collectors.toMap(role -> role.name, role -> role));
  }

  public static ERole findByName(String name) {
    return roleByName.get(name);
  }
}
