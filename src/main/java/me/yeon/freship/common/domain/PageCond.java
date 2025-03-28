package me.yeon.freship.common.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageCond {
    private int pageNum;
    private int pageSize;

    public PageCond(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum == null ? 1 : pageNum;
        this.pageSize = pageSize == null ? 10 : pageSize;
    }
}
