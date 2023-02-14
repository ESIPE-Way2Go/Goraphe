package fr.esipe.way2go.service.impl;

import fr.esipe.way2go.Way2GoApplication;
import fr.esipe.way2go.configuration.WebSecurityConfiguration;
import fr.esipe.way2go.dao.SimulationEntity;
import fr.esipe.way2go.dao.UserEntity;
import fr.esipe.way2go.service.SimulationService;
import fr.esipe.way2go.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        classes = Way2GoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
class SimulationServiceImplTest {
    private final UserService userService;

    private final SimulationService simulationService;

    private UserEntity user;

    private WebSecurityConfiguration webSecurityConfiguration;

    @Autowired
    public SimulationServiceImplTest(UserService userService, SimulationService simulationService, WebSecurityConfiguration webSecurityConfiguration)  {
        this.userService = userService;
        this.simulationService = simulationService;
        this.webSecurityConfiguration = webSecurityConfiguration;
    }

    @BeforeEach
    public void saveUser() {
        user = userService.saveUser(new UserEntity("test", webSecurityConfiguration.passwordEncoder().encode("0000") , "test@email.com", "admin"));
    }
    @Test
    void saveSimulation() {
        var simulation = new SimulationEntity("simulationTest", user, "Description de la simulation de test", "default");
        var simulationSave = simulationService.createSimulation(simulation);
        assertEquals(simulation, simulationSave);
    }
}