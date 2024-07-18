package vip.xiaonuo.modular.weChatPay.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @projectName: xiongan
 * @package: vip.xiaonuo.modular.weChatPay.entity
 * @className: weChatOrg
 * @author: mqd
 * @description: 1000
 * @date: 2024/5/22 17:51
 * @version: 1.0
 */
@Data
public class weChatOrg {
    @Excel(name = "截止日期", databaseFormat = "yyyy-MM-dd HH:mm:ss", format = "yyyy-MM-dd", width = 20)
    @JsonFormat(pattern = "yyyy-MM-dd")
    public Date end_time;
    public String money;
    public String reminder;
}
