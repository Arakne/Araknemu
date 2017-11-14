package fr.quatrevieux.araknemu.core.di;

/**
 * Module for configure a container
 */
public interface ContainerModule {
    /**
     * Configure the container
     */
    public void configure(ContainerConfigurator configurator);
}
