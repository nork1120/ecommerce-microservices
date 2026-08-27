package com.company.ecommerce.productservice.converter;


import com.company.ecommerce.productservice.dto.request.UpdateProductRequest;
import com.company.ecommerce.productservice.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductConverter {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "spuId", ignore = true)
    @Mapping(target = "skuCode", ignore = true)
    void updateProduct(UpdateProductRequest request, @MappingTarget Product product);

}