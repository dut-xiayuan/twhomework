package me.cwuyi.totw;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by XIA on 2017/9/9.
 */
public class PlayGround {
    private String name;
    //Map<日期, Map<时间段, 用户>>
    private Map<String, Map<String, String>> occupy;
    //Map<日期, Map<时间段, 操作日志>>
    private Map<String, Map<String, List<String>>> operateLog;

    public PlayGround (String name) {
        this.name = name;
        this.occupy = new HashMap<String, Map<String, String>>();
        this.operateLog = new HashMap<String, Map<String, List<String>>>();
    }

    public Map<String, Map<String, List<String>>> getOperateLog() {
        return operateLog;
    }

    public void setOperateLog(Map<String, Map<String, List<String>>> operateLog) {
        this.operateLog = operateLog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Map<String, String>> getOccupy() {
        return occupy;
    }

    public void setOccupy(Map<String, Map<String, String>> occupy) {
        this.occupy = occupy;
    }
}
