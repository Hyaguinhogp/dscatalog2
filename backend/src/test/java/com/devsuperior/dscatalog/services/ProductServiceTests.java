package com.devsuperior.dscatalog.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourcesNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private long categoryExistingId;
	private long categoryNonExistingId;
	private PageImpl<Product> page;
	private Product product;
	private Category category;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		categoryExistingId = 1L;
		categoryNonExistingId = 2L;
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		category = Factory.createCategory();
		
		Mockito.doThrow(ResourcesNotFoundException.class).when(repository).getOne(nonExistingId);
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.find(any(), any(), any())).thenReturn(page);
		
		Mockito.when(repository.save(any())).thenReturn(product);
		
		Mockito.when(repository.findAll((Pageable)any())).thenReturn(page);
		
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DatabaseException.class).when(repository).deleteById(dependentId);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenDoesNotExistsId() {
		
		Assertions.assertThrows(ResourcesNotFoundException.class, () -> {
			service.update(nonExistingId, Factory.createProductDTO());
		});
		
		Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
	}
	
	@Test
	public void updateShouldReturnProductWhenExistsId() {
		
		ProductDTO result = service.update(existingId, Factory.createProductDTO());
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
		Mockito.verify(categoryRepository, Mockito.times(1)).getOne(categoryExistingId);
	}
	
	@Test
	public void findByIdShoudThrowResourceNotFoundExceptionWhenDoesNotExistsId() {
		
		Assertions.assertThrows(ResourcesNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
	}
	
	@Test
	public void findByIdShoudReturnProductDTOWhenIdExists() {
		
		ProductDTO dto = service.findById(existingId);
		
		Assertions.assertNotNull(dto);
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(0L, "", pageable);
		
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void deleteShouldThrowsDataIntegrityViolationExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowsResourcesNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourcesNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
}
