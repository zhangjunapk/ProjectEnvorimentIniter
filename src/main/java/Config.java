import annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhangJun on 2018/7/6.
 */
@WebDirName("webapp")
@Service(dir = "demo-service", packageName = "org.zj.service")
@Controller(dir = "demo-web", packageName = "org.zj.controller")
@Dao(dir = "demo-dao", packageName = "org.zj.dao")
@Bean(dir = "demo-bean", packageName = "org.zj.bean")
@ParentProject(version = "1.0-SNAPSHOT", name = "ProjectEvorimentIniter", artifactId = "org.zj")
@DataSource(username = "root", password = "", url = "jdbc:mysql://localhost:3306/bilibili", driver = "com.mysql.jdbc.Driver")
public class Config {
    public static void main(String[] args) throws IOException {

        Map<String, List<MenuMapping>> menuMap = new HashMap<>();
        List<MenuMapping> menuMappings = new ArrayList<>();
        menuMappings.add(new MenuMapping("collection", "收藏管理"));
        menuMappings.add(new MenuMapping("user", "用户管理"));
        menuMappings.add(new MenuMapping("category", "分类管理"));
        menuMappings.add(new MenuMapping("give_coin_history", "给币管理"));
        menuMappings.add(new MenuMapping("play_history", "播放历史管理"));
        menuMappings.add(new MenuMapping("student", "学生管理"));
        menuMappings.add(new MenuMapping("tag_set", "标签管理"));
        menuMappings.add(new MenuMapping("user", "用户管理"));

        menuMap.put("用户中心", menuMappings);


        Map<String, Creater.ClassBean> fieldMap = new HashMap<>();
        List<Creater.ClassBean> cList = new ArrayList<>();
        List<Creater.FieldBean> fieldBeans = new ArrayList<>();
        fieldBeans.add(new Creater.FieldBean(false, null, "id", "ID"));
        fieldBeans.add(new Creater.FieldBean(false, null, "name", "分类名"));
        fieldBeans.add(new Creater.FieldBean(false, null, "parentId", "父ID"));
        Creater.ClassBean classBean = new Creater.ClassBean();
        classBean.setAlias("分类管理");
        classBean.setFieldList(fieldBeans);
        cList.add(classBean);
        fieldMap.put("category", classBean);

        new ProjectIniter(Config.class).menuMap(menuMap).classBeanMap(fieldMap).init();
    }
}
