import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动生成bean工具
 *
 * @author 张君
 */
public class Creater {

    private String projectName;
    private String artifactId;
    private String beanDir;
    private String daoDir;
    private String serviceDir;
    private String controllerDir;
    private String servicePackage;
    private String controllerPackage;

    private Map<String,List<String>> primaryKeyMap = new HashMap<>();
    private String beanPackage;
    private String mapperPackage;
    private String username;
    private String password;
    private List<String> tables = new ArrayList<>();
    private String url;

    public Map<String, List<String>> getPrimaryKeyMap() {
        return primaryKeyMap;
    }

    public void setPrimaryKeyMap(Map<String, List<String>> primaryKeyMap) {
        this.primaryKeyMap = primaryKeyMap;
    }

    private HashMap<String, ClassBean> map = new HashMap<>();


    public String getBeanPackage() {
        return beanPackage;
    }

    public Creater setBeanPackage(String beanPackage) {
        this.beanPackage = beanPackage;
        return this;
    }

    public String getUrl() {
        return url;
    }


    public String getMapperPackage() {
        return mapperPackage;
    }

    public Creater setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
        return this;
    }

    public Creater allTable() {
        String sql = "show tables";
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            List<String> table = new ArrayList<>();
            while (resultSet.next()) {
                table.add(resultSet.getString(1));
            }
            this.tables = table;
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    //通过包名获得绝对路径
    private String getRelativePath(String packageName) {
        String srcPath = System.getProperty("user.dir") + "\\src";
        System.out.println(packageName + "  packname");
        packageName = packageName.replace(".", "\\");
        System.out.println(packageName + "   packagename");
        System.out.println(srcPath + "\\" + packageName);
        return srcPath + "\\" + packageName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public Creater(String url, String username, String password) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    public Creater addTable(String name) {
        tables.add(name);
        return this;
    }

    //放字段类型和名字
    private class ClassBean {
        //class 字段 类型的键值对
        private List<FieldBean> fieldList = new ArrayList<>();
        //导入list
        private List<String> importList = new ArrayList<>();

        public ClassBean() {
        }

        public ClassBean(List<FieldBean> fieldMap, List<String> importList) {
            this.fieldList = fieldMap;
            this.importList = importList;
        }

        public List<FieldBean> getFieldList() {
            return fieldList;
        }

        public void setFieldList(List<FieldBean> fieldList) {
            this.fieldList = fieldList;
        }

        public List<String> getImportList() {
            return importList;
        }

        public void setImportList(List<String> importList) {
            this.importList = importList;
        }
    }


    private class FieldBean {
        private boolean isPrimaryKey;
        private String type;
        private String name;

        public FieldBean() {
        }

        public FieldBean(boolean isPrimaryKey, String type, String name) {
            this.isPrimaryKey = isPrimaryKey;
            this.type = type;
            this.name = name;
        }

        public boolean isPrimaryKey() {
            return isPrimaryKey;
        }

        public void setPrimaryKey(boolean primaryKey) {
            isPrimaryKey = primaryKey;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public Creater controllerPackage(String controllerPackage){
        this.controllerPackage=controllerPackage;
        return this;
    }

public Creater servicePackage(String servicePackage){
        this.servicePackage=servicePackage;
        return this;
}
public Creater beanModule(String beanDir){
        this.beanDir=beanDir;
        return this;
}
    public Creater daoModulle(String daoDir){
        this.daoDir=daoDir;
        return this;
    }public Creater serviceModule(String serviceDir){
        this.serviceDir=serviceDir;
        return this;
    }public Creater controllerModule(String controllerDir){
        this.controllerDir=controllerDir;
        return this;
    }

public Creater artifactId(String name){
        this.artifactId=name;
        return this;
}


    public Creater projectName(String projectName){
        this.projectName=projectName;
        return this;
    }

    //获得所有数据
    private void setData() throws Exception {

        setPrimaryKeyData();


        //创建连接
        Connection conn = DriverManager.getConnection(this.url, this.username, this.password);
        Statement statement = conn.createStatement();
        //遍历每个表
        for (String tableName : tables) {
            System.out.println("tablename:" + tableName);
            String sql = "select * from `" + tableName + "`";
            ResultSet resultSet = statement.executeQuery(sql);
            //遍历表里的每个字段，放到map中

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<ClassBean> list = new ArrayList<>();
            //获得的索引从1开始
            List<String> importList = new ArrayList<>();
            List<FieldBean> fieldBeanList = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                System.out.println(i);
                String columnName = metaData.getColumnName(i);
                System.out.println(" columnName:" + columnName);
                int columnType = metaData.getColumnType(i);
                addToImportList(importList, columnType,columnName,tableName);
                fieldBeanList.add(new FieldBean(isPrimaryKey(tableName, columnName), getJavaTypeString(columnType), columnName));
            }
            map.put(tableName, new ClassBean(fieldBeanList, importList));

        }
        statement.close();
        conn.close();
    }

    private void setPrimaryKeyData() {

        for(String tableName:tables){
            List<String> columnList=new ArrayList<>();
        String sql = "select * from " + tableName;
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);

            while (primaryKeys.next()) {
                String columnName= primaryKeys.getString(4);
                System.out.println(columnName+"主键");
                columnList.add(columnName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
            getPrimaryKeyMap().put(tableName,columnList);
        }
    }

    //判断指定字段名是否是主键
    private boolean isPrimaryKey(String tableName, String columnName) {
        return getPrimaryKeyMap().get(tableName).contains(columnName);
    }

    //根据接收到的sqltype 数值来判断类型并返回
    private String getJavaTypeString(int type) {

        System.out.println("  type:" + type);

        if (type == Types.TIME || type == Types.DATE || type == Types.TIMESTAMP) {
            return "Date";
        }
        if (type == Types.FLOAT||type==Types.REAL) {
            return "float";
        }
        if (type == Types.DOUBLE) {
            return "double";
        }
        if (type == Types.INTEGER) {
            return "Integer";
        }
        if (type == Types.BOOLEAN) {
            return "boolean";
        }


        return "String";
    }

    private void addToImportList(List<String> list, int type,String columnName,String tableName) {
        //类型判断是否需要导包
        //然后放到importList

        if(isPrimaryKey(tableName,columnName)){
            if(!list.contains("javax.persistence.Id")){
                list.add("javax.persistence.Id");
            }
        }

        switch (type) {
            case Types.DATALINK:
            case Types.DATE:
            case Types.TIMESTAMP:
                if (!list.contains("java.util.Date")) {
                    list.add("java.util.Date");
                }
                break;
        }


    }


    //创建文件
    public Creater handle() {
        try {
            setData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //创建java文件

        handleBean();
        //创建通用mapper
        handleMapper();

        //生成service
        handleService();

        //生成controller
        handleController();

        return this;
    }

    //生成controller
    private void handleController() {
        for (String str : tables) {

            String path=getRelativePath(this.controllerDir) + "\\";
            if(controllerDir!=null&&controllerDir!=""){
                path=getModulePath(controllerDir,artifactId+"."+projectName+".controller.");
            }

            File file = new File(path + toCamelCase(1, str) + "Controller.java");

            System.out.println("          <<<"+file.getAbsolutePath());

            setFile(file);
            append(file, "package " + artifactId+"."+projectName + ".controller;\r\n");
            append(file,"import org.springframework.stereotype.Controller;\r\n" +
                    "import org.springframework.web.bind.annotation.RequestMapping;\r\n");
            append(file,"import org.springframework.beans.factory.annotation.Autowired;\r\n");
            append(file,"import "+artifactId+"."+projectName+".service.I"+toCamelCase(1,str)+"Service;\r\n");
            append(file, "@Controller \r\n");
            append(file, "@RequestMapping(\"/"+str+"\") \r\n");
            append(file, "public class " + toCamelCase(1, str) + "Controller {\r\n");
            append(file,"@Autowired\r\n");
            append(file,"I"+toCamelCase(1,str)+"Service "+toCamelCase(0,str)+"Service;\r\n}");
        }
    }

    //生成service
    private void handleService() {
        for (String str : tables) {

            String path=getRelativePath(this.serviceDir) + "\\";
            if(serviceDir!=null&&serviceDir!=""){
                path=getModulePath(serviceDir,artifactId+"."+projectName+".service.");
            }
            File file = new File(path +"I" + toCamelCase(1, str) + "Service.java");
            setFile(file);
            append(file, "package " + artifactId+"."+projectName + ".service;\r\n");
            append(file, "public interface I" + toCamelCase(1, str) + "Service {\r\n}");
        }

        for (String str : tables) {

            String path=getRelativePath(this.serviceDir) + "\\";
            if(serviceDir!=null&&serviceDir!=""){
                path=getModulePath(serviceDir,artifactId+"."+projectName+".service.impl.");
            }
            File file = new File(path  + toCamelCase(1, str) + "ServiceImpl.java");

            setFile(file);
            append(file, "package " + artifactId+"."+projectName + ".service.impl;\r\n");
            append(file,"import org.springframework.stereotype.Service;\r\n" +
                    "import org.springframework.transaction.annotation.Transactional;\r\n");
            append(file,"import "+artifactId+"."+projectName+".service.I"+toCamelCase(1,str)+"Service;\r\n");
            append(file,"import "+artifactId+"."+projectName+".dao."+toCamelCase(1,str)+"Mapper;\r\n");
            append(file,"import org.springframework.beans.factory.annotation.Autowired;");
            append(file, "@Service \r\n");
            append(file, "@Transactional \r\n");
            append(file, "public class " + toCamelCase(1, str) + "ServiceImpl implements I"+toCamelCase(1,str)+"Service {\r\n");
            append(file,"@Autowired\r\n");
            append(file,toCamelCase(1,str)+"Mapper "+toCamelCase(0,str)+"Mapper;\r\n}");
        }

    }

    public void handleBean() {

        for (String key : map.keySet()) {
            System.out.println(key);
            //这里创建文件

            String path=getRelativePath(this.beanPackage) + "\\";
            if(beanDir!=null&&beanDir!=""){
                path=getModulePath(beanDir,artifactId+"."+projectName+".bean.");
            }

            File file = new File( path+ toCamelCase(1, key) + ".java");
            System.out.println(file.getPath() + "----------");
            setFile(file);

            //package 声明
            append(file, "package " + artifactId+"."+projectName + ".bean;\r\n");

            //导包文本添加
            for (String str : map.get(key).getImportList()) {
                append(file, "import " + str + ";\r\n");
            }

            append(file, "public class " + toCamelCase(1, key) + "{\r\n");
            for (FieldBean fieldBean : map.get(key).getFieldList()) {
                String type = fieldBean.getType();
                String name = fieldBean.getName();
                //写入文本
                //写入变量声明部分

                if (fieldBean.isPrimaryKey()) {
                    //如果是主键,就写入
                    append(file,"@Id\r\n");
                    append(file, "private " + type + " " + toCamelCase(0, name) + ";\r\n");
                } else {
                    append(file, "private " + type + " " + toCamelCase(0, name) + ";\r\n");
                }
            }
            append(file, "\r\n");
            for (FieldBean fieldBean : map.get(key).getFieldList()) {
                String type = fieldBean.getType();
                String name = fieldBean.getName();
                //写入setter getter
                append(file, "public void set" + toCamelCase(1, name) + "(" + type + " " + toCamelCase(0, name) + "){ \r\nthis." + toCamelCase(0, name) + "=" + toCamelCase(0, name) + ";\r\n}\r\n");
                append(file, "public " + type + " get" + toCamelCase(1, name) + "(){ \r\nreturn " + toCamelCase(0, name) + ";\r\n}\r\n");
                System.out.println("  " + type + ":" + type);
            }
            append(file, "\r\n}");
        }
    }

    public void handleMapper() {
        for (String str : tables) {

            String path=getRelativePath(this.daoDir) + "\\";
            if(daoDir!=null&&daoDir!=""){
                path=getModulePath(daoDir,artifactId+"."+projectName+".dao.");
            }
            File file = new File(path + toCamelCase(1, str) + "Mapper.java");
            setFile(file);
            append(file, "package " + artifactId+"."+projectName + ".dao;\r\n");
            append(file, "import " + artifactId+"."+projectName + ".bean." + toCamelCase(1, str) + ";\r\n");
            append(file, "import tk.mybatis.mapper.common.Mapper;\r\n");
            append(file, "public interface " + toCamelCase(1, str) + "Mapper extends Mapper<" + toCamelCase(1, str) + ">{\r\n}");
        }
    }

    //如果文件存在，那就删除然后新建，不存在就直接新建
    private void setFile(File file) {

        file.mkdirs();

        //如果不存在
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    //java驼峰转换
    public String toCamelCase(int flag, String name) {
        int index = 0;
        for (int i = 0; i < name.length(); i++) {
            if (name.substring(i, i + 1).equals("_")) {
                System.out.println("前面:" + name.substring(0, i));

                /*System.out.println("后面"+name.substring(i+1));
                 */
                //判断_后面是否还有字符

                System.out.println(name.substring(i + 1) + "----0000");

                if (name.substring(i + 1).length() == 0) {
                    name = name = name.substring(0, i);
                } else {
                    System.out.println("后面的哦" + name.substring(i + 1) + "---");
                    name = name.substring(0, i) + name.substring(i + 1).substring(0, 1).toUpperCase() + name.substring(i + 2);
                }
            }
        }
        if (flag == 0) {
            if (name.length() > 1) {
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
            } else {
                name = name.substring(0).toLowerCase();
            }
        } else {
            //如果flag=1 大驼峰

            if (name.length() > 1) {
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
            } else {
                name = name.substring(0).toUpperCase();
            }
        }
        return name;
    }

    public String getModulePath(String moduleName,String modulePackage){

        String path=modulePackage.replace(".","/");
String result=System.getProperty("user.dir")+"/"+moduleName+"/src/main/java/"+path;

        System.out.println("             >>>>"+result+"  模块路径");

        return result;
    }


}
