package me.cwuyi.totw;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by XIA on 2017/9/9.
 */
public class Client {
    static OrderBook orderBook = new OrderBook();
    public static void main(String[] args) throws Exception {

        BufferedReader receiver = new BufferedReader(new InputStreamReader(System.in));
        String orderStr = "";
        while ((orderStr = receiver.readLine()) != null) {
            handleOrder(orderStr);
        }
    }

    public static void handleOrder(String orderStr) {
        if (orderStr.length() == 0 || orderStr.equals("")) {
            orderBook.printIncome();
        } else {
            Order order = Order.parse(orderStr);
            if (!order.isComplete()) {
                System.out.println("Error: the booking is invalid!");
                return;
            }
            if (!order.isCancel()) {
                if (orderBook.addOrder(order)) {
                    System.out.println("Success: the booking is accepted!");
                } else {
                    System.out.println("Error: the booking conflicts with existing booking!");
                }
            } else {
                if (orderBook.cancelOrder(order)) {
                    System.out.println("Success: the booking is accepted!");
                } else {
                    System.out.println("Error: the booking being cancelled does not exist!");
                }
            }
        }
    }
}
