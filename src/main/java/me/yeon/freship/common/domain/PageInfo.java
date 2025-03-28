package me.yeon.freship.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageInfo {
    private int pageNum;
    private int pageSize;
    private long totalElement;
    private int totalPage;

    @Builder
    public PageInfo(int pageNum, int pageSize, long totalElement, int totalPage) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalElement = totalElement;
        this.totalPage = totalPage;
    }

    public static PageInfo of(Page<?> page, int pageNum, int pageSize) {
        return PageInfo.builder()
                .pageNum(pageNum > 0 ? pageNum - 1 : 0)
                .pageSize(pageSize)
                .totalElement(page.getTotalElements())
                .totalPage(page.getTotalPages())
                .build();
    }
}
