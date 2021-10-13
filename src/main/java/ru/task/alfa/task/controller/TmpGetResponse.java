
package ru.task.alfa.task.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TmpGetResponse {
    private String url;
    private String width;
    private String height;
}
