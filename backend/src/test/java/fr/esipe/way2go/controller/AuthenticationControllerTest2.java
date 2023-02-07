package fr.esipe.way2go.controller;

import fr.esipe.way2go.Way2GoApplication;
import fr.esipe.way2go.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest (
        classes = Way2GoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
class AuthenticationControllerTest2 {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser
    public void checkAuthentication() throws Exception {
        assertEquals(Optional.empty(), userRepository.findByUsername("maks"));
    }

    @Test
    @WithMockUser
    public void checkAuthentication2() throws Exception {
        var response = mockMvc.perform(get("/api/auth/hello")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("Hello")));
    }
}