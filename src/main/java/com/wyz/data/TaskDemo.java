package com.wyz.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.OffsetDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Table(name = "taskDemo")
public class TaskDemo {
    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "INT")
    private Integer id;

    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(64)")
    private String title;

    @Column(name = "content", columnDefinition = "VARCHAR(128)")
    private String content;

    // TODO: 2022/9/2 json
    // 设定的任务时间
    @Column(name = "setTime", nullable = false, columnDefinition = "DATETIME")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private OffsetDateTime setTime;
    // 设定的任务结束时间，可留空
    @Column(name = "endTime", columnDefinition = "DATETIME")
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private OffsetDateTime endTime;
    // 0:无, 1:秒, 2:分, 3:时, 4:天, 5:周, 6:月, 7:年
    // 重复类型：0 --> 无重复, 4 --> 每天, 5 --> 每周, 6 --> 每月, 7 -->每年
    @Column(name = "repeatType", columnDefinition = "INT")
    private Integer repeatType;

    // 重复周期，默认1，即每一天/每一周...
    @Column(name = "repeatValue", columnDefinition = "INT")
    private Integer repeatValue;

    // 提前类型：0 --> 不提前, 2 --> 提前x分钟, 3 --> 提前x小时, 4 --> 提前x天
    @Column(name = "InAdvanceType", columnDefinition = "INT")
    private Integer InAdvanceType;

    // 提前时间
    @Column(name = "InAdvanceValue", columnDefinition = "INT")
    private Integer InAdvanceValue;

    // cron表达式，若有提前，则有两个，用","隔开
    @Column(name = "cron", nullable = false, columnDefinition = "VARCHAR(32)")
    private String cron;

    // 任务有效标志
    @Column(name = "isValid", columnDefinition = "BOOLEAN")
    private Boolean isValid;
}
