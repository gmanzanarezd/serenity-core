package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.serenitybdd.core.webdriver.servicepools.EdgeServicePool;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_USE_DRIVER_SERVICE_POOL;

public class EdgeDriverProvider implements DriverProvider {

    private final DriverCapabilityRecord driverProperties;

    private final DriverServicePool driverServicePool = new EdgeServicePool();

    private final FixtureProviderService fixtureProviderService;

    public EdgeDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        DesiredCapabilities desiredCapabilities = enhancer.enhanced(DesiredCapabilities.edge(), SupportedWebDriver.EDGE);
        driverProperties.registerCapabilities("edge", capabilitiesToProperties(desiredCapabilities));

        return ProvideNewDriver.withConfiguration(environmentVariables,
                desiredCapabilities,
                driverServicePool,
                DriverServicePool::newDriver,
                (pool, caps) -> new EdgeDriver(caps)
        );
    }
}
