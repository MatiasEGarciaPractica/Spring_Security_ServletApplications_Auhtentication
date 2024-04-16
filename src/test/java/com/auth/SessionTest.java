package com.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class SessionTest {

    @Autowired
    private MockMvc mvc;

//    @Test//this test works if you quit this line from session management : .maxSessionsPreventsLogin(true)
//    void loginOnSecondLoginThenFirstSessionTerminated() throws Exception{
//        //perfom a login and we expect to be authenticated.
//    MvcResult mvcResult = this.mvc.perform(formLogin())
//            .andExpect(authenticated()).andReturn();
//
//    //retrieves the session from the first login
//    MockHttpSession firstLoginSession = (MockHttpSession) mvcResult.getRequest().getSession();
//
//    //perfom a request and expect to be authenticated, still with first session.
//    this.mvc.perform(get("/").session(firstLoginSession)).andExpect(authenticated());
//
//    //it performs another form login and expects the user to be authenticated.
//    this.mvc.perform(formLogin()).andExpect(authenticated());
//
//    //it performs a request with the session from the first login and expects the user to be unauthenticated,
//        // indicating that the first session has been terminated.
//        this.mvc.perform(get("/").session(firstLoginSession)).andExpect(unauthenticated());
//    }

    @Test
    void loginOnSecondLoginThenPreventLogin() throws Exception{
        MvcResult mvcResult = this.mvc.perform(formLogin()).andExpect(authenticated()).andReturn();

        MockHttpSession firstLoginSession = (MockHttpSession) mvcResult.getRequest().getSession();

        this.mvc.perform(get("/").session(firstLoginSession)).andExpect(authenticated());

        // second login is prevented
        this.mvc.perform(formLogin()).andExpect(unauthenticated());

        // first session is still valid
        this.mvc.perform(get("/").session(firstLoginSession)).andExpect(authenticated());
    }
}
