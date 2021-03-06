package com.commercetools.sync.commons.helpers;

import com.commercetools.sync.categories.helpers.CategoryCustomActionBuilder;
import com.commercetools.sync.channels.helpers.ChannelCustomActionBuilder;
import com.commercetools.sync.commons.exceptions.BuildUpdateActionException;
import com.commercetools.sync.inventories.helpers.InventoryCustomActionBuilder;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.inventory.InventoryEntry;
import io.sphere.sdk.models.Resource;
import io.sphere.sdk.types.Custom;

import javax.annotation.Nonnull;

import static java.lang.String.format;

/**
 * Factory class that has the main objective of creating an instance of the of a concrete implementation of the
 * {@link GenericCustomActionBuilder}, which is responsible for building custom update actions, according to the type
 * of the {@code resource} instance provided to the {@link GenericCustomActionBuilderFactory#of(Custom)} function.
 */
@SuppressWarnings("unchecked")
public class GenericCustomActionBuilderFactory<T extends Custom & Resource<T>,
    S extends GenericCustomActionBuilder<T>> {
    private static final String UPDATE_ACTION_NOT_IMPLEMENTED = "Update actions for resource: '%s' is not implemented.";

    /**
     * Container of enums that provide mapping between the CTP resource and it's adjacent concrete custom action builder
     * class. For example, the {@link Category} resources point to the {@link CategoryCustomActionBuilder}
     * which contains implementations of update methods responsible for updating custom fields for categories.
     *
     * <p>Note: To add new concrete custom builders for new CTP resources, a new mapping has to be adding in this enum.
     */
    enum ConcreteBuilder {
        CATEGORY(CategoryCustomActionBuilder.class, Category.class),
        CHANNEL(ChannelCustomActionBuilder.class, Channel.class),
        INVENTORY(InventoryCustomActionBuilder.class, InventoryEntry.class);

        private final Class<? extends GenericCustomActionBuilder> builderClass;
        private final Class<? extends Resource> resourceClass;

        ConcreteBuilder(@Nonnull final Class<? extends GenericCustomActionBuilder> builderClass,
                        @Nonnull final Class<? extends Resource> resourceClass) {
            this.builderClass = builderClass;
            this.resourceClass = resourceClass;
        }

        /**
         * Returns the concrete custom action builder of {@code this} instance of the enum.
         *
         * @return the concrete custom action builder of {@code this} instance of the enum.
         */
        public Class<? extends GenericCustomActionBuilder> getBuilderClass() {
            return builderClass;
        }

        /**
         * Returns the resource class of {@code this} instance of the enum.
         *
         * @return the resource class of {@code this} instance of the enum.
         */
        public Class<? extends Resource> getResourceClass() {
            return resourceClass;
        }
    }

    /**
     * Given a {@code resource} of the type T which represents a CTP resource creates an instance of the concrete
     * implementation of the {@link GenericCustomActionBuilder}, which is responsible for building custom
     * update actions, according to the type of the {@code resource} instance provided. For example, if U
     * is of type {@link Category} this method will return an instance of {@link CategoryCustomActionBuilder}, if T
     * is of type {@link Channel} this method will return an instance of {@link ChannelCustomActionBuilder} instance.
     *
     * <p>The method uses the {@link ConcreteBuilder} enum to find the mapping of the concrete builder
     * from the CTP resource type.
     *
     * @param resource the resource from which a  concrete custom builder should be created, according to it's type.
     * @return an instance of the concrete implementation of the {@link GenericCustomActionBuilder} responsible for
     *      building custom update actions according to the type of the {@code resource} instance provided.
     * @throws BuildUpdateActionException thrown if no concrete implementation of {@link GenericCustomActionBuilder}
     *                                    exists yet.
     * @throws IllegalAccessException     if the {@code resource} class or its nullary constructor is not accessible.
     * @throws InstantiationException     if {@code resource} {@code Class} represents an abstract class,
     *                                    an interface, an array class, a primitive type, or void;
     *                                    or if the {@code resource} class has no nullary constructor;
     *                                    or if the instantiation fails for some other reason.
     */
    private S getBuilder(@Nonnull final T resource) throws BuildUpdateActionException,
        IllegalAccessException, InstantiationException {
        for (ConcreteBuilder concreteBuilder : ConcreteBuilder.values()) {
            final Class<? extends GenericCustomActionBuilder> builderClass = concreteBuilder.getBuilderClass();
            final Class<? extends Resource> resourceClass = concreteBuilder.getResourceClass();
            if (resourceClass.isInstance(resource)) {
                return (S) builderClass.newInstance();
            }
        }
        throw new BuildUpdateActionException(format(UPDATE_ACTION_NOT_IMPLEMENTED, resource.toReference().getTypeId()));
    }

    /**
     * Given a {@code resource} of the type U which represents a CTP resource (currently either Category or
     * Channel) creates an instance of the concrete implementation of the
     * {@link GenericCustomActionBuilder}, which is responsible for building custom update actions, according to
     * the type of the {@code resource} instance provided. For example, if U is of type {@link Category} this method
     * will return an instance of {@link CategoryCustomActionBuilder}, if U is of type {@link Channel} this method
     * will return an instance of {@link ChannelCustomActionBuilder} instance.
     *
     * @param resource the resource from which the type of the CTP resource to choose the update action builder.
     * @param <U>      the type of the resource.
     * @return an instance which represents a concrete implementation of the {@link GenericCustomActionBuilder}
     *      which is responsible for building custom update actions.
     * @throws BuildUpdateActionException exception thrown in case a concrete implementation of the the
     *                                    {@link GenericCustomActionBuilder} for the provided resource type is not
     *                                    implemented yet.
     * @throws IllegalAccessException     thrown by {@link #getBuilder(Custom)} if the {@code resource} class or its
     *                                    nullary constructor is not accessible.
     * @throws InstantiationException     thrown by {@link #getBuilder(Custom)} if {@code resource} {@code Class}
     *                                    represents an abstract class, an interface, an array class, a primitive type,
     *                                    or void; or if the {@code resource} class has no nullary constructor;
     *                                    or if the instantiation fails for some other reason.
     */
    public static <U extends Custom & Resource<U>> GenericCustomActionBuilder of(@Nonnull final U resource)
        throws BuildUpdateActionException, InstantiationException, IllegalAccessException {
        return new GenericCustomActionBuilderFactory().getBuilder(resource);
    }


}
