package ru.checkdev.auth.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.dto.ProfileDTO;
import ru.checkdev.auth.service.ProfileService;

import java.util.List;

/**
 * CheckDev пробное собеседование
 * ProfileController контроллер отправки и приема DTO модели ProfileDTO
 *
 * @author Dmitry Stepanov
 * @version 22.09.2023T23:49
 */
@RestController
@RequestMapping("/profiles")
@Slf4j
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Обрабатывает get запрос на получение профиля пользователя по запрошенному ID.
     *
     * @param id ID ProfileDTO
     * @return ResponseEntity
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileDTO> getProfileById(@PathVariable int id) {
        var profileDTO = profileService.findProfileByID(id);
        return new ResponseEntity<>(
                profileDTO.orElse(new ProfileDTO()),
                profileDTO.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    /**
     * Отправляет все профили пользователей
     *
     * @return ResponseEntity
     */
    @GetMapping("/")
    public ResponseEntity<List<ProfileDTO>> getAllProfilesOrderByCreateDesc() {
        var profiles = profileService.findProfilesOrderByCreatedDesc();
        return new ResponseEntity<>(
                profiles,
                profiles.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    /**
     * Обрабатывает get запрос на получение списка профилей пользователя по запрошенному ID чата.
     *
     * @param id ID Telegram chat
     * @return ResponseEntity
     */
    @GetMapping("chat/{id}")
    public ResponseEntity<List<ProfileDTO>> getProfileByChatId(@PathVariable Long id) {
        var profiles = profileService.findProfilesByChatId(id);
        return new ResponseEntity<>(
                profiles,
                profiles.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }


    /**
     * Обрабатывает post запрос на привязку или отвязку профиля от ресурса Mock.
     *
     * @param profile
     * @return ResponseEntity
     */
    @PostMapping("/bindAndUnbind")
    public ResponseEntity<ProfileDTO> getProfileByLoginAndPasswordAndUpdateBind(@RequestBody Profile profile) {
        ProfileDTO findProfile = this.profileService.update(profile);
        var uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(profile.getId())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(uri)
                .body(findProfile);
    }
}
