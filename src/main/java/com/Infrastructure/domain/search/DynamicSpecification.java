package com.infrastructure.domain.search;

import com.infrastructure.exceptions.BaseException;
import com.infrastructure.exceptions.GeneralExceptionType;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class DynamicSpecification<T> implements Specification<T> {

    private final List<FilterField> filters;

    public DynamicSpecification(List<FilterField> filters) {
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root<T> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();

        for (FilterField filter : filters) {

            Path<?> path = resolvePath(root, filter.getField());

            Predicate predicate = buildPredicate(filter, path, cb);

            predicates.add(predicate);
        }

        query.distinct(true);

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Path<?> resolvePath(Root<T> root, String field) {

        if (!field.contains(".")) {
            return root.get(field);
        }

        String[] parts = field.split("\\.");
        From<?, ?> join = root;

        for (int i = 0; i < parts.length - 1; i++) {
            join = join.join(parts[i], JoinType.LEFT);
        }

        return join.get(parts[parts.length - 1]);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate buildPredicate(FilterField filter,
                                     Path<?> path,
                                     CriteriaBuilder cb) {

        Object value = filter.getValue();

        switch (filter.getOp()) {

            case EQUAL:
                return cb.equal(path, value);

            case NOT_EQUAL:
                return cb.notEqual(path, value);

            case LIKE:
                return cb.like(
                        cb.lower((Expression) path),
                        "%" + value.toString().toLowerCase() + "%"
                );

            case STARTS_WITH:
                return cb.like(
                        cb.lower((Expression) path),
                        value.toString().toLowerCase() + "%"
                );

            case ENDS_WITH:
                return cb.like(
                        cb.lower((Expression) path),
                        "%" + value.toString().toLowerCase()
                );

            case GREATER_THAN:
                return cb.greaterThan(
                        (Expression) path,
                        (Comparable) value
                );

            case GREATER_THAN_EQUAL:
                return cb.greaterThanOrEqualTo(
                        (Expression) path,
                        (Comparable) value
                );

            case LESS_THAN:
                return cb.lessThan(
                        (Expression) path,
                        (Comparable) value
                );

            case LESS_THAN_EQUAL:
                return cb.lessThanOrEqualTo(
                        (Expression) path,
                        (Comparable) value
                );

            case BETWEEN:
                return cb.between(
                        (Expression) path,
                        (Comparable) value,
                        (Comparable) filter.getValueTo()
                );

            case IN:
                return path.in(convertStringToArrayStream(value));

            case IS_NULL:
                return cb.isNull(path);

            case IS_NOT_NULL:
                return cb.isNotNull(path);

            default:{
                log.debug("Operation not supported");
                throw new BaseException(GeneralExceptionType.UNKNOWN_ERROR);
            }

        }
    }

    private Object[] convertStringToArrayStream(Object input) {
        if(input==null)
            return null;
        return Arrays.stream(input.toString().replace("[", "").replace("]", "").split(","))
                .map(String::trim)
                .map(part -> {
                    return (Object) part;
                })
                .toArray();
    }
}
