package com.laojiahuo.ictproject.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 配置文件
 */
@Data
@Component
public class Appconfig {
    private Integer WsPort = 5051;
}
