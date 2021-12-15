package com.devsuperior.dscatalog.resources;

import static org.mockito.Mockito.mockitoSession;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourcesNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.devsuperior.dscatalog.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	
	private String username; 
	private String password;
	
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	
	@BeforeEach
	void setUp() throws Exception {
		
		username = "maria@gmail.com";
		password = "123456";
		
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		Mockito.when(service.findAllPaged(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(page);
		
		Mockito.when(service.findById(existingId)).thenReturn(productDTO);
		Mockito.when(service.findById(nonExistingId)).thenThrow(ResourcesNotFoundException.class);
		
		Mockito.when(service.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any())).thenReturn(productDTO);
		Mockito.when(service.update(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourcesNotFoundException.class);
	
		Mockito.doNothing().when(service).delete(existingId);
		Mockito.doThrow(ResourcesNotFoundException.class).when(service).delete(nonExistingId);
		Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);
		
		Mockito.when(service.insert(ArgumentMatchers.any())).thenReturn(productDTO);
	
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products")
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(MockMvcResultMatchers.status().isNotFound());
		result.andExpect(jsonPath("$.id").doesNotExist());
	}
	
	@Test
	public void updateShouldUpdateWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
				.header("Authorization", "Bearer" + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(MockMvcResultMatchers.status().isOk());
		result.andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
				.header("Authorization", "Bearer" + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", existingId)
				.header("Authorization", "Bearer" + accessToken)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(MockMvcResultMatchers.status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception { 
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", nonExistingId)
				.header("Authorization", "Bearer" + accessToken)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	public void InsertShouldReturnCreateWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/products")
				.header("Authorization", "Bearer" + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(MockMvcResultMatchers.status().isCreated());
 	}
}
