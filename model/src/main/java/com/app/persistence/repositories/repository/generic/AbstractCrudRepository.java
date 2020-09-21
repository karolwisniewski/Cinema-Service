package com.app.persistence.repositories.repository.generic;

import com.app.persistence.model.enums.Category;
import com.app.persistence.model.enums.Role;
import com.app.persistence.model.enums.Status;
import com.app.persistence.model.TicketType;
import com.app.persistence.repositories.connection.DbConnection;
import com.app.persistence.repositories.exception.AbstractCrudRepositoryException;
import com.google.common.base.CaseFormat;
import org.atteo.evo.inflector.English;
import org.jdbi.v3.core.Jdbi;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class AbstractCrudRepository <T, ID> implements CrudRepository<T, ID>{

    protected final Jdbi jdbi = DbConnection.getInstance().getJdbi();

    private final Class<T> modelType = (Class<T>) ((ParameterizedType) (super.getClass().getGenericSuperclass())).getActualTypeArguments()[0];
    private final Class<ID> idType = (Class<ID>) ((ParameterizedType) (super.getClass().getGenericSuperclass())).getActualTypeArguments()[0];
    private final String TABLE_NAME = English.plural(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelType.getSimpleName()));


    @Override
    public Optional<T> add(T item) {
        var SQL = new StringBuilder()
                .append("insert into ")
                .append(TABLE_NAME)
                .append(generateColumnNamesForInsert())
                .append(" values ")
                .append(generateColumnValuesForInsert(item))
                .append(";")
                .toString();
        var result = jdbi.withHandle(handle -> handle.createUpdate(SQL).execute());
        if(result == 0) {
            return Optional.empty();
        }
        return findLast();
    }

    @Override
    public Optional<T> update(T item) {
        var SQL = new StringBuilder()
                .append("update ")
                .append(TABLE_NAME)
                .append(" set ")
                .append(generateColumnNamesAndValuesForUpdate(item))
                .append(" where id = ")
                .append(getId(item))
                .append(";")
                .toString();

        var result = jdbi.withHandle(handle ->
                handle.createUpdate(SQL).execute());
        if(result == 0) {
            return Optional.empty();
        }
        return findById(getId(item));
    }

    @Override
    public Optional<T> findById(ID id) {
        var SQL = "select * from " + TABLE_NAME + " where id = :id ;";
        return jdbi.withHandle(handle ->
                handle.createQuery(SQL)
                .bind("id", id)
                .mapToBean(modelType)
                .findFirst());
    }

    @Override
    public List<T> findAll() {
        var SQL = " select * from " + TABLE_NAME + " ;";
        return jdbi.withHandle(handle -> handle
                .createQuery(SQL)
                .mapToBean(modelType)
                .list());
    }

    @Override
    public void deleteById(ID id) {
        var SQL = "delete from " + TABLE_NAME + " where id = :id ;";
        jdbi.useHandle(handle -> handle
                .createUpdate(SQL)
                .bind("id", id)
                .execute());
    }

    // METODY PRYWATNE

    private String generateColumnNamesForInsert(){
        return
        "( " + Arrays
                .stream(modelType.getDeclaredFields())
                .filter(field -> !field.getName().toLowerCase().equals("id"))
                .map(field -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()))
                .collect(Collectors.joining(", ")) + " )";
    }

    private String generateColumnValuesForInsert(T t){
        return
                "( " +  Arrays
                .stream(modelType.getDeclaredFields())
                .filter(field -> !field.getName().toLowerCase().equals("id"))
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        if(field.getType().equals(String.class)
                                || field.getType().equals(Category.class)
                                || field.getType().equals(Status.class)
                                || field.getType().equals(TicketType.class)
                                || field.getType().equals(Role.class)
                                || field.getType().equals(LocalDate.class)){
                            return "'" + field.get(t) + "'";
                        }
                        else if(field.getType().equals(LocalDateTime.class)){
                            return localDateTimeToCorrectString(field.get(t));
                        }
                        return field.get(t).toString();

                    }catch (Exception e){
                        throw new AbstractCrudRepositoryException(e.getMessage());
                    }
                }).collect(Collectors.joining(", ")) + " ) ";
    }


    private String localDateTimeToCorrectString(Object o){
        return "'" + o.toString().replaceAll("T", " ").concat(":00") + "'";
    }


    private Optional<T> findLast(){
        var SQL = new StringBuilder()
                .append("select * from ")
                .append(TABLE_NAME)
                .append(" order by id desc limit 1;")
                .toString();
        return jdbi.withHandle(handle -> handle
                .createQuery(SQL)
                .mapToBean(modelType)
                .findFirst());
    }

    private String generateColumnNamesAndValuesForUpdate(T t){
        return
        Arrays
                .stream(modelType.getDeclaredFields())
                .filter(field -> !field.getName().toLowerCase().equals("id"))
                .map(field -> {
                    try{
                        field.setAccessible(true);
                        if (field.getType().equals(String.class)
                                || field.getType().equals(Category.class)
                                || field.getType().equals(LocalDate.class)
                                || field.getType().equals(Status.class)) {
                            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()) + " = '" + field.get(t) + "'";
                        } else if(field.getType().equals(LocalDateTime.class)){
                            return localDateTimeToCorrectString(field.get(t));
                        }
                        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()) + " = " + field.get(t).toString();
                    }catch (Exception e){
                        throw new AbstractCrudRepositoryException(e.getMessage());
                    }
                })
                .collect(Collectors.joining(", "));
    }

    private ID getId(T t){
        try {
            Field field = modelType.getDeclaredField("id");
            field.setAccessible(true);
            return (ID) field.get(t);
        }catch (Exception e){
            throw new AbstractCrudRepositoryException(e.getMessage());
        }
    }
}
