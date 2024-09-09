package cn.edu.xmu.oomall.freight.dao.bo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class LogisticsTest {

    //
    @Test
    public void isMatchTest1(){
        Logistics logistics = new Logistics();
        logistics.setSnPattern("^JT[0-9]{13}$");
        assertThat(logistics.isMatch("JT1728392817277")).isEqualTo(true);
    }

    //
    @Test
    public void isMatchTest2(){
        Logistics logistics = new Logistics();
        logistics.setSnPattern("^ZTO[0-9]{12}$");
        assertThat(logistics.isMatch("ZTO983748293847")).isEqualTo(true);
    }

    @Test
    public void isMatchTest3(){
        Logistics logistics = new Logistics();
        logistics.setSnPattern("^SF[A-Za-z0-9-]{4,35}$");
        assertThat(logistics.isMatch("SF1234")).isEqualTo(true);
    }



}
