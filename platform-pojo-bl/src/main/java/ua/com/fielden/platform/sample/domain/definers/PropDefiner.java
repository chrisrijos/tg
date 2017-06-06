package ua.com.fielden.platform.sample.domain.definers;

import ua.com.fielden.platform.entity.meta.IAfterChangeEventHandler;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.sample.domain.TgEntityWithPropertyDependency;
import ua.com.fielden.platform.utils.EntityUtils;

public class PropDefiner implements IAfterChangeEventHandler<String> {

    @Override
    public void handle(final MetaProperty<String> property, final String newValue) {
        final TgEntityWithPropertyDependency entity = (TgEntityWithPropertyDependency) property.getEntity();
        final String otherPropName = "dependentProp";
        
        if (!entity.isPersisted()) {
            if (EntityUtils.equalsEx(newValue, "IS")) {
                // try {
                //     Thread.sleep(2000);
                // } catch (final InterruptedException e) {
                //     e.printStackTrace();
                // }
                entity.set(otherPropName, "InService");
                // entity.getProperty(otherPropName).setEditable(false);
            }
        }
        
        if (!property.getEntity().isInitialising() && EntityUtils.equalsEx(newValue, "DR")) {
            entity.set(otherPropName, null);
            entity.getProperty(otherPropName).setRequired(true);
        }
    }

}
