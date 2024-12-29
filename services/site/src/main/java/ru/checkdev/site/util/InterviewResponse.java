package ru.checkdev.site.util;

import lombok.Getter;
import lombok.Setter;
import ru.checkdev.site.dto.InterviewDTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class InterviewResponse {

    private List<InterviewDTO> content;

}

