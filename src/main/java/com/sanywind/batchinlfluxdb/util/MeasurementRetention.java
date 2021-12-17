package com.sanywind.batchinlfluxdb.util;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
/**
 * @author chenguizhi
 * @create 2021--08-30 10:28
 * description:获取风机类型的点,测试@value取不到
 */
@Configuration
@Data
@ConfigurationProperties("measurement-retention")
public class MeasurementRetention {
    private Map<String,String> measurements;
}
