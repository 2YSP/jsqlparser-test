package cn.sp;

import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * Created by 2YSP on 2019/7/6.
 */
public class ParseTest2 {

  public static void main(String[] args) throws JSQLParserException {
    // 使用如下信息拼接成完整的select语句
    String originalSelectSql = "SELECT * FROM t_user ";
    String[] items = {"user_name", "age", "email", "order_id", "sum"};
    String[] tables = {"t_user", "t_order"};
    String where = "user_id > 38647";
    String[] groups = {" age "};
    String[] orders = {" user_name ", " age DESC"};

    createSelect(originalSelectSql, items, tables, where, groups, orders);
  }

  private static void createSelect(String sql, String[] columns, String[] tables,
      String where, String[] groups, String[] orders) throws JSQLParserException {
    // 解析SQL语句，形成Select对象
    Select select = (Select) CCJSqlParserUtil.parse(sql);
    PlainSelect plain = (PlainSelect) select.getSelectBody();
    // 创建查询的列名
    createSelectColumns(plain, columns);
    // 创建查询的表名
    createSelectTables(plain, tables);
    // 创建where子句
    createSelectWhere(plain, where);
    // 创建group by子句
    createSelectGroupBy(plain, groups);
    // 创建order by子句
    createSelectOrderBy(plain, orders);
    // 重置SelectBody
    select.setSelectBody(plain);
    // 输出拼装完成的SQL语句
    System.out.println(select.toString());
  }

  private static void createSelectOrderBy(PlainSelect plain, String[] orders)
      throws JSQLParserException {
    List<OrderByElement> orderByElements = new ArrayList<>();
    for (int i = 0; i < orders.length; i++) {
      OrderByElement orderByElement = new OrderByElement();
      String orderByStr = orders[i];
      String desc = orderByStr.substring(orderByStr.length() - 4, orderByStr.length());
      // 解析order by子句
      Expression orderExpr = CCJSqlParserUtil.parseExpression(orderByStr);
      // 初始化表达式、初始化排序顺序
      orderByElement.setExpression(orderExpr);
      if ("DESC".equals(desc.toUpperCase())) {
        orderByElement.setAsc(false);
      } else {
        orderByElement.setAsc(true);
      }
      // 记录order by子句
      orderByElements.add(orderByElement);
    }
    plain.setOrderByElements(orderByElements);
  }

  private static void createSelectGroupBy(PlainSelect plain, String[] groups)
      throws JSQLParserException {
    GroupByElement groupByElement = new GroupByElement();
    for (int i = 0; i < groups.length; i++) {
      groupByElement.addGroupByExpression(CCJSqlParserUtil.parseExpression(groups[i]));
    }
    plain.setGroupByElement(groupByElement);
  }

  private static void createSelectWhere(PlainSelect plain, String where)
      throws JSQLParserException {
    Expression whereExpr = CCJSqlParserUtil.parseCondExpression(where);
    // 设置where子句
    plain.setWhere(whereExpr);
  }

  private static void createSelectTables(PlainSelect plain, String[] tables)
      throws JSQLParserException {
    // 清空FROM部分
    plain.setFromItem(null);
    // 清空JOIN部分
    plain.setJoins(null);
    if (tables.length == 1) {
      plain.setFromItem(new Table(tables[0]));
    }
    // 如果使用JOIN方式查询多表，则使用JOIN对象进行设置
    // 多表JOIN连接时，使用joins集合记录全部的join对象
    List<Join> joins = new ArrayList<>();
    for (int i = 0; i < tables.length - 1 && tables.length >= 2; ) {
      Join join = new Join();
      if (i == 0) {
        // 设置第一张表
        plain.setFromItem(new Table(tables[0]));
      }
      i++;
      // 这里默认使用内连接
      join.setInner(true);
      // 设置连接表
      join.setRightItem(new Table(tables[i]));
      join.setOnExpression(CCJSqlParserUtil.parseCondExpression("user_id = order_id"));
      // 记录到joins集合中
      joins.add(join);
    }

    plain.setJoins(joins);

  }

  private static void createSelectColumns(PlainSelect plain, String[] columns)
      throws JSQLParserException {
    SelectItem[] selectItems = new SelectItem[columns.length];
    // 清空原有的SQL语句查询的列名
    plain.setSelectItems(null);
    for (int i = 0; i < columns.length; i++) {
      // 将items 转换成SelectItem对象
      selectItems[i] = new SelectExpressionItem(CCJSqlParserUtil.parseExpression(columns[i]));
      plain.addSelectItems(selectItems[i]);
    }
  }
}
