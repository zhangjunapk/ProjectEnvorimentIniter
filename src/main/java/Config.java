import annotation.*;

import java.io.IOException;

/**
 * Created by ZhangJun on 2018/7/6.
 */
@Service(dir = "demo-service",packageName = "org.zj.service")
@Controller(dir = "demo-web",packageName = "org.zj.controller")
@Dao(dir = "demo-dao",packageName = "org.zj.dao")
@Bean(dir = "demo-bean",packageName = "org.zj.bean")
@ParentProject(version = "1.0-SNAPSHOT",name = "bilibili",artifactId = "com.czxy")
@DataSource(username = "root",password = "",url="jdbc:mysql://localhost:3306/bilibili",driver = "com.mysql.jdbc.Driver")
public class Config {
    public static void main(String[] args) throws IOException {
        new ProjectIniter(Config.class).init();
    }
}
