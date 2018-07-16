/**
 * Created by ZhangJun on 2018/7/14.
 */
public class MenuMapping {
    String tableName;
    String alias;


    public MenuMapping() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public MenuMapping(String tableName, String alias) {
        this.tableName = tableName;
        this.alias = alias;
    }
}
