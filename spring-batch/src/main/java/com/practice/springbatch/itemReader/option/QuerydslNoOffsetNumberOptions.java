package dodobatch.task.itemReader.option;


import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import dodobatch.task.itemReader.expression.Expression;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuerydslNoOffsetNumberOptions<T, N extends Number & Comparable<?>> extends QuerydslNoOffsetOptions <T>{

    private N currentId;

    private final NumberPath<N> field;

    public QuerydslNoOffsetNumberOptions(@Nonnull NumberPath<N> field,
                                         @Nonnull Expression expression) {
        super(field, expression);
        this.field = field;
    }


    public N getCurrentId() {
        return currentId;
    }

    @Override
    public void initKeys(JPAQuery<T> query, int page) {
        if(page == 0) {
            initFirstId(query);

            if (log.isDebugEnabled()) {
                log.debug("First Key= "+currentId+"");
            }
        }
    }

    @Override
    protected void initFirstId(JPAQuery<T> query) {
        JPAQuery<T> clone = query.clone();
        boolean isGroupByQuery = isGroupByQuery(clone);

        if(isGroupByQuery) {
            currentId = clone
                    .select(field)
                    .orderBy(expression.isAsc()? field.asc() : field.desc())
                    .fetchFirst();
        } else {
            currentId = clone
                    .select(expression.isAsc()? field.min(): field.max())
                    .fetchFirst();
        }

    }

    @Override
    public JPAQuery<T> createQuery(JPAQuery<T> query, int page) {
        if(currentId == null) {
            return query;
        }

        return query
                .where(whereExpression(page))
                .orderBy(orderExpression());
    }

    private BooleanExpression whereExpression(int page) {
        return expression.where(field, page, currentId);
    }

    private OrderSpecifier<N> orderExpression() {
        return expression.order(field);
    }

    @Override
    public void resetCurrentId(T item) {
        //noinspection unchecked
        currentId = (N) getFiledValue(item);

        if (log.isDebugEnabled()) {
            log.debug("Current Select Key= " + currentId);
        }
    }

}