spring batch 5.x

AS-IS
10만개 기준
시간: 7m
```java
		HashMap<String, Object> param = new HashMap<>();
		param.put("date", jobParameter.getDate());
		return new JpaPagingItemReaderBuilder<User>()
				.parameterValues(param)
				.queryString("select u from User u where u.date = :date")
				.pageSize(chunkSize)
				.entityManagerFactory(entityManagerFactory)
				.name("reader")
				.build();
```

TO-BE
시간: 4m
```java
QuerydslNoOffsetNumberOptions<User, Long> options =
				new QuerydslNoOffsetNumberOptions<>(user.id, Expression.ASC);

		return new QuerydslZeroPagingItemReader<>(entityManagerFactory, chunkSize, options,
                jpaQueryFactory ->
                        jpaQueryFactory.selectFrom(user)
                                .where(user.date.eq(jobParameter.getDate()))
        );
```
