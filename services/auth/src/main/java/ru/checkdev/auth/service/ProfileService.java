package ru.checkdev.auth.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.matcher.ElementMatcher;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.dto.ProfileDTO;
import ru.checkdev.auth.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

/**
 * CheckDev пробное собеседование
 * Класс получения ProfileDTO
 *
 * @author Dmitry Stepanov
 * @version 22.09.2023'T'23:41
 */

@Service
@AllArgsConstructor
@Slf4j
public class ProfileService {
    private final PersonRepository personRepository;
    private PasswordEncoder passwordEncoder;

    /**
     * Получить ProfileDTO по ID
     *
     * @param id int
     * @return ProfileDTO
     */
    public Optional<ProfileDTO> findProfileByID(int id) {
        return Optional.ofNullable(personRepository.findProfileById(id));
    }

    /**
     * Получить список всех PersonDTO
     *
     * @return List<PersonDTO>
     */
    public List<ProfileDTO> findProfilesOrderByCreatedDesc() {
        return personRepository.findProfileOrderByCreatedDesc();
    }

    /**
     * Получить список всех PersonDTO по ID Telegram чата
     *
     * @param chatId ID Telegram чата
     * @return List<PersonDTO>
     */
    public List<ProfileDTO> findProfilesByChatId(Long chatId) {
        return this.personRepository.getProfileByChatId(chatId);
    }

    /**
     * Получить ProfileDTO по email
     *
     * @param profile Profile
     * @return ProfileDTO
     */
    private Optional<ProfileDTO> findProfileByLoginAndPassword(Profile profile) {
        ProfileDTO findProfile = this.personRepository.findProfileByLoginAndPassword(profile.getEmail());
        if (findProfile == null || !this.passwordEncoder.matches(profile.getPassword(), findProfile.getPassword())) {
            throw new IllegalArgumentException("Incorrect password for user: " + profile.getEmail());
        }
        return Optional.of(findProfile);
    }


    /**
     * Привязать Profile к ресурсу Mock
     *
     * @param profile Profile
     * @return ProfileDTO
     */
    public ProfileDTO update(Profile profile) {
        Optional<ProfileDTO> findProfile = findProfileByLoginAndPassword(profile);
        findProfile.ifPresent(profileDTO -> this.personRepository.updateBind(profileDTO.getId(), !profile.isBind()));
        findProfile.get().setBind(!profile.isBind());
        return findProfile.get();
    }
}
