package com.example.shortlink.shared;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static <T> PageResponse<T> from(Page<T> page) {
        PageResponse<T> res = new PageResponse<>();
        res.content = page.getContent();
        res.page = page.getNumber();
        res.size = page.getSize();
        res.totalElements = page.getTotalElements();
        res.totalPages = page.getTotalPages();
        return res;
    }
}
