package me.yeon.freship.member.domain;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberfindResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String phone;
    private final String address;

    public MemberfindResponse(Long id, String email, String name, String phone, String address) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
}