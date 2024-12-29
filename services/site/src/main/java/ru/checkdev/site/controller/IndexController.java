package ru.checkdev.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.checkdev.site.dto.InterviewDTO;
import ru.checkdev.site.dto.ProfileDTO;
import ru.checkdev.site.service.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {
    private final CategoriesService categoriesService;
    private final InterviewsService interviewsService;
    private final AuthService authService;
    private final NotificationService notifications;
    private final ProfilesService profilesService;
    private final TopicsService topicsService;
    private final InterviewService interviewService;


    @GetMapping({"/", "index"})
    public String getIndexPage(Model model, HttpServletRequest req) throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/"
        );
        try {
            model.addAttribute("categories", categoriesService.getMostPopular());
            var token = RequestResponseTools.getToken(req);
            if (token != null) {
                var userInfo = authService.userInfo(token);
                model.addAttribute("userInfo", userInfo);
                model.addAttribute("userDTO", notifications.findCategoriesByUserId(userInfo.getId()));
                RequestResponseTools.addAttrCanManage(model, userInfo);
            }
        } catch (Exception e) {
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }

        List<InterviewDTO> interviewDTOList = interviewsService.getByType(1);
        Set<ProfileDTO> userList = interviewDTOList.stream()
                .map(x -> profilesService.getProfileById(x.getSubmitterId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        List<Integer> countNewInterview = interviewService.countNewInterviews(this.categoriesService, topicsService, interviewsService);

        model.addAttribute("count_new_interviews", countNewInterview);
        model.addAttribute("new_interviews", interviewDTOList);
        model.addAttribute("users", userList);
        return "index";
    }
}
