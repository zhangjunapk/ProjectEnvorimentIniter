import org.springframework.web.bind.annotation.RequestMapping;
import sun.text.resources.cldr.dyo.FormatData_dyo;

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
    private Map<String, List<ClassBean>> fieldMap = new HashMap<>();


    private String indexBefore = "<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\" %>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>Title</title>\n" +
            "    <script type=\"text/javascript\" src=\"/static/js/jquery-1.12.4.js\"></script>\n" +
            "    <script type=\"text/javascript\" src=\"/static/js/layui.js\"></script>\n" +
            "    <link rel=\"stylesheet\" href=\"/static/css/layui.css\">\n" +
            "    <script type=\"text/javascript\" src=\"/static/js/layer.js\"></script>\n" +
            "    <link rel=\"stylesheet\" href=\"/static/css/layer.css\">\n" +
            "\n" +
            "    <script>\n" +
            "\n" +
            "        $(function () {\n" +
            "            layui.use('element', function () {\n" +
            "                var element = layui.element;\n" +
            "\n" +
            "                //一些事件监听\n" +
            "                element.on('tab(demo)', function (data) {\n" +
            "                    console.log(data);\n" +
            "                });\n" +
            "            });\n" +
            "\n" +
            "        })\n" +
            "\n" +
            " function change(link) {\n" +
            "            $(\"#iframe\").attr(\"src\",link)\n" +
            "        }" +
            "    </script>\n" +
            "\n" +
            "</head>\n" +
            "<body>\n" +
            "<div class=\"layui-side layui-bg-black\">\n" +
            "    <div class=\"layui-side-scroll\">\n" +
            "\n" +
            "        <ul class=\"layui-nav layui-nav-tree site-demo-nav\">\n";


    private String projectName;
    private String artifactId;
    private String beanDir;
    private String daoDir;
    private String serviceDir;
    private String controllerDir;
    private String servicePackage;
    private String controllerPackage;

    private String webDirName;


    private Map<String, List<MenuMapping>> menuMap = new HashMap<>();

    private Map<String, List<String>> primaryKeyMap = new HashMap<>();
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

    public Creater webDir(String webDirName) {
        this.webDirName = webDirName;
        return this;
    }

    public Creater fieldMap(Map<String, List<ClassBean>> fieldMap) {
        this.fieldMap = fieldMap;
        return this;
    }

    //放字段类型和名字
    public static class ClassBean {
        //class 字段 类型的键值对
        private List<FieldBean> fieldList = new ArrayList<>();
        //导入list
        private List<String> importList = new ArrayList<>();

        private String alias;

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public ClassBean(List<FieldBean> fieldList, List<String> importList, String alias) {
            this.fieldList = fieldList;
            this.importList = importList;
            this.alias = alias;
        }

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


    public static class FieldBean {
        private boolean isPrimaryKey;
        private String type;
        private String name;
        private String alias;//别名

        public FieldBean() {
        }

        public FieldBean(boolean isPrimaryKey, String type, String name, String alias) {
            this.isPrimaryKey = isPrimaryKey;
            this.type = type;
            this.name = name;
            this.alias = alias;
        }

        public FieldBean(boolean isPrimaryKey, String type, String name) {
            this.isPrimaryKey = isPrimaryKey;
            this.type = type;
            this.name = name;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
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

    public Creater controllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
        return this;
    }

    public Creater servicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
        return this;
    }

    public Creater beanModule(String beanDir) {
        this.beanDir = beanDir;
        return this;
    }

    public Creater daoModulle(String daoDir) {
        this.daoDir = daoDir;
        return this;
    }

    public Creater serviceModule(String serviceDir) {
        this.serviceDir = serviceDir;
        return this;
    }

    public Creater controllerModule(String controllerDir) {
        this.controllerDir = controllerDir;
        return this;
    }

    public Creater artifactId(String name) {
        this.artifactId = name;
        return this;
    }


    public Creater projectName(String projectName) {
        this.projectName = projectName;
        return this;
    }


    public Creater menuMap(Map<String, List<MenuMapping>> menuMap) {
        this.menuMap = menuMap;
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
                addToImportList(importList, columnType, columnName, tableName);
                fieldBeanList.add(new FieldBean(isPrimaryKey(tableName, columnName), getJavaTypeString(columnType), columnName));
            }
            map.put(tableName, new ClassBean(fieldBeanList, importList));

        }
        statement.close();
        conn.close();
    }

    private void setPrimaryKeyData() {

        for (String tableName : tables) {
            List<String> columnList = new ArrayList<>();
            String sql = "select * from " + tableName;
            try {
                Connection conn = DriverManager.getConnection(url, username, password);
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);

                while (primaryKeys.next()) {
                    String columnName = primaryKeys.getString(4);
                    System.out.println(columnName + "主键");
                    columnList.add(columnName);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            getPrimaryKeyMap().put(tableName, columnList);
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
        if (type == Types.FLOAT || type == Types.REAL) {
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

    private void addToImportList(List<String> list, int type, String columnName, String tableName) {
        //类型判断是否需要导包
        //然后放到importList

        if (isPrimaryKey(tableName, columnName)) {
            if (!list.contains("javax.persistence.Id")) {
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

        handleView();
        return this;
    }

    /**
     * 生成jsp页面
     */
    private void handleView() {
        //生成主页
        File file = new File(getWebPath(webDirName) + "index.jsp");
        setFile(file);
        append(file, indexBefore);
        for (Map.Entry<String, List<MenuMapping>> entry : menuMap.entrySet()) {
            append(file, "<li class=\"layui-nav-item layui-nav-itemed\">\n" +
                    "                <a class=\"javascript:;\" href=\"javascript:;\">" + entry.getKey() + "<span class=\"layui-nav-more\"></span></a>\n" +
                    "                <dl class=\"layui-nav-child\">");
            for (MenuMapping menuMapping : entry.getValue()) {
                append(file, "<dd>\n" +
                        "                        <a href='javascript:void(0)' onclick=\"change('/jsp/" + menuMapping.getTableName() + ".jsp')\">" + menuMapping.getAlias() + "</a>\n" +
                        "                    </dd>");
            }
            append(file, "</dl>");
            append(file, "</li>");
        }
        append(file, "\n" +
                "            <li class=\"layui-nav-item\" style=\"height: 30px; text-align: center\"></li>\n" +
                "            <span class=\"layui-nav-bar\" style=\"top: 107.5px; height: 0px; opacity: 0;\"></span></ul>\n" +
                "\n" +
                "    </div>\n" +
                "</div>\n" +
                "<iframe id=\"iframe\" src=\"/jsp/user.jsp\" height=\"800px\" width=\"1720px\" style=\"margin-left:200px\"></iframe>\n" +
                "</body>\n" +
                "</html>\n");

        //生成每个子页面的主页

        String tableHeadBefore = "<div class=\"layui-form layui-border-box layui-table-view\" lay-filter=\"LAY-table-1\" style=\" height:332px;\">\n" +
                "    <div class=\"layui-table-box\">\n" +
                "        <div class=\"layui-table-header\">\n" +
                "            <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"layui-table\">\n" +
                "                <thead>";

        for (Map.Entry<String, List<ClassBean>> entry : fieldMap.entrySet()) {
            File f = new File(getWebPath(webDirName) + "/jsp/" + entry.getKey() + ".jsp");
            setFile(f);
            String baseChildJspBefore = "<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\" %>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>Title</title>\n" +
                    "    <script type=\"text/javascript\" src=\"/static/js/jquery-1.12.4.js\"></script>\n" +
                    "    <script type=\"text/javascript\" src=\"/static/js/layui.js\"></script>\n" +
                    "    <link rel=\"stylesheet\" href=\"/static/css/layui.css\">\n" +
                    "    <script type=\"text/javascript\" src=\"/static/js/layer.js\"></script>\n" +
                    "    <link rel=\"stylesheet\" href=\"/static/css/layer.css\">\n" +
                    "\n" +
                    "    <script>\n" +
                    "function delete_item(id){\r\n" +
                    "$.ajax({" +
                    "url:\"/" + entry.getKey() + "/delete_" + entry.getKey() + ".action\"" +
                    "\r\n,data:{" +
                    "ids:id}," +
                    "async:false," +
                    "dataType:'json'" +
                    "})\r\n" +
                    "init()\r\n" +
                    "}" +
                    "function init()\r\n{getPage(1,10)}\r\n" +
                    "function getPage(page,pageNum){\n" +
                    "            $.ajax({\n" +
                    "                url: \"/category/get_category.action\",\n" +
                    "data:{page:page,pageNum:pageNum},\r\n" +
                    "                dataType: \"json\",\n" +
                    "                success: (function (data) {\n" +
                    "$(\"#container\").html(\"\")\r\n" +
                    "                    $(data).each(function () {\n" +
                    "                        handleData(this);\n" +
                    "                    })\n" +
                    "                })\n" +
                    "            })\n" +
                    "        }" +
                    "        $(function () {\n" +
                    "init()" +
                    "        })\n";

            String tableHead = null;
            String baseChildJspHandleData = null;
            for (ClassBean classBean : entry.getValue()) {


                tableHead = " <tr>\n" +
                        "                    \n" +
                        "\n" +
                        "                \n";

                baseChildJspHandleData = "function handleData(data) {\n" +
                        "            $(\"#container\").append(\"<tr >\\n\" +\n";

                for (FieldBean fieldBean : classBean.getFieldList()) {


                    tableHead += "<th >\n" +
                            "                        <div class='layui-table-cell laytable-cell-1-username'><span>" + fieldBean.getAlias() + "</span></div>\n" +
                            "                    </th>";
                    baseChildJspHandleData += "+\"<td ><div class='layui-table-cell laytable-cell-1-sex'>\"+data." + fieldBean.getName() + "+\"</div></td>\"";
                }
                tableHead += "\n" +
                        "                </tr>\n" +
                        "                </thead>\n" +
                        "            </table>\n" +
                        "        </div>";
                baseChildJspHandleData += "+\" <td data-field='9' align='center' data-off='true'>\"\n" +
                        "+ \"                       <div class='layui-table-cell laytable-cell-1-9'>\"\n" +
                        "+\"                               <a  class='layui-btn layui-btn-primary layui-btn-xs' lay-event='detail'>查看</a> \"\n" +
                        " +\"                               <a class='layui-btn layui-btn-xs' lay-event='edit'>编辑</a> \"\n" +
                        " +\"                               <a class='layui-btn layui-btn-danger layui-btn-xs' lay-event='del' href='javascript:void(0)' onclick=\\\"delete_item('\"+data." + getId(entry.getKey()) + "+\"')\\\"" + ">删除</a></div>\"\r\n" +
                        " +\"                   </td>\"\n" +
                        "+\"                </tr>\")}\r\n</script>\r\n</head>\"";

                String ass = "\n" +
                        "                </thead>\n" +
                        "            </table>\n" +
                        "        </div>\n" +
                        "        <div class=\"layui-table-body layui-table-main\" style=\"height: 251px;\">\n" +
                        "            <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"layui-table\">\n" +
                        "                <tbody id=\"container\">\n" +
                        "                <tr >\n" +
                        "                    <td >\n" +
                        "                        <div class=\"layui-table-cell laytable-cell-1-id\">10000</div>\n" +
                        "                    </td>\n" +
                        "                    <td >\n" +
                        "                        <div class=\"layui-table-cell laytable-cell-1-username\">user-0</div>\n" +
                        "                    </td>\n" +
                        "                    <td >\n" +
                        "                        <div class=\"layui-table-cell laytable-cell-1-sex\">女</div>\n" +
                        "                    </td>\n" +
                        "\n" +
                        "                    <td data-field=\"9\" align=\"center\" data-off=\"true\">\n" +
                        "                        <div class=\"layui-table-cell laytable-cell-1-9\"><a\n" +
                        "                                class=\"layui-btn layui-btn-primary layui-btn-xs\" lay-event=\"detail\">查看</a> <a\n" +
                        "                                class=\"layui-btn layui-btn-xs\" lay-event=\"edit\">编辑</a> <a\n" +
                        "                                class=\"layui-btn layui-btn-danger layui-btn-xs\" lay-event=\"del\">删除</a></div>\n" +
                        "                    </td>\n" +
                        "                </tr>\n" +
                        "\n" +
                        "                </tbody>\n" +
                        "            </table>\n" +
                        "        </div>\n" +
                        "        <div class=\"layui-table-fixed layui-table-fixed-l\">\n" +
                        "            <div class=\"layui-table-header\">\n" +
                        "                <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"layui-table\">\n" +
                        "                    <thead>\n" +
                        "                    </thead>\n" +
                        "                </table>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "        <div class=\"layui-table-fixed layui-table-fixed-r\" style=\"right: 16px;\">\n" +
                        "            <div class=\"layui-table-header\">\n" +
                        "                <table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"layui-table\">\n" +
                        "                    <thead>\n" +
                        "                    <tr>\n" +
                        "                        <th data-field=\"9\">\n" +
                        "                            <div class=\"layui-table-cell laytable-cell-1-9\" align=\"center\"><span></span></div>\n" +
                        "                        </th>\n" +
                        "                    </tr>\n" +
                        "                    </thead>\n" +
                        "                </table>\n" +
                        "                <div class=\"layui-table-mend\"></div>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "    <div class=\"layui-table-page\">\n" +
                        "        <div id=\"layui-table-page1\">\n" +
                        "            <div class=\"layui-box layui-laypage layui-laypage-default\" id=\"layui-laypage-2\"><a href=\"javascript:;\"\n" +
                        "                                                                                               class=\"layui-laypage-prev layui-disabled\"\n" +
                        "                                                                                               data-page=\"0\"><i\n" +
                        "                    class=\"layui-icon\">\uE603</i></a><span class=\"layui-laypage-curr\"><em\n" +
                        "                    class=\"layui-laypage-em\"></em><em>1</em></span><a href=\"javascript:;\" data-page=\"2\">2</a><a\n" +
                        "                    href=\"javascript:;\" data-page=\"3\">3</a><span class=\"layui-laypage-spr\">…</span><a\n" +
                        "                    href=\"javascript:;\" class=\"layui-laypage-last\" title=\"尾页\" data-page=\"100\">100</a><a\n" +
                        "                    href=\"javascript:;\" class=\"layui-laypage-next\" data-page=\"2\"><i class=\"layui-icon\">\uE602</i></a><span\n" +
                        "                    class=\"layui-laypage-skip\">到第<input type=\"text\" min=\"1\" value=\"1\" class=\"layui-input\">页<button\n" +
                        "                    type=\"button\" class=\"layui-laypage-btn\">确定</button></span><span\n" +
                        "                    class=\"layui-laypage-count\">共 1000 条</span><span class=\"layui-laypage-limits\"><select lay-ignore=\"\"><option\n" +
                        "                    value=\"10\" selected=\"\">10 条/页</option><option value=\"20\">20 条/页</option><option\n" +
                        "                    value=\"30\">30 条/页</option><option value=\"40\">40 条/页</option><option value=\"50\">50 条/页</option><option\n" +
                        "                    value=\"60\">60 条/页</option><option value=\"70\">70 条/页</option><option value=\"80\">80 条/页</option><option\n" +
                        "                    value=\"90\">90 条/页</option></select></span></div>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "    <style>.laytable-cell-1-id {\n" +
                        "        width: 80px;\n" +
                        "    }\n" +
                        "\n" +
                        "    .laytable-cell-1-username {\n" +
                        "        width: 80px;\n" +
                        "    }\n" +
                        "\n" +
                        "    .laytable-cell-1-sex {\n" +
                        "        width: 80px;\n" +
                        "    }\n" +
                        "\n" +
                        "    .laytable-cell-1-city {\n" +
                        "        width: 80px;\n" +
                        "    }\n" +
                        "\n" +
                        "    .laytable-cell-1-sign {\n" +
                        "        width: 170px;\n" +
                        "    }\n" +
                        "\n" +
                        "    .laytable-cell-1-experience {\n" +
                        "        width: 80px;\n" +
                        "    }\n" +
                        "\n" +
                        "    .laytable-cell-1-score {\n" +
                        "        width: 80px;\n" +
                        "    }\n" +
                        "\n" +
                        "    .laytable-cell-1-classify {\n" +
                        "        width: 80px;\n" +
                        "    }\n" +
                        "\n" +
                        "    .laytable-cell-1-wealth {\n" +
                        "        width: 135px;\n" +
                        "    }\n" +
                        "\n" +
                        "    .laytable-cell-1-9 {\n" +
                        "        width: 165px;\n" +
                        "    }</style>\n" +
                        "</div>\n" +
                        "\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>";

                append(f, baseChildJspBefore);
                append(f, baseChildJspHandleData);
                append(f, tableHeadBefore);
                append(f, tableHead);
                append(f, ass);
            }


        }

        //生成每个子页面的添加页面
        for (Map.Entry<String, List<ClassBean>> entry : fieldMap.entrySet()) {
            //遍历每一个class
            String item="";
            for (ClassBean classBean : entry.getValue()) {
                //生成文件
                File f = new File(getWebPath(webDirName) + "/jsp/add_" + convert(entry.getKey()) + ".jsp");
                setFile(f);

                String before = "\n" +
                        "<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\" %>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <title>Title</title>\n" +
                        "    <script type=\"text/javascript\" src=\"/static/js/jquery-1.12.4.js\"></script>\n" +
                        "    <script type=\"text/javascript\" src=\"/static/js/layui.js\"></script>\n" +
                        "    <link rel=\"stylesheet\" href=\"/static/css/layui.css\">\n" +
                        "    <script type=\"text/javascript\" src=\"/static/js/layer.js\"></script>\n" +
                        "    <link rel=\"stylesheet\" href=\"/static/css/layer.css\">\n" +
                        "\n" +
                        "</head>";
                before += "\n" +
                        "<body>\n" +
                        "<form class=\"layui-form\" action=\"/" + entry.getKey() + "/add_" + entry.getKey() + ".action\">";
                for(FieldBean fieldBean:classBean.getFieldList()) {
                    item += "<div class=\"layui-form-item\">\n" +
                            "        <label class=\"layui-form-label\">" + fieldBean.getAlias() + "</label>\n" +
                            "        <div class=\"layui-input-block\">\n" +
                            "            <input type=\"text\" name=\""+fieldBean.getName()+"\" required=\"\" lay-verify=\"required\" placeholder=\"请输入"+fieldBean.getAlias()+"\" autocomplete=\"off\"\n" +
                            "                   class=\"layui-input\">\n" +
                            "        </div>\n" +
                            "    </div>";
                }
                String ass="<div class=\"layui-form-item\">\n" +
                        "        <div class=\"layui-input-block\">\n" +
                        "            <button class=\"layui-btn\" lay-submit=\"\" lay-filter=\"formDemo\">立即提交</button>\n" +
                        "            <button type=\"reset\" class=\"layui-btn layui-btn-primary\">重置</button>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</form>\n" +
                        "</body>\n" +
                        "</html>\n";
                append(f,before);
                append(f,item);
                append(f,ass);
            }
        }

        ///生成每个子页面的编辑页面
        for (Map.Entry<String, List<ClassBean>> entry : fieldMap.entrySet()) {
            //遍历每一个class
            File f=new File(getWebPath(webDirName)+"/jsp/edit_"+entry.getKey()+".jsp");
            setFile(f);

            for (ClassBean classBean : entry.getValue()) {
                // TODO: 2018/7/15
                String body="\n" +
                        "<body>\n" +
                        "<form class=\"layui-form\" action=\"/category/modify_"+entry.getKey()+".action\" id=\"form\">\n" +
                        "    <input type=\"hidden\" name=\"id\" value=\"\" id=\"id\"/>";
                String before="<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\" %>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <title>Title</title>\n" +
                        "    <script type=\"text/javascript\" src=\"/static/js/jquery-1.12.4.js\"></script>\n" +
                        "    <script type=\"text/javascript\" src=\"/static/js/layui.js\"></script>\n" +
                        "    <link rel=\"stylesheet\" href=\"/static/css/layui.css\">\n" +
                        "    <script type=\"text/javascript\" src=\"/static/js/layer.js\"></script>\n" +
                        "    <link rel=\"stylesheet\" href=\"/static/css/layer.css\">\n" +
                        "    <script>\n";

                String handleData = "\n" +
                        "        $.ajax({\n" +
                        "            url: \"/" + entry.getKey() + "/get_" + entry.getKey() + "_by_id.action\",\n" +
                        "            data: {\n" +
                        "                id:${param.id}\n" +
                        "            },\n" +
                        "            dataType: \"json\",\n" +
                        "            success: function (data) {";
                for(FieldBean fieldBean:classBean.getFieldList()) {
                    handleData+="$(\"#"+fieldBean.getName()+"\").attr(\"value\",data."+fieldBean.getName()+");";

/**
 *  //往表单容器中添加
 *                 $("#name").attr("value",data.name);
 *                 $("#parentId").attr("value",data.parentId);
 *                 $("#id").attr("value",data.id);
 *             }
 *         })
 *
 *     </script>
 */

if(fieldBean.isPrimaryKey()){continue;}
body+=" <div class=\"layui-form-item\">\n" +
        "        <label class=\"layui-form-label\">"+fieldBean.getAlias()+"</label>\n" +
        "        <div class=\"layui-input-block\">\n" +
        "            <input id=\""+fieldBean.getName()+"\" type=\"text\" name=\"id\" required=\"\" lay-verify=\"required\" placeholder=\"请输入"+fieldBean.getAlias()+"\" autocomplete=\"off\"\n" +
        "                   class=\"layui-input\">\n" +
        "        </div>\n" +
        "    </div>";
                }
                body+=" <div class=\"layui-form-item\">\n" +
                        "        <div class=\"layui-input-block\">\n" +
                        "            <button class=\"layui-btn\" lay-submit=\"\" lay-filter=\"formDemo\">立即提交</button>\n" +
                        "            <button type=\"reset\" class=\"layui-btn layui-btn-primary\">重置</button>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</form>\n" +
                        "</body>\n" +
                        "</html>\n";
                handleData+="</script>\r\n</head>";
                append(f,before);
                append(f,handleData);
                append(f,body);
            }
        }

    }

    //生成controller
    private void handleController() {
        for (String str : tables) {

            String path = getRelativePath(this.controllerDir) + "\\";
            if (controllerDir != null && controllerDir != "") {
                path = getModulePath(controllerDir, artifactId + "." + projectName + ".controller.");
            }

            File file = new File(path + toCamelCase(1, str) + "Controller.java");

            System.out.println("          <<<" + file.getAbsolutePath());

            setFile(file);
            append(file, "package " + artifactId + "." + projectName + ".controller;\r\n");
            append(file, "import com.github.pagehelper.PageInfo;");
            append(file, "import org.springframework.web.bind.annotation.RequestParam;");
            append(file, "import org.springframework.web.bind.annotation.ResponseBody;\r\n");
            append(file, "import java.util.List;\r\n");
            append(file, "import org.springframework.stereotype.Controller;\r\n" +
                    "import org.springframework.web.bind.annotation.RequestMapping;\r\n");

            append(file, "import org.springframework.beans.factory.annotation.Autowired;\r\n");
            append(file, "import " + artifactId + "." + projectName + ".service.I" + toCamelCase(1, str) + "Service;\r\n");
            append(file, "import " + artifactId + "." + projectName + ".bean." + toCamelCase(1, str) + ";\r\n");
            append(file, "@Controller \r\n");
            append(file, "@RequestMapping(\"/" + str + "\") \r\n");
            append(file, "public class " + toCamelCase(1, str) + "Controller {\r\n");
            append(file, "@Autowired\r\n");
            append(file, "I" + toCamelCase(1, str) + "Service " + toCamelCase(0, str) + "Service;\r\n");

            //生成删除
            // TODO: 2018/7/13

            for (FieldBean fieldBean : map.get(str).getFieldList()) {
                if (fieldBean.isPrimaryKey) {
                    append(file, "\r\n@ResponseBody\r\n");
                    append(file, "@RequestMapping(\"/delete_" + str + "\")\r\n");
                    append(file, "public void delete(" + fieldBean.getType() + "[] " + toCamelCase(0, fieldBean.getName()) + "s){\r\n");
                    append(file, toCamelCase(0, str) + "Service.delete(" + fieldBean.getName() + "s);\r\n}");



                    //生成根据id获得对象的方法

                    append(file,"\r\n@ResponseBody");
                    append(file, "@RequestMapping(\"/get_" + str + "_by_id\")\r\n");
                    append(file,"public "+toCamelCase(1,str)+" get"+toCamelCase(1,str)+"ById("+fieldBean.getType()+" id){");
                    append(file,"return "+toCamelCase(0,str)+"Service.get"+toCamelCase(1,str)+"(id);\r\n}");
                }
            }


            append(file, "\r\n@ResponseBody\r\n");
            append(file, "@RequestMapping(\"/modify_" + str + "\")\r\n");
            //生成修改
            append(file, "\r\npublic void update(" + toCamelCase(1, str) + " " + toCamelCase(0, str) + "){\r\n");
            append(file, toCamelCase(0, str) + "Service.update(" + toCamelCase(0, str) + ");\r\n}");


            append(file, "\r\n@ResponseBody\r\n");
            append(file, "@RequestMapping(\"/get_" + str + "\")\r\n");
            //生成查询
            append(file, "\r\npublic List<" + toCamelCase(1, str) + "> getAll(){\r\n");
            append(file, "return " + toCamelCase(0, str) + "Service.getAll();\r\n}");
            //生成添加


            append(file, "\r\n@ResponseBody\r\n");
            append(file, "@RequestMapping(\"/add_" + str + "\")\r\n");
            append(file, "\r\npublic void add(" + toCamelCase(1, str) + " " + toCamelCase(0, str) + "){\r\n");
            append(file, toCamelCase(0, str) + "Service.insert(" + toCamelCase(0, str) + ");\r\n}");


            //生成获得分页的方法
            append(file, "\r\n@ResponseBody\r\n");
            append(file, "@RequestMapping(\"/get_page_" + str + "\")\r\n");
            append(file, "\r\npublic PageInfo<" + toCamelCase(1, str) + "> getPage(@RequestParam(\"page\")Integer page,@RequestParam(\"pageNum\")Integer pageNum){\r\n");
            append(file, "return " + toCamelCase(0, str) + "Service.getPage(page,pageNum);\r\n}\r\n}");


        }
    }

    //生成service
    private void handleService() {
        //生成接口
        for (String str : tables) {

            String path = getRelativePath(this.serviceDir) + "\\";
            if (serviceDir != null && serviceDir != "") {
                path = getModulePath(serviceDir, artifactId + "." + projectName + ".service.");
            }
            File file = new File(path + "I" + toCamelCase(1, str) + "Service.java");
            setFile(file);
            append(file, "package " + artifactId + "." + projectName + ".service;\r\n");
            append(file, "import com.github.pagehelper.PageInfo;");
            append(file, "import " + artifactId + "." + projectName + ".bean" + "." + toCamelCase(1, str) + ";\r\n");
            append(file, "import java.util.List;\r\n");
            append(file, "public interface I" + toCamelCase(1, str) + "Service {\r\n");


            //添加方法
            append(file, "void insert(" + toCamelCase(1, str) + " " + toCamelCase(0, str) + ");\r\n");
            //删除方法
            for (FieldBean fieldBean : map.get(str).getFieldList()) {
                if (fieldBean.isPrimaryKey) {
                    append(file, "void delete(" + fieldBean.getType() + "[] " + toCamelCase(0, fieldBean.getName()) + "s);\r\n");

                    //根据id获得对象的方法
                    append(file,toCamelCase(1,str)+" get"+toCamelCase(1,str)+"("+fieldBean.getType()+" id);");
                }
            }
            //更新方法
            append(file, "void update(" + toCamelCase(1, str) + " " + toCamelCase(0, str) + ");\r\n");
            //这里还要生成增删改查接口
            append(file, "List<" + toCamelCase(1, str) + "> getAll();\r\n");

            append(file, "PageInfo<" + toCamelCase(1, str) + "> getPage(Integer page,Integer pageNum);\r\n}");



        }


        //生成实现类
        for (String str : tables) {

            String path = getRelativePath(this.serviceDir) + "\\";
            if (serviceDir != null && serviceDir != "") {
                path = getModulePath(serviceDir, artifactId + "." + projectName + ".service.impl.");
            }
            File file = new File(path + toCamelCase(1, str) + "ServiceImpl.java");

            setFile(file);
            append(file, "package " + artifactId + "." + projectName + ".service.impl;\r\n");
            append(file, "import org.springframework.stereotype.Service;\r\n" +
                    "import org.springframework.transaction.annotation.Transactional;\r\n");
            append(file, "import com.github.pagehelper.PageInfo;");
            append(file, "import " + artifactId + "." + projectName + ".service.I" + toCamelCase(1, str) + "Service;\r\n");
            append(file, "import " + artifactId + "." + projectName + ".dao." + toCamelCase(1, str) + "Mapper;\r\n");
            append(file, "import org.springframework.beans.factory.annotation.Autowired;\r\n");
            append(file, "import java.util.List;\r\n");
            append(file, "import " + artifactId + "." + projectName + ".bean." + toCamelCase(1, str) + ";\r\n");
            append(file, "@Service \r\n");
            append(file, "@Transactional \r\n");
            append(file, "public class " + toCamelCase(1, str) + "ServiceImpl implements I" + toCamelCase(1, str) + "Service {\r\n");
            append(file, "@Autowired\r\n");
            append(file, toCamelCase(1, str) + "Mapper " + toCamelCase(0, str) + "Mapper;\r\n");

            //生成删除
            // TODO: 2018/7/13

            for (FieldBean fieldBean : map.get(str).getFieldList()) {
                if (fieldBean.isPrimaryKey) {
                    append(file, "public void delete(" + fieldBean.getType() + "[] " + toCamelCase(0, fieldBean.getName()) + "s){\r\n");
                    append(file, "for(" + fieldBean.getType() + " " + fieldBean.getName() + ":" + toCamelCase(0, fieldBean.getName()) + "s){\r\n");
                    append(file, toCamelCase(0, str) + "Mapper.deleteByPrimaryKey(" + fieldBean.getName() + ");\r\n}\r\n}");


                    //生成根据id获得对象的方法


                    append(file,"public "+toCamelCase(1,str)+" get"+toCamelCase(1,str)+"("+fieldBean.getType()+" id){");
                    append(file,"return "+toCamelCase(0,str)+"Mapper.selectByPrimaryKey(id);\r\n}");
                }
            }

            //生成修改
            append(file, "\r\npublic void update(" + toCamelCase(1, str) + " " + toCamelCase(0, str) + "){\r\n");
            append(file, toCamelCase(0, str) + "Mapper.updateByPrimaryKey(" + toCamelCase(0, str) + ");\r\n}");


            //生成查询
            append(file, "\r\npublic List<" + toCamelCase(1, str) + "> getAll(){\r\n");
            append(file, "return " + toCamelCase(0, str) + "Mapper.selectAll();\r\n}");
            //生成添加

            append(file, "\r\npublic void insert(" + toCamelCase(1, str) + " " + toCamelCase(0, str) + "){\r\n");
            append(file, toCamelCase(0, str) + "Mapper.insert(" + toCamelCase(0, str) + ");\r\n}\r\n");

            //生成查询分页
            append(file, "public PageInfo<" + toCamelCase(1, str) + "> getPage(Integer page,Integer pageNum){\r\n");
            append(file, "List<" + toCamelCase(1, str) + "> all=" + toCamelCase(0, str) + "Mapper.selectAll();\r\n");
            append(file, "return new PageInfo<" + toCamelCase(1, str) + ">(all);\r\n}\r\n}");

        }

    }

    public void handleBean() {

        for (String key : map.keySet()) {
            System.out.println(key);
            //这里创建文件

            String path = getRelativePath(this.beanPackage) + "\\";
            if (beanDir != null && beanDir != "") {
                path = getModulePath(beanDir, artifactId + "." + projectName + ".bean.");
            }

            File file = new File(path + toCamelCase(1, key) + ".java");
            System.out.println(file.getPath() + "----------");
            setFile(file);

            //package 声明
            append(file, "package " + artifactId + "." + projectName + ".bean;\r\n");

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
                    append(file, "@Id\r\n");
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

            String path = getRelativePath(this.daoDir) + "\\";
            if (daoDir != null && daoDir != "") {
                path = getModulePath(daoDir, artifactId + "." + projectName + ".dao.");
            }
            File file = new File(path + toCamelCase(1, str) + "Mapper.java");
            setFile(file);
            append(file, "package " + artifactId + "." + projectName + ".dao;\r\n");
            append(file, "import " + artifactId + "." + projectName + ".bean." + toCamelCase(1, str) + ";\r\n");
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

    public String getModulePath(String moduleName, String modulePackage) {

        String path = modulePackage.replace(".", "/");
        String result = System.getProperty("user.dir") + "/" + moduleName + "/src/main/java/" + path;

        System.out.println("             >>>>" + result + "  模块路径");

        return result;
    }

    public String getWebPath(String dirName) {
        String result = System.getProperty("user.dir") + "/" + controllerDir + "/src/main/" + dirName + "/";
        return result;
    }

    //获取指定类的id
    public String getId(String className) {

        String tableName = convert(className);

        for (Map.Entry<String, ClassBean> entry : map.entrySet()) {
            if (entry.getKey().equals(tableName)) {
                for (FieldBean fieldBean : entry.getValue().getFieldList()) {
                    if (fieldBean.isPrimaryKey()) {
                        return fieldBean.getName();
                    }
                }
            }
        }
        return null;
    }


    /**
     * 驼峰转下划线
     *
     * @param name
     * @return
     */
    private String convert(String name) {

        System.out.println(name + "    需要转换成下划线");

        for (int i = 0; i < name.length(); i++) {
            if (name.substring(i, i + 1).equals(name.substring(i, i + 1).toUpperCase())) {
                //说明当前字符是大写的
                name = name.substring(0, i) + "_" + name.substring(i, i + 1).toLowerCase() + name.substring(i + 1);
            }
        }
        return name;
    }

    /**
     * 判断指定表中的字段是否是主键
     *
     * @param tableName
     * @param fieldName
     * @return
     */
    public boolean isPrimary(String tableName, String fieldName) {
        return false;
    }
}
