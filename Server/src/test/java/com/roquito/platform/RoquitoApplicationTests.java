package com.roquito.platform;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pubkit.PubKitApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PubKitApplication.class)
@WebAppConfiguration
public class RoquitoApplicationTests {
    
    @Test
    public void contextLoads() {
    }
    
}
