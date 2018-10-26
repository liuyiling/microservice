package com.liuyiling.microservice.api.controller;

import com.liuyiling.microservice.api.MicroserviceApplication;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

import static java.lang.System.out;

/**
 * @author liuyiling
 * @date on 2018/10/24
 */
@SpringBootTest(classes = MicroserviceApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class TestSampleController {

    @Resource
    private SampleController sampleController;

    @Test
    public void testHome(){
        out.println(this.sampleController.home("liuyiling"));
    }
}
