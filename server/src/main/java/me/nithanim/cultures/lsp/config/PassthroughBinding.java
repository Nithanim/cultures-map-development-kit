package me.nithanim.cultures.lsp.config;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import javax.naming.OperationNotSupportedException;
import lombok.AllArgsConstructor;
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

public class PassthroughBinding<T> implements Binding<Object, T> {
  private final Converter<Object, T> converter;

  public PassthroughBinding(Class<T> clazz) {
    this.converter = new PassthroughConverter<>(clazz);
  }

  @Override
  public Converter<Object, T> converter() {
    return converter;
  }

  @Override
  public void sql(BindingSQLContext<T> ctx) throws SQLException {
    if (ctx.render().paramType() == ParamType.INLINED) {
      throw new SQLException(
          new OperationNotSupportedException(
              "Inlining into SQL was requested but this is impossible for POJOs!"));
    } else {
      ctx.render().sql(ctx.variable()); // Use question mark in sql for binding POJO later
    }
  }

  @Override
  public void register(BindingRegisterContext<T> ctx) throws SQLException {
    ctx.statement()
        .registerOutParameter(
            ctx.index(), Types.OTHER); // Specify data type as other according to H2 spec
  }

  @Override
  public void set(BindingSetStatementContext<T> ctx) throws SQLException {
    PreparedStatement stmt = ctx.statement();
    Object value = ctx.convert(converter()).value();
    stmt.setObject(ctx.index(), value); // Bind POJO directly as parameter
  }

  @Override
  public void get(BindingGetResultSetContext<T> ctx) throws SQLException {
    // Has not been called in my tests so it might be wrong
    ctx.convert(converter()).value(ctx.resultSet().getObject(ctx.index()));
  }

  // Getting a String value from a JDBC CallableStatement and converting that to a JsonElement
  @Override
  public void get(BindingGetStatementContext<T> ctx) throws SQLException {
    // Has not been called in my tests so it might be wrong
    ctx.convert(converter()).value(ctx.statement().getObject(ctx.index()));
  }

  @Override
  public void set(BindingSetSQLOutputContext<T> ctx) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  public void get(BindingGetSQLInputContext<T> ctx) throws SQLException {
    throw new SQLFeatureNotSupportedException();
  }

  @AllArgsConstructor
  private static class PassthroughConverter<T> implements Converter<Object, T> {
    private final Class<T> clazz;

    @SuppressWarnings("unchecked")
    @Override
    public T from(Object t) {
      return (T) t;
    }

    @Override
    public Object to(T u) {
      return u;
    }

    @Override
    public Class<Object> fromType() {
      return Object.class;
    }

    @Override
    public Class<T> toType() {
      return clazz;
    }
  }
}
