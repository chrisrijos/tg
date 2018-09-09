package ua.com.fielden.platform.serialisation.spike;

import java.nio.ByteBuffer;

import ua.com.fielden.platform.domaintree.impl.DomainTreeEnhancerCache;
import ua.com.fielden.platform.entity.factory.EntityFactory;
import ua.com.fielden.platform.ioc.ApplicationInjectorFactory;
import ua.com.fielden.platform.serialisation.api.SerialiserEngines;
import ua.com.fielden.platform.serialisation.api.impl.ProvidedSerialisationClassProvider;
import ua.com.fielden.platform.serialisation.api.impl.Serialiser;
import ua.com.fielden.platform.test.CommonTestEntityModuleWithPropertyFactory;

import com.esotericsoftware.kryo.Kryo;
import com.google.inject.Injector;

public class KryoSpikeForFinalProperties {

    public static void main(final String[] args) throws Exception {
        System.out.println("kryo for final props");

        final Injector injector = new ApplicationInjectorFactory().add(new CommonTestEntityModuleWithPropertyFactory()).getInjector();
        final EntityFactory factory = injector.getInstance(EntityFactory.class);

        final ProvidedSerialisationClassProvider provider = new ProvidedSerialisationClassProvider(DomainType1.class, TypeWithFinalProperty.class);
        final Kryo kryoWriter = (Kryo) new Serialiser(factory, provider, new DomainTreeEnhancerCache()).getEngine(SerialiserEngines.KRYO);
        final Kryo kryoReader = (Kryo) new Serialiser(factory, provider, new DomainTreeEnhancerCache()).getEngine(SerialiserEngines.KRYO);

        final TypeWithFinalProperty finalInstance = new TypeWithFinalProperty(25);

        final ByteBuffer buffer = ByteBuffer.allocateDirect(128);
        kryoWriter.writeObject(buffer, finalInstance);
        buffer.flip();
        final byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        buffer.clear();
        final TypeWithFinalProperty restoredFinalInstance = kryoReader.readObject(ByteBuffer.wrap(data), TypeWithFinalProperty.class);
        System.out.println(restoredFinalInstance.getIntField());
    }
}
