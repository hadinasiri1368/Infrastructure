package com.infrastructure.domain.search;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FilterRequest {
    private List<FilterField> filters = new ArrayList<>();
    private Integer pageNumber;
    private Integer pageSize;
}
