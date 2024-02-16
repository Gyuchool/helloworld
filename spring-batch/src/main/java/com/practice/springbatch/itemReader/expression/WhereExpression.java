package dodobatch.task.itemReader.expression;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Created by jojoldu@gmail.com on 23/01/2020
 * Blog : http://jojoldu.tistory.com
 * Github : http://github.com/jojoldu
 */

/**
 * 첫페이지 조회시에는 >=, <=
 * 두번째 페이지부터는 >, <
 */
public enum WhereExpression {
    GT(
            (id, page, currentId) -> page == 0? id.goe(currentId): id.gt(currentId)
    ),
    LT(
            (id, page, currentId) -> page == 0? id.loe(currentId): id.lt(currentId)
    );

    private final WhereNumberFunction number;

    WhereExpression(WhereNumberFunction number) {
        this.number = number;
    }

    public <N extends Number & Comparable<?>>BooleanExpression expression (NumberPath<N> id, int page, N currentId) {
        return this.number.apply(id, page, currentId);
    }
}