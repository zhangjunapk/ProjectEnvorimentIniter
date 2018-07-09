import annotation.*;
import com.alibaba.druid.pool.DruidDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Created by ZhangJun on 2018/7/6.
 * @author  张君 项目初始化
 */

public class ProjectIniter {
    Class c;

    String basePom="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "\n" +
            "    \n" +
            "\n" +
            "    \n" +
            "\n" +
            "\n" +
            "\n" +
            "    \n" +
            "\n" +
            "\n" +
            "\n" +
            "</project>";


    String mvcConfig;

    String batisConfig;

    String springConfig;

String webIniter;

    String projectName;
    String projectVersion;

    String artifactId;

    String beanPackageName;
    String beanDirName;
    String controllerPackageName;
    String controllerDirName;
    String daoPackageName;
    String daoDirName;
    String servicePackageName;
    String serviceDirName;
    DruidDataSource druidDataSource=new DruidDataSource();
    public ProjectIniter(Class c){
        this.c=c;
    }
    public void init() throws IOException {
        //扫描配置类
        inflateData();
        //为每一个层创建文件及文件夹
        createModules();
        //生成bean
        createClass();
        //为父项目添加模块
        addModule();
        //生成配置类
        createConfig();
    }

    private void createConfig() throws IOException {
        mvcConfig="package "+ artifactId +"."+projectName+".config;\r\n"+"import org.springframework.context.annotation.Bean;\n" +
                "import org.springframework.context.annotation.ComponentScan;\n" +
                "import org.springframework.context.annotation.Configuration;\n" +
                "import org.springframework.web.servlet.config.annotation.EnableWebMvc;\n" +
                "import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;\n" +
                "import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;\n" +
                "import org.springframework.web.servlet.view.InternalResourceViewResolver;\n" +
                "\n" +
                "@ComponentScan(basePackages=\""+ artifactId +"."+projectName+".controller\")\n" +
                "@EnableWebMvc\n" +
                "public class MvcConfig extends WebMvcConfigurerAdapter {\n" +
                "\n" +
                "\t/**\n" +
                "\t * 视图解析器\n" +
                "\t * @return\n" +
                "\t */\n" +
                "\t@Bean\n" +
                "\tpublic InternalResourceViewResolver internalResourceViewResolver(){\n" +
                "\t\tInternalResourceViewResolver viewResolver = new InternalResourceViewResolver();\n" +
                "\t\tviewResolver.setPrefix(\"/\");\n" +
                "\t\tviewResolver.setSuffix(\".jsp\");\n" +
                "\t\treturn viewResolver;\n" +
                "\t}\n" +
                "\n" +
                "\t@Override\n" +
                "\tpublic void addResourceHandlers(ResourceHandlerRegistry registry) {\n" +
                "\t\tregistry.addResourceHandler(\"/js/**\").addResourceLocations(\"/js/\");\n" +
                "\t\tregistry.addResourceHandler(\"/css/**\").addResourceLocations(\"/css/\");\n" +
                "        registry.addResourceHandler(\"/themes/**\").addResourceLocations(\"/themes/\");\n" +
                "\t}\n" +
                "}\n";

        //创建webmvc.java
        String pathByModuleName = getPathByModuleName(controllerDirName);
        String prefix=pathByModuleName+"src/main/java/org/zj/"+projectName;
        File file=new File(prefix+"/config");
        file.mkdirs();
        file=new File(prefix+"/config/MvcConfig.java");
        file.delete();
        file.createNewFile();
        append(file,mvcConfig);


        batisConfig="package "+ artifactId +"."+projectName+".config;\r\nimport java.util.Properties;\n" +
                "\n" +
                "import javax.sql.DataSource;\n" +
                "\n" +
                "import org.apache.ibatis.plugin.Interceptor;\n" +
                "import org.apache.ibatis.session.Configuration;\n" +
                "import org.apache.ibatis.session.SqlSessionFactory;\n" +
                "import org.mybatis.spring.SqlSessionFactoryBean;\n" +
                "import org.springframework.context.annotation.Bean;\n" +
                "\n" +
                "import com.github.pagehelper.PageHelper;\n" +
                "\n" +
                "import tk.mybatis.spring.mapper.MapperScannerConfigurer;\n" +
                "public class MybatisConfig {\n" +
                "\n" +
                "\t/**\n" +
                "\t * 获得会话工厂\n" +
                "\t * @return\n" +
                "\t * @throws Exception\n" +
                "\t */\n" +
                "\t@Bean\n" +
                "\tpublic SqlSessionFactory sessionFactory(DataSource dataSource) throws Exception{\n" +
                "\t\t//1 创建factorybean\n" +
                "\t\tSqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();\n" +
                "\t\t//2 设置数据\n" +
                "\t\t//2.1 数据源 \n" +
                "\t\tfactoryBean.setDataSource(dataSource);\n" +
                "\t\t//2.2 驼峰\n" +
                "\t\tConfiguration configuration = new Configuration();\n" +
                "\t\tconfiguration.setMapUnderscoreToCamelCase(true);\n" +
                "\t\tfactoryBean.setConfiguration(configuration);\n" +
                "\t\t//2.3 分页插件\n" +
                "\t\tPageHelper pageHelper = new PageHelper();\n" +
                "\t\t\n" +
                "\t\tProperties props = new Properties();\n" +
                "\t\tprops.setProperty(\"dialect\", \"mysql\");\n" +
                "\t\tprops.setProperty(\"rowBoundsWithCount\", \"true\");\n" +
                "\t\tpageHelper.setProperties(props);\n" +
                "\t\t\n" +
                "\t\tfactoryBean.setPlugins(new Interceptor[]{ pageHelper});\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\t//3 获得对象\n" +
                "\t\treturn factoryBean.getObject();\n" +
                "\t}\n" +
                "\t\n" +
                "\t/**\n" +
                "\t * 映射扫描器\n" +
                "\t * @return\n" +
                "\t */\n" +
                "\t@Bean\n" +
                "\tpublic MapperScannerConfigurer mapperScannerConfigurer(){\n" +
                "\t\tMapperScannerConfigurer mapperScanner = new MapperScannerConfigurer();\n" +
                "\t\tmapperScanner.setBasePackage(\""+ artifactId +"."+projectName+".dao\");\n" +
                "\t\treturn mapperScanner;\n" +
                "\t}\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "}\n";

        //生成batis.java
        File batisJava=new File(prefix+"/config");
        batisJava.mkdirs();
        batisJava=new File(prefix+"/config/MybatisConfig.java");
        batisJava.delete();
        batisJava.createNewFile();
        append(batisJava,batisConfig);


        springConfig="package "+ artifactId +"."+projectName+".config;\r\nimport javax.sql.DataSource;\n" +
                "\n" +
                "import org.springframework.beans.factory.annotation.Value;\n" +
                "import org.springframework.context.annotation.Bean;\n" +
                "import org.springframework.context.annotation.ComponentScan;\n" +
                "import org.springframework.context.annotation.Configuration;\n" +
                "import org.springframework.context.annotation.PropertySource;\n" +
                "import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;\n" +
                "import org.springframework.jdbc.datasource.DataSourceTransactionManager;\n" +
                "import org.springframework.transaction.annotation.EnableTransactionManagement;\n" +
                "\n" +
                "import com.alibaba.druid.pool.DruidDataSource;\n" +
                "@ComponentScan(basePackages=\""+ artifactId +"."+projectName+".service\")\n" +
                "@EnableTransactionManagement\n" +
                "public class SpringConfig {\n" +
                "\t\n" +
                "\t/**\n" +
                "\t * 配置数据源\n" +
                "\t * @return\n" +
                "\t */\n" +
                "\t@Bean\n" +
                "\tpublic DataSource dataSource(){\n" +
                "\t\tDruidDataSource druidDataSource = new DruidDataSource();\n" +
                "\t\tdruidDataSource.setDriverClassName(\""+druidDataSource.getDriverClassName()+"\");\n" +
                "\t\tdruidDataSource.setUrl(\""+druidDataSource.getUrl()+"\");\n" +
                "\t\tdruidDataSource.setUsername(\""+druidDataSource.getUsername()+"\");\n" +
                "\t\tdruidDataSource.setPassword(\""+druidDataSource.getPassword()+"\");\n" +
                "\t\treturn druidDataSource;\n" +
                "\t}\n" +
                "\t/**\n" +
                "\t * bug修复\n" +
                "\t * @return\n" +
                "\t */\n" +
                "\t@Bean\n" +
                "\tpublic static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){\n" +
                "\t\treturn new PropertySourcesPlaceholderConfigurer();\n" +
                "\t}\n" +
                "\t\n" +
                "\t/**\n" +
                "\t * 配置事务管理器\n" +
                "\t * @param dataSource\n" +
                "\t * @return\n" +
                "\t */\n" +
                "\t@Bean\n" +
                "\tpublic DataSourceTransactionManager txManager(DataSource dataSource){\n" +
                "\t\treturn new DataSourceTransactionManager(dataSource);\n" +
                "\t}\n" +
                "\n" +
                "}";

        //生成spring.java
        File springJava=new File(prefix+"/config/");
        springJava.mkdirs();
        springJava=new File(prefix+"/config/SpringConfig.java");
        springJava.delete();
        springJava.createNewFile();
        append(springJava,springConfig);

        webIniter="package "+ artifactId +"."+projectName+".config;\r\nimport javax.servlet.FilterRegistration;\n" +
                "import javax.servlet.ServletContext;\n" +
                "import javax.servlet.ServletException;\n" +
                "import javax.servlet.ServletRegistration;\n" +
                "\n" +
                "import org.springframework.context.annotation.Configuration;\n" +
                "import org.springframework.web.WebApplicationInitializer;\n" +
                "import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;\n" +
                "import org.springframework.web.filter.CharacterEncodingFilter;\n" +
                "import org.springframework.web.servlet.DispatcherServlet;\n" +
                "\n" +
                "public class WebIniter implements WebApplicationInitializer {\n" +
                "\n" +
                "\t@Override\n" +
                "\tpublic void onStartup(ServletContext servletContext) throws ServletException {\n" +
                "\n" +
                "\n" +
                "\t\t//1. spring容器\n" +
                "\t\tAnnotationConfigWebApplicationContext application = new AnnotationConfigWebApplicationContext();\n" +
                "\t\tapplication.register(MybatisConfig.class);\n" +
                "\t\tapplication.register(SpringConfig.class);\n" +
                "\t\tapplication.register(MvcConfig.class);\n" +
                "\t\tapplication.setServletContext(servletContext);\n" +
                "\t\t\n" +
                "\t\t//2. post请求中文乱码过滤器\n" +
                "\t\tFilterRegistration.Dynamic encodingFilter = servletContext.addFilter(\"encoding\", new CharacterEncodingFilter(\"UTF-8\"));\n" +
                "\t\tencodingFilter.addMappingForUrlPatterns(null, true, \"/*\");\n" +
                "\t\t\n" +
                "\t\t//3. mvc 核心控制器\n" +
                "\t\tServletRegistration.Dynamic mvcServlet = servletContext.addServlet(\"springmvc\", new DispatcherServlet(application));\n" +
                "\t\tmvcServlet.addMapping(\"*.action\");\n" +
                "\t\tmvcServlet.setLoadOnStartup(2);\n" +
                "\t\t\n" +
                "\t}}";



        //生成webIniter.java
        File webinitJava=new File(prefix+"/config/");
        webinitJava.mkdirs();
        webinitJava=new File(prefix+"/config/WebIniter.java");
        webinitJava.delete();
        webinitJava.createNewFile();
        append(webinitJava,webIniter);


    }

    private void createClass() {
        //调用工具类生成bean/dao/service/controller

        new Creater(druidDataSource.getUrl(),druidDataSource.getUsername(),druidDataSource.getPassword())
                .allTable()
                .setBeanPackage(beanPackageName)
                .setMapperPackage(daoPackageName)
                .controllerPackage(controllerPackageName)
                .servicePackage(servicePackageName)
                .beanModule(beanDirName)
                .daoModulle(daoDirName)
                .serviceModule(serviceDirName)
                .controllerModule(controllerDirName)
                .projectName(projectName)
                .artifactId(artifactId)
                .handle();

    }

    private void addModule() {
        //往父项目中添加模块

        /**
         * <modules>
         *         <module>yycg-commons</module>
         *         <module>yycg-service</module>
         *         <module>yycg-dao</module>
         *         <module>yycg-web</module>
         *         <module>yycg-bean</module>
         *     </modules>
         */

        SAXReader reader=new SAXReader();

        File file=new File(_getProjectPath()+"pom.xml");

        try {
            Document read = null;
            try {
                read = reader.read(file);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Element rootElement = read.getRootElement();

            Element element = rootElement.addElement("modules");
            element.addElement("module").setText(beanDirName);
            element.addElement("module").setText(daoDirName);
            element.addElement("module").setText(serviceDirName);
            element.addElement("module").setText(controllerDirName);
            try {
                OutputFormat format = OutputFormat.createPrettyPrint();
                XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"),format);
                writer.write(read);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private void createModules() {
        createModule(beanDirName,beanPackageName);
        createModule(daoDirName,beanPackageName);
        createModule(serviceDirName,servicePackageName);
        createModule(controllerDirName,controllerPackageName);
        try {
            addDependency(daoDirName,new String[]{beanDirName});
            addDependency(serviceDirName,new String[]{daoDirName});
            addDependency(controllerDirName,new String[]{serviceDirName});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //为指定模块添加依赖
    private void addDependency(String neeDir, String[] dependencyDir) throws Exception {
        //通过模块名获得pom.xml的路径
        String pomPath=getPathByModuleName(neeDir)+"pom.xml";
        SAXReader saxReader=new SAXReader();
        Document read = saxReader.read(pomPath);
        Element rootElement = read.getRootElement();
        /**
         * <dependencies>
         *
         *     <dependency>
         *         <artifactId>org.zj</artifactId>
         *         <artifactId>yycg-commons</artifactId>
         *         <version>1.0-SNAPSHOT</version>
         *     </dependency>
         *
         * </dependencies>
         */

        for(String s:dependencyDir){
            Element dependencies = rootElement.addElement("dependencies");
            Element element = dependencies.addElement("dependency");
            element.addElement("groupId").setText(artifactId);
            element.addElement("artifactId").setText(s);
            element.addElement("version").setText(projectVersion);
        }

        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(pomPath),"utf-8"),format);
        writer.write(read);
        writer.close();

    }

    private void createModule(String dir, String packageName) {
        //创建文件夹
        new File(getProjectPath()+"/"+dir+"/src/main/java").mkdirs();
        new File(getProjectPath()+"/"+dir+"/src/main/resources").mkdirs();
        new File(getProjectPath()+"/"+dir+"/src/test/java").mkdirs();
        //创建pom.xml

        try {
            File file = new File(getProjectPath() + "/" + dir + "/pom.xml");
            file.delete();
            file.createNewFile();

            //写入
            append(file,basePom);

            setParentAndSelf(dir);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //为指定模块设置parent标签和<artifactId
    private void setParentAndSelf(String dir) {
        //通过dom4j
        String pomPath=getPathByModuleName(dir)+"pom.xml";

        /**
         * <parent>
         *         <artifactId>yycg</artifactId>
         *         <artifactId>org.zj</artifactId>
         *         <version>1.0-SNAPSHOT</version>
         *     </parent>
         *
         *      <artifactId>yycg-bean</artifactId>
         *
         */

        SAXReader saxReader=new SAXReader();
        try {
            Document read = saxReader.read(pomPath);
            Element rootElement = read.getRootElement();

            Element parent= rootElement.addElement("parent");
            parent.addElement("artifactId").setText(projectName);
            parent.addElement("groupId").setText(artifactId);
            parent.addElement("version").setText(projectVersion);

            rootElement.addElement("artifactId").setText(dir);
            rootElement.addElement("modelVersion").setText("4.0.0");


            try {

                OutputFormat format = OutputFormat.createPrettyPrint();
                XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(pomPath),"utf-8"),format);
                writer.write(read);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        //<parent>
        //<artifId>

    }

    private void inflateData() {

        DataSource dataSource= (DataSource) c.getAnnotation(DataSource.class);
        setDataSource(dataSource,druidDataSource);
        Bean bean= (Bean) c.getAnnotation(Bean.class);
        Controller controller= (Controller) c.getAnnotation(Controller.class);
        Dao dao= (Dao) c.getAnnotation(Dao.class);
        Service service= (Service) c.getAnnotation(Service.class);

        ParentProject parentProject= (ParentProject) c.getAnnotation(ParentProject.class);

        projectName=parentProject.name();
        projectVersion=parentProject.version();
        artifactId =parentProject.artifactId();


        beanPackageName=bean.packageName();
        beanDirName=bean.dir();
        controllerDirName=controller.dir();
        controllerPackageName=controller.packageName();
        serviceDirName=service.dir();
        servicePackageName=service.packageName();
        daoPackageName=dao.packageName();
        daoDirName=dao.dir();

    }


    private void setDataSource(DataSource dataSource, DruidDataSource druidDataSource) {
        String username=dataSource.username();
        String password=dataSource.password();
        String url=dataSource.url();
        String driver=dataSource.driver();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName(driver);
    }

    private String getProjectPath() {
        String path = null;
        try {
            path = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
            // System.out.println(path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // System.out.println(path);

        path = path.substring(1, path.indexOf("/target/classes"));
        //System.out.println(path);
        return path;
    }


    private void append(File file, String content) {
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.append(content);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPathByModuleName(String moduleName){
        return _getProjectPath()+moduleName+"/";
    }

    private String _getProjectPath(){
        return System.getProperty("user.dir")+"/";
    }

}
