package ru.job4j.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ru.job4j.site.domain.StatusInterview;
import ru.job4j.site.domain.StatusWisher;
import ru.job4j.site.dto.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class InterviewService {
    private static final String URL_MOCK = "http://localhost:9912/interview/";
    private final ProfilesService profilesService;

    public InterviewService(ProfilesService profilesService) {
        this.profilesService = profilesService;
    }

    public InterviewDTO create(String token, InterviewDTO interviewDTO) throws JsonProcessingException {
        interviewDTO.setStatus(StatusInterview.IS_NEW.getId());
        var mapper = new ObjectMapper();
        var out = new RestAuthCall(URL_MOCK).post(
                token,
                mapper.writeValueAsString(interviewDTO)
        );
        return mapper.readValue(out, InterviewDTO.class);
    }

    public InterviewDTO getById(String token, int id) throws JsonProcessingException {
        var text = new RestAuthCall(String.format("%s%d", URL_MOCK, id))
                .get(token);
        return new ObjectMapper().readValue(text, new TypeReference<>() {
        });
    }

    public void update(String token, InterviewDTO interviewDTO) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        new RestAuthCall(URL_MOCK).update(
                token,
                mapper.writeValueAsString(interviewDTO));
    }

    /**
     * Метод обновляет статус собеседования
     *
     * @param token     User security token
     * @param id        int ID Interview
     * @param newStatus int New status
     */
    public void updateStatus(String token, int id, int newStatus) {
        new RestAuthCall(String.format("%sstatus/?id=%d&newStatus=%d", URL_MOCK, id, newStatus))
                .put(token, "");
    }

    /**
     * Метод проверяет являться пользователь автором собеседования.
     *
     * @param userInfoDTO  UserInfoDto
     * @param interviewDTO InterviewDTO
     * @return boolean userId == submitterId
     */
    public boolean isAuthor(UserInfoDTO userInfoDTO, InterviewDTO interviewDTO) {
        return userInfoDTO.getId() == interviewDTO.getSubmitterId();
    }

    /**
     * Метод формирует детальную информацию по всем кандидатам в собеседовании
     *
     * @param wishers List<WisherDto>
     * @return List<WisherDetail>
     */
    public List<WisherDetailDTO> getAllWisherDetail(List<WisherDto> wishers) {
        List<WisherDetailDTO> wishersDetail = new ArrayList<>();
        var statusesWisher = StatusWisher.values();
        for (WisherDto wisherDto : wishers) {
            var person = profilesService.getProfileById(wisherDto.getUserId());
            if (person.isPresent()) {
                var wisherUser = new WisherDetailDTO(wisherDto.getId(),
                        wisherDto.getInterviewId(),
                        wisherDto.getUserId(),
                        person.get().getUsername(),
                        wisherDto.getContactBy(),
                        wisherDto.isApprove(),
                        wisherDto.getStatus(),
                        statusesWisher[wisherDto.getStatus()].getInfo());
                wishersDetail.add(wisherUser);
            }
        }
        return wishersDetail;
    }


    public List<Integer> countNewInterviews(List<ProfileDTO> profiles, List<CategoryDTO> categories, TopicsService topicsService, InterviewsService interviewsService) throws JsonProcessingException {
        List<Integer> countNewInterview = new ArrayList<>();

        for (CategoryDTO categoryDTO : categories) {
            var getAllTopics = topicsService.getByCategory(categoryDTO.getId());
            int count = 0;

            for (ProfileDTO profileDTO : profiles) {
                List<InterviewDTO> interviewsList = interviewsService.getByType(profileDTO.getId());

                count += (int) getAllTopics.stream()
                        .filter(topicDTO -> isNewTopic(interviewsList, topicDTO))
                        .count();
            }

            countNewInterview.add(count);
        }

        return countNewInterview;
    }

    private boolean isNewTopic(List<InterviewDTO> interviewsList, TopicDTO topicDTO) {
        return interviewsList.stream()
                .anyMatch(interviewDTO -> topicDTO.getName().equals(interviewDTO.getTitle()));
    }
}
