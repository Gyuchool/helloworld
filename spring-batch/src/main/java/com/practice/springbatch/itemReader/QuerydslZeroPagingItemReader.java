package dodobatch.task.itemReader;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dodobatch.task.itemReader.option.QuerydslNoOffsetOptions;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.function.Function;

public class QuerydslZeroPagingItemReader<T> extends QuerydslPagingItemReader<T> {

    private QuerydslNoOffsetOptions<T> options;

    public QuerydslZeroPagingItemReader() {
        super();
        setName(ClassUtils.getShortName(QuerydslZeroPagingItemReader.class));
    }

    public QuerydslZeroPagingItemReader(EntityManagerFactory entityManagerFactory,
                                        int pageSize,
                                        Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
        this(entityManagerFactory, pageSize, null, queryFunction);
    }

    public QuerydslZeroPagingItemReader(EntityManagerFactory entityManagerFactory,
                                        int pageSize,
                                        QuerydslNoOffsetOptions<T> options,
                                        Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
        this();
        setTransacted(true);
        this.options = options;
        super.entityManagerFactory = entityManagerFactory;
        super.queryFunction = queryFunction;
        setPageSize(pageSize);
    }

    @Override
    protected void doReadPage() {

        EntityTransaction tx = getTxOrNull();

        JPQLQuery<T> query = createQuery(options)
                .offset(0)
                .limit(getPageSize());

        initResults();

        fetchQuery(query, tx);

        resetCurrentIdIfNotLastPage();
    }

    private void resetCurrentIdIfNotLastPage() {
        if (isNotEmptyResults()) {
            options.resetCurrentId(getLastItem());
        }
    }

    // 조회결과가 Empty이면 results에 null이 담긴다
    private boolean isNotEmptyResults() {
        return !CollectionUtils.isEmpty(results) && results.get(0) != null;
    }

    private T getLastItem() {
        return results.get(results.size() - 1);
    }
}
