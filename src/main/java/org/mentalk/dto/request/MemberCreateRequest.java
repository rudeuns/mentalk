package org.mentalk.dto.request;

import org.mentalk.enums.Role;

public record MemberCreateRequest(String email,
                                  String password,
                                  String username,
                                  String phoneNumber,
                                  Role role) {

}
