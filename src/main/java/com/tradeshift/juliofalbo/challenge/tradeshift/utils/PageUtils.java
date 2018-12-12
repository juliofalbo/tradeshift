package com.tradeshift.juliofalbo.challenge.tradeshift.utils;

import com.tradeshift.juliofalbo.challenge.tradeshift.exceptions.PageNotFountException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class PageUtils {

    public static <T> PageImpl<T> convertListToPage(List<T> list, Integer page, Integer size){
        PageRequest pageable = PageRequest.of(page, size);
        Long start = pageable.getOffset();

        if (start > list.size()) {
            throw new PageNotFountException("There is no " + page + " page(s) for this query");
        }

        Long end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        return new PageImpl<>(list.subList(start.intValue(), end.intValue()), pageable, list.size());
    }

}
