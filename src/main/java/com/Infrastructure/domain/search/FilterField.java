package com.infrastructure.domain.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterField {
    private String field;
    private SearchOperation op;
    private Object value;
    private Object valueTo;
}
