package ua.com.fielden.platform.web.view.master.api.compound.impl;

import static ua.com.fielden.platform.types.tuples.T2.t2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.inject.Injector;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.AbstractFunctionalEntityForCompoundMenuItem;
import ua.com.fielden.platform.entity.AbstractFunctionalEntityWithCentreContext;
import ua.com.fielden.platform.entity.IEntityProducer;
import ua.com.fielden.platform.types.tuples.T2;
import ua.com.fielden.platform.web.app.config.IWebUiBuilder;
import ua.com.fielden.platform.web.centre.EntityCentre;
import ua.com.fielden.platform.web.centre.api.actions.EntityActionConfig;
import ua.com.fielden.platform.web.view.master.EntityMaster;
import ua.com.fielden.platform.web.view.master.api.ICompoundMasterBuilder;
import ua.com.fielden.platform.web.view.master.api.compound.Compound;
import ua.com.fielden.platform.web.view.master.api.compound.ICompoundMaster0;
import ua.com.fielden.platform.web.view.master.api.compound.ICompoundMaster1;
import ua.com.fielden.platform.web.view.master.api.compound.ICompoundMaster2;
import ua.com.fielden.platform.web.view.master.api.compound.ICompoundMaster3;
import ua.com.fielden.platform.web.view.master.api.compound.ICompoundMaster4;
import ua.com.fielden.platform.web.view.master.api.compound.ICompoundMaster5;
import ua.com.fielden.platform.web.view.master.api.compound.ICompoundMaster6;
import ua.com.fielden.platform.web.view.master.api.compound.ICompoundMaster6CentreView;
import ua.com.fielden.platform.web.view.master.api.compound.ICompoundMaster7;

public class CompoundMasterBuilder<T extends AbstractEntity<?>, F extends AbstractFunctionalEntityWithCentreContext<T>> extends Compound implements ICompoundMasterBuilder<T, F>, ICompoundMaster0<T, F>, ICompoundMaster1<T, F>, ICompoundMaster2<T, F>, ICompoundMaster3<T, F>, ICompoundMaster4<T, F>, ICompoundMaster5<T, F>, ICompoundMaster6<T, F>, ICompoundMaster6CentreView<T, F>, ICompoundMaster7<T, F> {
    private final Injector injector;
    private final IWebUiBuilder builder;
    private Class<F> type;
    private Class<? extends IEntityProducer<F>> producerType;
    private int defaultMenuItemNumber = 0;
    private final List<T2<EntityActionConfig, Optional<String>>> menuItems = new ArrayList<>();
    private Class<? extends AbstractFunctionalEntityForCompoundMenuItem<T>> currentMenuItemType;
    private String currentIcon;
    private String currentShortDesc;
    private String currentLongDesc;

    private CompoundMasterBuilder(final Injector injector, final IWebUiBuilder builder) {
        this.injector = injector;
        this.builder = builder;
    }

    public static <T extends AbstractEntity<?>, F extends AbstractFunctionalEntityWithCentreContext<T>> ICompoundMasterBuilder<T, F> create(final Injector injector, final IWebUiBuilder builder) {
        return new CompoundMasterBuilder<>(injector, builder);
    }

    @Override
    public ICompoundMaster2<T, F> also() {
        return this;
    }

    @Override
    public EntityMaster<F> done() {
        final EntityMaster<F> master = new EntityMaster<>(
                type,
                producerType,
                new MasterWithMenu<>(type, menuItems, defaultMenuItemNumber),
                injector);
        builder.register(master);
        return master;
    }

    @Override
    public ICompoundMaster7<T, F> withView(final EntityMaster<?> embeddedMaster) {
        builder.register(miMaster(currentMenuItemType, builder.register(embeddedMaster), injector));
        menuItems.add(t2(miAction(currentMenuItemType, currentIcon, currentShortDesc, currentLongDesc), Optional.empty()));
        return this;
    }

    @Override
    public ICompoundMaster6CentreView<T, F> withView(final EntityCentre<?> embeddedCentre) {
        builder.register(miCentre(currentMenuItemType, builder.register(embeddedCentre), injector));
        menuItems.add(t2(miAction(currentMenuItemType, currentIcon, currentShortDesc, currentLongDesc), Optional.empty()));
        return this;
    }

    @Override
    public ICompoundMaster6<T, F> longDesc(final String longDesc) {
        this.currentLongDesc = longDesc;
        return this;
    }

    @Override
    public ICompoundMaster5<T, F> shortDesc(final String shortDesc) {
        this.currentShortDesc = shortDesc;
        return this;
    }

    @Override
    public ICompoundMaster4<T, F> icon(final String icon) {
        this.currentIcon = icon;
        return this;
    }

    @Override
    public ICompoundMaster3<T, F> addMenuItem(final Class<? extends AbstractFunctionalEntityForCompoundMenuItem<T>> type) {
        this.currentMenuItemType = type;
        return this;
    }

    @Override
    public ICompoundMaster2<T, F> andDefaultItemNumber(final int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Specify menu item number, that is greater or equal to zero.");
        }
        this.defaultMenuItemNumber = number;
        return this;
    }

    @Override
    public ICompoundMaster1<T, F> withProducer(final Class<? extends IEntityProducer<F>> producerType) {
        this.producerType = producerType;
        return this;
    }

    @Override
    public ICompoundMaster0<T, F> forEntity(final Class<F> type) {
        this.type = type;
        return this;
    }

    @Override
    public ICompoundMaster7<T, F> represents(final String property) {
        final T2<EntityActionConfig, Optional<String>> lastView = menuItems.get(menuItems.size() - 1);
        menuItems.set(menuItems.size() - 1, t2(lastView._1, Optional.of(property)));
        return this;
    }
}
