import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-redis.xml")
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test01(){
        redisTemplate.boundHashOps("seckillGoods").delete(1l);
        System.out.println("搞定...");

    }
    @Test
    public void test02(){
        Object seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        System.out.println(seckillGoods);
        System.out.println("搞定...");

    }



}
