package me.cwuyi.totw;

import org.joda.time.DateTime;

import java.util.*;

/**
 * Created by XIA on 2017/9/9.
 */
public class OrderBook {

    private static Map<String, PlayGround> playGroundMap;
    private static final String ORDER = "order";
    private static final String CANCEL = "cancel";

    static {
        playGroundMap = new HashMap<String, PlayGround>();
        PlayGround pgA = new PlayGround("A");
        PlayGround pgB = new PlayGround("B");
        PlayGround pgC = new PlayGround("C");
        PlayGround pgD = new PlayGround("D");
        playGroundMap.put("A", pgA);
        playGroundMap.put("B", pgB);
        playGroundMap.put("C", pgC);
        playGroundMap.put("D", pgD);
    }

    public OrderBook() {}

    public OrderBook(Map<String, PlayGround> playGroundMap) {
        this.playGroundMap = playGroundMap;
    }

    /**
     *
     * @param order 待新增的预定
     * @return 新增预定是否成功
     */
    public boolean addOrder(Order order) {
        PlayGround playGround = playGroundMap.get(order.getPlayGround());
        Map<String, Map<String, String>> occupy = playGround.getOccupy();
        Map<String, Map<String, List<String>>> operateLog = playGround.getOperateLog();

        String date = order.getOrderDate().toString("yyyy-MM-dd");
        String time = order.getOrderTime();

        if (null == occupy.get(date)) {
            occupy.put(date, new HashMap<String, String>());
        }
        Map<String, String> dayOccupy = occupy.get(date);

        //重复预定
        if (dayOccupy.get(time) != null && dayOccupy.get(time).equals(order.getUserId())) {
            if (dayOccupy.get(time).equals(order.getUserId())) {
                return true;
            } else {
                return false;
            }
        }

        //是否有时间段交叉
        if (isOverlap(dayOccupy.keySet(), time)) {
            return false;
        }
        dayOccupy.put(time, order.getUserId());

        if (null == operateLog.get(date)) {
            operateLog.put(date, new HashMap<String, List<String>>());
        }
        Map<String, List<String>> dayOperate = operateLog.get(date);

        if (null == dayOperate.get(time)) {
            dayOperate.put(time, new ArrayList<String>());
        }

        List<String> log = dayOperate.get(time);
        log.add(OrderBook.ORDER);

        return true;
    }

    /**
     *
     * @param order 待取消的预定
     * @return 取消成功
     */
    public boolean cancelOrder(Order order) {
        PlayGround playGround = playGroundMap.get(order.getPlayGround());
        Map<String, Map<String, String>> occupy = playGround.getOccupy();
        Map<String, Map<String, List<String>>> operateLog = playGround.getOperateLog();

        String date = order.getOrderDate().toString("yyyy-MM-dd");
        String time = order.getOrderTime();

        Map<String, String> dayOccupy = occupy.get(date);
        if (null == dayOccupy) {
            return false;
        }

        if (!dayOccupy.containsKey(order.getOrderTime()) || !dayOccupy.get(order.getOrderTime()).equals(order.getUserId())) {
            return false;
        }

        dayOccupy.remove(order.getOrderTime());
        Map<String, List<String>> dayOperate = operateLog.get(date);
        dayOperate.get(order.getOrderTime()).add(OrderBook.CANCEL);

        return true;
    }

    /**
     * 控制台打印收入汇总
     */
    public void printIncome() {
        System.out.println("收入汇总");
        System.out.println("---");

        int total = 0;

        List<Map.Entry<String, PlayGround>> list = new ArrayList<Map.Entry<String, PlayGround>>(playGroundMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, PlayGround>>() {
            public int compare(Map.Entry<String, PlayGround> o1, Map.Entry<String, PlayGround> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        for (Map.Entry<String, PlayGround> pgEntry : list) {
            System.out.println("场地:" + pgEntry.getKey());
            TreeSet<String> set = new TreeSet<String>();
            int count = 0;

            PlayGround playGround = pgEntry.getValue();
            Map<String, Map<String, String>> occupy = playGround.getOccupy();
            Map<String, Map<String, List<String>>> operateLog = playGround.getOperateLog();

            for (Map.Entry<String, Map<String, String>> dayOccupyEntry : occupy.entrySet()) {
                String day = dayOccupyEntry.getKey();
                int weekDay = DateTime.parse(day).getDayOfWeek();
                for (Map.Entry<String, String> dayOccupyDetailEntry : dayOccupyEntry.getValue().entrySet()) {
                    String time = dayOccupyDetailEntry.getKey();
                    int money = charge(weekDay, time);
                    set.add(day + " " + time + " " + money + "元");
                    count += money;
                }
            }

            for (Map.Entry<String, Map<String, List<String>>> operateLogEntry : operateLog.entrySet()) {
                String day = operateLogEntry.getKey();
                int weekDay = DateTime.parse(day).getDayOfWeek();

                for (Map.Entry<String, List<String>> logDetailEntry : operateLogEntry.getValue().entrySet()) {
                    String time = logDetailEntry.getKey();
                    int money = 0;
                    for (String type : logDetailEntry.getValue()) {
                        if (type.equals(OrderBook.CANCEL)) {
                            if (weekDay>=1 && weekDay <= 5) {
                                money += charge(weekDay, time)*0.5;
                            }
                        }
                    }
                    if (money != 0) {
                        set.add(day + " " + time + " 违约金" + money + "元");
                    }
                    count += money;
                }
            }

            for (String detail : set) {
                System.out.println(detail);
            }
            System.out.println("小计:" + count + "元");
            System.out.println();
            total += count;
        }
        System.out.println("---");
        System.out.println("总计： " + total + "元");
    }


    public void addPlayGround(String pg) {

    }

    /**
     *
     * @param occupy 某天已经被预定出去的时间段
     * @param attemp 尝试预定的时间段
     * @return true if 没有时间交叉，false if 存在时间交叉
     */
    private boolean isOverlap(Set<String> occupy, String attemp) {
        Set<Integer> orderInts = new HashSet<Integer>();

        for (String orderStr : occupy) {
            int[] fromTo = getOrderIntTime(orderStr);
            for (int i = fromTo[0]; i < fromTo[1]; ++i) {
                orderInts.add(i);
            }
        }
        int[] fromTo = getOrderIntTime(attemp);
        for (int i = fromTo[0]; i < fromTo[1]; ++i) {
            if (orderInts.contains(i)) {
                return true;
            }
        }

        return false;
    }

    private int[] getOrderIntTime(String orderTime) {
        int[] fromTo = new int[2];
        fromTo[0] = Integer.parseInt(orderTime.split("~")[0].split(":")[0]);
        fromTo[1] = Integer.parseInt(orderTime.split("~")[1].split(":")[0]);
        return fromTo;
    }

    /**
     *
     * @param dayOfWeek 预定是一周的第几天
     * @param time 预定的时间段
     * @return
     */
    private int charge(int dayOfWeek, String time) {
        int res = 0;
        if (dayOfWeek >=1 && dayOfWeek <=5) {
            int[] fromTo = getOrderIntTime(time);
            for (int i = fromTo[0]; i < fromTo[1]; ++i) {
                if (i >= 9 && i < 12) {
                    res += 30;
                } else if (i >= 12 && i < 18) {
                    res += 50;
                } else if (i >= 18 && i < 20) {
                    res += 80;
                } else if (i >= 20 && i < 22) {
                    res += 60;
                }
            }
        } else if (dayOfWeek >=6 && dayOfWeek <=7) {
            int[] fromTo = getOrderIntTime(time);
            for (int i = fromTo[0]; i < fromTo[1]; ++i) {
                if (i >= 9 && i < 12) {
                    res += 40;
                } else if (i >= 12 && i < 18) {
                    res += 50;
                } else if (i >= 18 && i < 22) {
                    res += 60;
                }
            }
        }

        return res;
    }
}
