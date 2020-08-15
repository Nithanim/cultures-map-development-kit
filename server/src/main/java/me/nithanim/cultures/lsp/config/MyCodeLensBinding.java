package me.nithanim.cultures.lsp.config;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import javax.naming.OperationNotSupportedException;
import me.nithanim.cultures.lsp.processor.services.clientpersistent.MyCodeLense;
import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.conf.ParamType;

public class MyCodeLensBinding implements Binding<Object, MyCodeLense> {
  private static final Converter<Object, MyCodeLense> CONVERTER = new PassthroughConverter();

  @Override
  public Converter<Object, MyCodeLense> converter() {
    return CONVERTER;
  }

  @Override
  public void sql(BindingSQLContext<MyCodeLense> ctx) throws SQLException {
    if (ctx.render().paramType() == ParamType.INLINED) {
      throw new SQLException(
          new OperationNotSupportedException(
              "Inlining into SQL was requested but this is impossible for POJOs!"));
    } else {
      ctx.render().sql(ctx.variable()); // Use question mark in sql for binding POJO later
    }
  }

  @Override
  public void register(BindingRegisterContext<MyCodeLense> ctx) throws SQLException {
    ctx.statement()
        .registerOutParameter(
            ctx.index(), Types.OTHER); // Specify data type as other according to H2 spec
  }

  @Override
  public void set(BindingSetStatementContext<MyCodeLense> ctx) throws SQLException {
    PreparedStatement stmt = ctx.statement();
    Object value = ctx.convert(converter()).value();
    stmt.setObject(ctx.index(), value); // Bind POJO directly as parameter
  }

  @Override
  public void get(BindingGetResultSetContext<MyCodeLense> ctx) throws SQLException {
    // Has not been called in my tests so it might be wrong
    ctx.convert(converter()).value(ctx.resultSet().getObject(ctx.index()));
  }

  // Getting a String value from a JDBC CallableStatement and converting that to a JsonElement
  @Override
  public void get(BindingGetStatementContext<MyCodeLense> ctx) throws SQLException {
    // Has not been called in my tests so it might be wrong
    ctx.convert(converter()).value(ctx.statement().getObject(ctx.index()));
  }

  @Override
  public void set(BindingSetSQLOutputContext<MyCodeLense> ctx) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public void get(BindingGetSQLInputContext<MyCodeLense> ctx) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  private static class PassthroughConverter implements Converter<Object, MyCodeLense> {
    @Override
    public MyCodeLense from(Object t) {
      return (MyCodeLense) t;
    }

    @Override
    public Object to(MyCodeLense u) {
      return u;
    }

    @Override
    public Class<Object> fromType() {
      return Object.class;
    }

    @Override
    public Class<MyCodeLense> toType() {
      return MyCodeLense.class;
    }
  }
}
