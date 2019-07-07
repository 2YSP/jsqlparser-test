package cn.sp;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;

import java.util.List;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 * Created by 2YSP on 2019/7/6.
 */
public class ParseTest {

  public static final String SELECT_SQL = "select user_name,age,email from t_user " +
      "where user_id > 16546 group by age order by user_name desc";

  public static final String INSERT_SQL = "insert into t_order (id,user_id,sum) values ('EF1243',12,23.6)";

  public static final String UPDATE_SQL = "update person set first_name = 'Fred' where last_name ='Wilson'";

  public static final String DELETE_SQL = "delete from t_item where id = 'AF3434'";

  public static void main(String[] args) throws Exception {
    ParseTest.parseSQL(SELECT_SQL);
    ParseTest.parseSQL(INSERT_SQL);
    ParseTest.parseSQL(UPDATE_SQL);
    ParseTest.parseSQL(DELETE_SQL);

  }

  public static void parseSQL(String sql) throws JSQLParserException {
    Statement statement = CCJSqlParserUtil.parse(sql);
    System.out.println("\n==============sql: " + sql);
    if (statement instanceof Select) {
      Select select = (Select) statement;
      parseSelect(select);
    }
    if (statement instanceof Update) {
      Update update = (Update) statement;
      parseUpdate(update);
    }
    if (statement instanceof Insert) {
      Insert insert = (Insert) statement;
      parseInsert(insert);
    }
    if (statement instanceof Delete) {
      Delete delete = (Delete) statement;
      parseDelete(delete);
    }
  }

  private static void parseDelete(Delete delete) {
    //解析SQL中的表名
    System.out.print("\n表名: ");
    Table table = delete.getTable();
    System.out.print(table.getName());
    //解析SQL中的Where部分
    Expression where = delete.getWhere();
    System.out.print("\nWhere部分: " + where.toString());
  }

  private static void parseInsert(Insert insert) {
    // 获取更新的列名
    System.out.print("\n列名: ");
    List<Column> columns = insert.getColumns();
    if (columns != null) {
      columns.forEach(column -> System.out.print(column.getColumnName() + " "));
    }
    // 解析表名
    System.out.print("\n表名: ");
    String tableName = insert.getTable().getName();
    System.out.print(tableName);

    // 解析insert语句中的插入记录的各个列值
    System.out.print("\n列值:");
    List<Expression> insertValueExpressionList = ((ExpressionList) insert.getItemsList())
        .getExpressions();
    insertValueExpressionList.forEach(expression -> System.out.print(expression.toString() + " "));
    System.out.println();
  }

  private static void parseUpdate(Update update) {
    //解析列名
    System.out.print("\n列名: ");
    List<Column> columns = update.getColumns();
    if (columns != null) {
      columns.forEach(column -> System.out.print(column.getColumnName() + " "));
    }
    // 解析表名
    System.out.print("\n表名: ");
    List<Table> tables = update.getTables();
    tables.forEach(table -> System.out.print(table.getName() + " "));
    // 解析 列值
    System.out.print("\n列值: ");
    List<Expression> expressions = update.getExpressions();
    expressions.forEach(expression -> System.out.print(expression.toString() + " "));
    // 解析where部分
    Expression whereExpression = update.getWhere();
    System.out.print("\nWhere部分: " + whereExpression);
    System.out.println();
  }

  private static void parseSelect(Select select) {
    // 获取select语句查询的列
    System.out.print("\n列名: ");
    PlainSelect plain = (PlainSelect) select.getSelectBody();
    List<SelectItem> selectItems = plain.getSelectItems();
    if (selectItems != null) {
      for (int i = 0; i < selectItems.size(); i++) {
        SelectItem selectItem = selectItems.get(i);
        System.out.print(selectItem.toString() + " ");
      }
    }
    // 解析Select语句中的表名
    System.out.print("\n表名: ");
    TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
    List<String> tableList = tablesNamesFinder.getTableList(select);
    tableList.forEach(tableName -> System.out.print(tableName));

    // 解析SQL语句中的where 部分
    Expression whereExpression = plain.getWhere();
    System.out.print("\nWhere部分: " + whereExpression.toString());
    // 解析SQL语句中的group by 部分
    System.out.print("\nGroup by 部分的列名: ");
    GroupByElement groupByElement = plain.getGroupBy();
    List<Expression> groupByExpressions = groupByElement.getGroupByExpressions();
    if (groupByExpressions != null) {
      groupByExpressions
          .forEach(groupByExpression -> System.out.print(groupByExpression.toString()));
    }

    // 解析SQL语句中的 order by 部分的列名
    System.out.print("\norder by 部分的列名: ");
    List<OrderByElement> orderByElementList = plain.getOrderByElements();
    if (orderByElementList != null) {
      orderByElementList
          .forEach(orderByElement -> System.out.print(orderByElement.getExpression().toString()));
    }
    System.out.println();
  }

  public static void parseSelectJoin(String sql) throws JSQLParserException {
    Statement statement = CCJSqlParserUtil.parse(sql);
    Select select = (Select) statement;
    PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
    List<Join> joinList = plainSelect.getJoins();
    if (joinList != null) {
      for (int i = 0; i < joinList.size(); i++) {
        Join join = joinList.get(i);
        System.out.println("JOIN部分: " + join.toString());
        System.out.println("链接表达式: " + join.getOnExpression().toString());
      }
    }

  }
}
