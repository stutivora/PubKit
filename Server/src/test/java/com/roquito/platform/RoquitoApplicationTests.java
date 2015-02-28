package com.roquito.platform;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.roquito.RoquitoApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RoquitoApplication.class)
@WebAppConfiguration
public class RoquitoApplicationTests {
    
    @Test
    public void contextLoads() {
    }
    
}
