package me.cwuyi.totw;

import org.joda.time.DateTime;

/**
 * Created by XIA on 2017/9/9.
 */
public class Order {
    private String userId;
    private String playGround;
    private DateTime orderDate;
    private String orderTime;
    private boolean isCancel;
    private boolean complete;

    public Order() {
        this.complete = false;
    }

    public Order(String userId, String playGround, DateTime orderDate, String orderTime, boolean isCancel) {
        this.userId = userId;
        this.playGround = playGround;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.isCancel = isCancel;
        this.complete = true;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlayGround() {
        return playGround;
    }

    public void setPlayGround(String playGround) {
        this.playGround = playGround;
    }

    public DateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(DateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public static Order parse(String orderString) {
        Order order = new Order();
        String[] items = orderString.split(" ");

        if (items.length == 4 || items.length == 5) {
            order.setUserId(items[0]);
            try {
                order.setOrderDate(DateTime.parse(items[1]));
            } catch (Exception e) {
                return order;
            }
            String[] time = items[2].split("~");
            if (time.length != 2) {
                return order;
            }
            String form = time[0];
            String to = time[1];
            if (form.split(":").length != 2 || to.split(":").length != 2) {
                return order;
            }
            if (!form.split(":")[1].equals("00") || !to.split(":")[1].equals("00")) {
                return order;
            }
            if (Integer.parseInt(form.split(":")[0]) >= Integer.parseInt(to.split(":")[0])) {
                return order;
            }
            if (Integer.parseInt(form.split(":")[0]) > 22 || Integer.parseInt(form.split(":")[0]) < 9) {
                return order;
            }
            if (Integer.parseInt(to.split(":")[0]) > 22 || Integer.parseInt(to.split(":")[0]) < 9) {
                return order;
            }

            order.setOrderTime(items[2]);

            String pg = items[3];
            if (!pg.equals("A") && !pg.equals("B") && !pg.equals("C") && !pg.equals("D")) {
                return order;
            }
            order.setPlayGround(pg);

            if (items.length == 5) {
                String type = items[4];
                if (!type.equals("C")) {
                    return order;
                }
                order.setCancel(true);
            }
            order.setComplete(true);
        }

        return order;
    }
}
