package me.yeon.freship.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Response<T> {
    private T data;
    private PageInfo page;

    private Response(T data) {
        this.data = data;
    }

    private Response(T data, PageInfo page) {
        this.data = data;
        this.page = page;
    }

    // Pagination이 적용되지 않은 response 형식
    public static <T> Response<T> of(T data) {
        return new Response<>(data);
    }

    // Pagination이 적용된 response 형식
    public static <T> Response<T> of(T data, PageInfo page) {
        return new Response<>(data, page);
    }
}
