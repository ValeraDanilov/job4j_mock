package ru.checkdev.notification.domain;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.is;

class PersonDTOTest {

    private PersonDTO person;

    @BeforeEach
    public void setUp() {
        List<RoleDTO> roles = new ArrayList<>();
        roles.add(new RoleDTO(1));
        Calendar created = new Calendar.Builder()
                .setDate(2023, 10, 23)
                .setTimeOfDay(20, 20, 20)
                .build();
        person = new PersonDTO("username", "email", "password", true, roles, created, 0L, false);
    }

    @Test
    void testGetUsername() {
        MatcherAssert.assertThat("username", is(person.getUsername()));
    }

    @Test
    void testGetEmail() {
        MatcherAssert.assertThat("email", is(person.getEmail()));
    }

    @Test
    void testGetPassword() {
        MatcherAssert.assertThat("password", is(person.getPassword()));
    }

    @Test
    void testGetPrivacy() {
        MatcherAssert.assertThat(true, is(person.isPrivacy()));
    }

    @Test
    void testGetRoles() {
        List<RoleDTO> roles = new ArrayList<>();
        roles.add(new RoleDTO(1));
        MatcherAssert.assertThat(roles, is(person.getRoles()));
    }

    @Test
    void testGetCreated() {
        Calendar created = new Calendar.Builder()
                .setDate(2023, 10, 23)
                .setTimeOfDay(20, 20, 20)
                .build();
        MatcherAssert.assertThat(created, is(person.getCreated()));
    }

    @Test
    void testGetTelegramChat() {
        MatcherAssert.assertThat(0L, is(person.getTgChatId()));
    }

    @Test
    void testGetBind() {
        MatcherAssert.assertThat(false, is(person.isBind()));
    }
}
