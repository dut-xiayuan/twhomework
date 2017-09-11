package me.cwuyi.totw;

import org.junit.Test;

/**
 * Created by XIA on 2017/9/11.
 */
public class ClientTest {
    @Test
    public void case1 () {
        OrderBook orderBook = new OrderBook();
        String[] inputStr = {"adafsdgsfsadasfsdfsdfsd52654321",
                "U001 2016-06-02 22:00~22:00 A",
                "U002 2017-08-01 19:00~22:00 A",
                "U003 2017-08-02 13:00~17:00 B",
                "U004 2017-08-03 15:00~16:00 C",
                "U005 2017-08-05 09:00~11:00 D",
                ""
        };

        for (String orderStr : inputStr) {
            Client.handleOrder(orderStr);
        }
    }

    @Test
    public void case2 () {
        OrderBook orderBook = new OrderBook();
        String[] inputStr = {"U002 2017-08-01 19:00~22:00 A",
                "U003 2017-08-01 18:00~20:00 A",
                "U002 2017-08-01 19:00~22:00 A C",
                "U002 2017-08-01 19:00~22:00 A C",
                "U003 2017-08-01 18:00~20:00 A",
                "U003 2017-08-02 13:00~17:00 B",
                ""
        };

        for (String orderStr : inputStr) {
            Client.handleOrder(orderStr);
        }
    }

}
