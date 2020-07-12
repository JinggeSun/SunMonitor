package com.sun.manager.entity.mac;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.manager.entity.BaseEntity;
import lombok.*;

/**
 * @author zcm
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Getter
@Setter
@AllArgsConstructor
@TableName("mac_info")
public class MacInfo extends BaseEntity {

    private String ipAddr;
    private boolean emailAlarm;
    private float cpuUpper;
    private float memUpper;

}
