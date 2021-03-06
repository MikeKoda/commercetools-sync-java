package com.commercetools.sync.commons.utils;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.queries.QueryDsl;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.sphere.sdk.queries.QueryExecutionUtils.DEFAULT_PAGE_SIZE;

public final class CtpQueryUtils {

    private CtpQueryUtils() {
    }

    /**
     * Queries all elements matching a query by using an offset based pagination with page size 500.
     * The method takes a callback {@link Function} that returns a result of type {@code <S>} that is returned on every
     * page of elements queried. Eventually, the method returns a {@link CompletionStage} that contains a list of all
     * the results of the callbacks returned from every page.
     *
     * @param client   commercetools client
     * @param query    query containing predicates and expansion paths
     * @param callBack callback function that is called on every page queried.
     * @param <T>      type of one query result element
     * @param <C>      type of the query
     * @param <S>      type of the returned result of the callback function on every page.
     * @return elements
     */
    @Nonnull
    public static <T, C extends QueryDsl<T, C>, S> CompletionStage<List<S>>
        queryAll(@Nonnull final SphereClient client, @Nonnull final QueryDsl<T, C> query,
                 @Nonnull final Function<List<T>, S> callBack) {
        return queryAll(client, query, callBack, DEFAULT_PAGE_SIZE);
    }

    /**
     * Queries all elements matching a query by using an offset based pagination with page size 500. The method takes a
     * consumer {@link Consumer} that is applied on on every page of elements queried.
     *
     * @param client commercetools client
     * @param query  query containing predicates and expansion paths
     * @param consumer that is applied on every page queried.
     * @param <T>    type of one query result element
     * @param <C>    type of the query
     * @return elements
     */
    @Nonnull
    public static <T, C extends QueryDsl<T, C>> CompletionStage<Void>
        queryAll(@Nonnull final SphereClient client, @Nonnull final QueryDsl<T, C> query,
                 @Nonnull final Consumer<List<T>> consumer) {
        return queryAll(client, query, consumer, DEFAULT_PAGE_SIZE);
    }

    /**
     * Queries all elements matching a query by using an offset based pagination. The method takes a callback
     * {@link Function} that returns a result of type {@code <S>} that is returned on every page of elements queried.
     * Eventually, the method returns a {@link CompletionStage} that contains a list of all the results of the
     * callbacks returned from every page.
     *
     * @param client   commercetools client
     * @param query    query containing predicates and expansion paths
     * @param callback callback function that is called on every page queried.
     * @param <T>      type of one query result element
     * @param <C>      type of the query
     * @param <S>      type of the returned result of the callback function on every page.
     * @param pageSize the page size.
     * @return elements
     */
    @Nonnull
    public static <T, C extends QueryDsl<T, C>, S> CompletionStage<List<S>>
        queryAll(@Nonnull final SphereClient client, @Nonnull final QueryDsl<T, C> query,
                 @Nonnull final Function<List<T>, S> callback, final int pageSize) {
        return QueryAll.of(query, pageSize).run(client, callback);
    }

    /**
     * Queries all elements matching a query by using an offset based pagination. The method takes a consumer
     * {@link Consumer} that is applied on on every page of elements queried.
     *
     * @param client commercetools client
     * @param query  query containing predicates and expansion paths
     * @param consumer that is applied on every page queried.
     * @param <T>    type of one query result element
     * @param <C>    type of the query
     * @param pageSize the page size.
     * @return elements
     */
    @Nonnull
    public static <T, C extends QueryDsl<T, C>> CompletionStage<Void>
        queryAll(@Nonnull final SphereClient client, @Nonnull final QueryDsl<T, C> query,
                 @Nonnull final Consumer<List<T>> consumer, final int pageSize) {
        return QueryAll.of(query, pageSize).run(client, consumer);
    }
}
