package ru.checkdev.auth.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.dto.ProfileDTO;
import ru.checkdev.auth.service.ProfileService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;


/**
 * CheckDev пробное собеседование
 * ProfileControllerTest тестирование RestController для отправки модели ProfileDTO.
 *
 * @author Dmitry Stepanov
 * @version 01:45
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {

    @MockBean
    private ProfileService profileService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileController profileController;

    private final ProfileDTO profileDTO1 = new ProfileDTO(
            1, "name1", "experience1", "delfyet@gmail.com", 123L, true);
    private final ProfileDTO profileDTO2 = new ProfileDTO(
            2, "name2", "experience2", "dlfit@gmail.com", 123L, false);


    @Before
    public void initController() {
        this.profileController = new ProfileController(profileService);
    }

    @Test
    @WithMockUser
    public void whenGetProfileByIdThenReturnStatusOK() throws Exception {
        when(profileService.findProfileByID(profileDTO1.getId())).thenReturn(Optional.of(profileDTO1));
        mockMvc.perform(get("/profiles/{id}", profileDTO1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileDTO1.getId()))
                .andExpect(jsonPath("$.username").value(profileDTO1.getUsername()))
                .andExpect(jsonPath("$.experience").value(profileDTO1.getExperience()))
                .andExpect(jsonPath("$.photoId").value(profileDTO1.getPhotoId()));
    }

    @SuppressWarnings("checkstyle:OperatorWrap")
    @Test
    @WithMockUser
    public void whenGetProfileByIdProfileNotFoundThenReturnStatusNotFound() throws Exception {
        when(profileService.findProfileByID(profileDTO1.getId())).thenReturn(Optional.empty());
        mockMvc.perform(get("/profiles/{id}/", anyInt()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void whenGetAllProfilesOrderByCreateDescThenReturnStatusOkAndBody() throws Exception {
        var profiles = List.of(profileDTO1, profileDTO2);
        when(profileService.findProfilesOrderByCreatedDesc()).thenReturn(profiles);
        mockMvc.perform(get("/profiles/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(profiles.size()));
    }

    @Test
    @WithMockUser
    public void whenGetAllProfilesOrderByCreateDescListEmptyThenReturnStatusNoContent() throws Exception {
        when(profileService.findProfilesOrderByCreatedDesc()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/profiles/"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void whenGetProfileByChatIdProfilesEmptyThenReturnStatusNoContent() throws Exception {
        when(profileService.findProfilesByChatId(profileDTO1.getTgChatId())).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/profiles/chat/{id}", profileDTO1.getTgChatId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    public void whenGetProfileByChatIdThenReturnStatusOK() throws Exception {
        List<ProfileDTO> profiles = List.of(profileDTO1, profileDTO2);
        when(profileService.findProfilesByChatId(profileDTO1.getTgChatId())).thenReturn(profiles);
        mockMvc.perform(get("/profiles/chat/{id}", profileDTO1.getTgChatId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(profiles.size()))
                .andExpect(jsonPath("$[0].id").value(profileDTO1.getId()))
                .andExpect(jsonPath("$[0].username").value(profileDTO1.getUsername()))
                .andExpect(jsonPath("$[1].id").value(profileDTO2.getId()))
                .andExpect(jsonPath("$[1].username").value(profileDTO2.getUsername()));
    }


    @Test
    @WithMockUser
    public void whenBindAndUnbindProfileThenReturnStatusCreated() throws Exception {
        Profile profileToUpdate = new Profile("name1", "experience1", "delfyet@gmail.com");
        profileToUpdate.setTgChatId(1L);

        when(profileService.update(profileToUpdate)).thenReturn(profileDTO1);

        mockMvc.perform(post("/profiles/bindAndUnbind")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(profileToUpdate))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(profileDTO1.getId()))
                .andExpect(jsonPath("$.username").value(profileDTO1.getUsername()))
                .andExpect(jsonPath("$.experience").value(profileDTO1.getExperience()))
                .andExpect(jsonPath("$.photoId").value(profileDTO1.getPhotoId()));
    }


    @Test
    @WithMockUser
    public void whenBindAndUnbindProfileThenReturnStatusBadRequest() throws Exception {
        mockMvc.perform(post("/profiles/bindAndUnbind")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(profileDTO1.getTgChatId()))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
