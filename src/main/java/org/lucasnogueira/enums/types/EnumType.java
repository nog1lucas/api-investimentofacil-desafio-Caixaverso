package org.lucasnogueira.enums.types;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

public class EnumType implements DynamicParameterizedType, UserType<EnumInterface<? extends Serializable>> {

    private Class<EnumInterface<? extends Serializable>> enumClass;

    @Override
    public int getSqlType() {
        return Types.BIGINT;
    }

    @Override
    public Class<EnumInterface<? extends Serializable>> returnedClass() {
        return enumClass;
    }

    @Override
    public boolean equals(EnumInterface x, EnumInterface y) {
        return x == y;
    }

    @Override
    public int hashCode(EnumInterface x) {
        return x == null ? 0 : x.hashCode();
    }

    private Integer getIdNumber(Object id) {
        if (id instanceof Integer) {
            return (Integer) id;
        } else if (id instanceof BigDecimal) {
            BigDecimal bigdec = (BigDecimal) id;
            return bigdec.intValue();
        }
        return null;
    }

    private EnumInterface<? extends Serializable> parseEnumInterface(Object id, Object object) {
        Enum value = (Enum) object;

        if (value instanceof EnumInterface) {

            EnumInterface enumInterface = (EnumInterface) value;

            if(id instanceof BigDecimal) {
                BigDecimal bigId = (BigDecimal) id;

                Integer idEnum = getIdNumber(enumInterface.getId());

                if (idEnum != null && idEnum.equals(bigId.intValue())) {
                    return enumInterface;
                }
            }

            if (id instanceof String) {
                String castId = (String) id;
                if(castId.equalsIgnoreCase(enumInterface.getId().toString())){
                    return enumInterface;
                }
            }
        }
        return null;
    }

    @Override
    public EnumInterface<? extends Serializable> nullSafeGet(ResultSet rs, int index, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws SQLException {
        Object id = rs.getObject(index);
        if (rs.wasNull()) {
            return null;
        }
        for (Object object : returnedClass().getEnumConstants()) {
            EnumInterface enumInterface = parseEnumInterface(id, object);

            if(enumInterface != null) return enumInterface;
        }
        throw new IllegalStateException("Não foi encontrado o enum " + id + " na classe " +  returnedClass().getSimpleName());
    }

    @Override
    public void nullSafeSet(PreparedStatement st, EnumInterface value, int index, SharedSessionContractImplementor sharedSessionContractImplementor) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.BIGINT);
        } else {
            st.setObject(index, ((EnumInterface) value).getId());
        }
    }

    @Override
    public EnumInterface<? extends Serializable> deepCopy(EnumInterface value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(EnumInterface value) {
        return (Serializable) value;
    }

    @Override
    public EnumInterface<? extends Serializable> assemble(Serializable cached, Object o) {
        return (EnumInterface) cached;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        ParameterType params = (ParameterType) parameters.get(PARAMETER_TYPE);
        enumClass = (Class<EnumInterface<? extends Serializable>>) params.getReturnedClass();
    }
}
