package com.uav.node;

import com.uav.node.demos.model.Message;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class testMsg {

    @Test
    public void test()
    {
        Message m = new Message();
        byte[] bytes = new byte[3];
        bytes[0] = 1;
        bytes[1] = 2;
        bytes[2] = 3;
        m.setValue(bytes);

        byte[] mm = m.toByteArray();
        Message m_ = Message.fromByteArray(mm);
        System.out.println(m.toGood());
        System.out.println(m_.toGood());



    }

}
