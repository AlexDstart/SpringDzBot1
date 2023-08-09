package ru.skypro.lessons.springboot.weblibrary;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.skypro.lessons.springboot.weblibrary.initializer.Postgres;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(initializers = {Postgres.Initializer.class})
public abstract class IntegrationTestBase {

    @BeforeAll
    public static void init() {
        Postgres.container.start();
    }
}
