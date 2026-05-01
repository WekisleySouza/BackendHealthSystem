package com.project.healthsystem.repository.specs;

import com.project.healthsystem.model.Instituition;
import org.springframework.data.jpa.domain.Specification;

public class InstituitionSpecs {
    public static Specification<Instituition> nameLike(String name){
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("name"),
                name
            );
    }

    public static Specification<Instituition> cepLike(String cep){
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("cep"),
                cep
            );
    }

    public static Specification<Instituition> cityNameLike(String cityName){
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("cityName"),
                cityName
            );
    }

    public static Specification<Instituition> addressLike(String address){
        return (root, query, cb) ->
            SpecsCommon.likeIgnoreCaseUnaccent(
                cb,
                root.get("address"),
                address
            );
    }

    public static Specification<Instituition> phoneLike(String phone){
        return (root, query, cb) ->
                SpecsCommon.likeIgnoreCaseUnaccent(
                        cb,
                        root.get("phone"),
                        phone
                );
    }
}
