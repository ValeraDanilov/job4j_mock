package ru.checkdev.auth.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.dto.ProfileDTO;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * CheckDev пробное собеседование
 * Тест на класс PersonRepository
 *
 * @author Dmitry Stepanov
 * @version 22.09.2023'T'21:14
 */
@RunWith(SpringRunner.class)
@DataJpaTest()
public class PersonRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PersonRepository personRepository;

    @Before
    public void clearTable() {
        entityManager.createQuery("delete from profile ").executeUpdate();
    }

    @Test
    public void injectedComponentAreNotNull() {
        assertNotNull(entityManager);
        assertNotNull(personRepository);
    }

    @Test
    public void whenFindProfileByIdThenReturnNull() {
        ProfileDTO profileDTO = personRepository.findProfileById(-1);
        assertNull(profileDTO);
    }

    @Test
    public void whenFindProfileOrderByCreatedDescThenReturnEmptyList() {
        var listProfileDTO = personRepository.findProfileOrderByCreatedDesc();
        assertThat(listProfileDTO, is(Collections.emptyList()));
    }

    @Test
    public void whenFindProfileByLoginAndPasswordThenReturnProfileDTO() {
        Profile profile = new Profile();
        profile.setUsername("testUser");
        profile.setEmail("test@example.com");
        profile.setPassword("password123");
        profile.setBind(true);
        profile.setCreated(Calendar.getInstance());
        profile.setUpdated(Calendar.getInstance());

        this.personRepository.save(profile);

        ProfileDTO result = this.personRepository.findProfileByLoginAndPassword("test@example.com");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("password123", result.getPassword());
        assertTrue(result.isBind());
    }

    @Test
    public void whenFindProfileByLoginAndPasswordThenReturnNull() {
        ProfileDTO result = this.personRepository.findProfileByLoginAndPassword("nonexistent@example.com");
        assertNull(result);
    }

    @Test
    public void whenGetProfileByChatIdThenReturnProfileDTO() {
        Profile profile = new Profile();
        profile.setUsername("testUser");
        profile.setEmail("test@example.com");
        profile.setPassword("password123");
        profile.setTgChatId(12345L);
        profile.setBind(true);
        profile.setCreated(Calendar.getInstance());
        profile.setUpdated(Calendar.getInstance());

        this.personRepository.save(profile);

        List<ProfileDTO> result = this.personRepository.getProfileByChatId(12345L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getUsername());
        assertEquals("test@example.com", result.get(0).getEmail());
    }

    @Test
    public void whenGetProfileByChatIdThenReturnEmptyList() {
        List<ProfileDTO> result = this.personRepository.getProfileByChatId(99999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetMultipleProfilesByChatIdThenReturnListOfProfileDTOs() {
        Profile profile1 = new Profile();
        profile1.setUsername("testUser1");
        profile1.setEmail("user1@example.com");
        profile1.setPassword("password123");
        profile1.setTgChatId(54321L);
        profile1.setBind(true);
        profile1.setCreated(Calendar.getInstance());
        profile1.setUpdated(Calendar.getInstance());
        this.personRepository.save(profile1);

        Profile profile2 = new Profile();
        profile2.setUsername("testUser2");
        profile2.setEmail("user2@example.com");
        profile2.setPassword("password456");
        profile2.setTgChatId(54321L);
        profile2.setBind(true);
        profile2.setCreated(Calendar.getInstance());
        profile2.setUpdated(Calendar.getInstance());
        this.personRepository.save(profile2);

        List<ProfileDTO> result = this.personRepository.getProfileByChatId(54321L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testUser1", result.get(0).getUsername());
        assertEquals("user1@example.com", result.get(0).getEmail());
        assertEquals("testUser2", result.get(1).getUsername());
        assertEquals("user2@example.com", result.get(1).getEmail());
    }


    @Test
    public void whenGetProfileByChatIdNullThenReturnEmptyList() {
        List<ProfileDTO> result = this.personRepository.getProfileByChatId(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
